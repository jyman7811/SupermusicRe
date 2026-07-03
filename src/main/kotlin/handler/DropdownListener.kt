package handler

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.textinput.TextInput
import net.dv8tion.jda.api.components.textinput.TextInputStyle
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.modals.Modal
import org.example.Util
import org.example.controller.ControllerManager
import org.example.controller.Repeat
import java.util.concurrent.TimeUnit

class DropdownListener : ListenerAdapter() {

    // ==========================================
    // 1. 대기열 드롭다운 메뉴 (queue_select) 선택 이벤트
    // ==========================================
    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        if (event.componentId == "queue_select") {
            val index = event.values[0].toIntOrNull() ?: return
            val guild = event.guild ?: return

            val controller = ControllerManager.getController(guild, event.channel)
            val queueList = controller.getQueue().toList()
            val track = queueList.getOrNull(index) ?: return

            val embed = EmbedBuilder()
                .setAuthor("선택한 대기열 곡 정보")
                .setTitle(track.info.title, track.info.uri)
                .setDescription("**채널:** ${track.info.author}\n**길이:** `${Util.convertTime(track.duration)}`")
                .setThumbnail("http://i.ytimg.com/vi/${track.identifier}/hqdefault.jpg")
                .setColor(Util.getRandomColor())
                .build()

            event.replyEmbeds(embed)
                .setEphemeral(true)
                .setComponents(
                    ActionRow.of(
                        Button.success("q_play_$index", "▶️ 즉시 재생"),
                        Button.primary("q_front_$index", "⬆️ 1순위로"),
                        Button.secondary("q_back_$index", "⬇️ 맨 뒤로"),
                        Button.secondary("q_insert_$index", "🔍 곡 삽입"),
                        Button.danger("q_remove_$index", "🗑️ 제거")
                    )
                ).queue()
        }
    }

    // ==========================================
    // 2. 버튼 클릭 이벤트 (플레이어 조작 & 대기열 큐 버튼)
    // ==========================================
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val id = event.componentId
        val guild = event.guild ?: return

        val controllerExist = ControllerManager.isExist(guild.idLong)
        if (!controllerExist) {
            event.reply("플레이어가 활성화되어 있지 않습니다.")
                .setEphemeral(true)
                .queue { event.hook.deleteOriginal().queueAfter(10, TimeUnit.SECONDS) }
            return
        }

        val controller = ControllerManager.getController(guild, event.channel)

        when (id) {
            "player_pause_play" -> {
                val isPaused = controller.getPaused()
                controller.setPaused(!isPaused)

                val msg = if (!isPaused) "⏸️ 음악을 일시정지했습니다." else "▶️ 음악을 다시 재생합니다."

                event.editButton(
                    event.button.withLabel(if (!isPaused) "▶️ 재생" else "⏸️ 일시정지")
                ).queue()

                event.channel.sendMessage(msg)
                    .queue { it.delete().queueAfter(5, TimeUnit.SECONDS) }
                return
            }

            "player_skip" -> {
                controller.skip()
                event.reply("⏭️ 곡을 스킵했습니다!").queue {
                    event.hook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
                }
                return
            }

            "player_loop" -> {
                controller.repeat = when (controller.repeat.status) {
                    0 -> Repeat.REPEAT
                    1 -> Repeat.REPEAT_ONE
                    else -> Repeat.NONE
                }

                val statusStr = when (controller.repeat.status) {
                    1 -> "전체 반복 🔁"
                    2 -> "한 곡 반복 🔂"
                    else -> "반복 비활성화 ➡️"
                }

                event.reply("반복 모드가 **$statusStr**로 변경되었습니다.")
                    .queue { event.hook.deleteOriginal().queueAfter(1, TimeUnit.MINUTES) }
                return
            }

            "player_stop" -> {
                controller.getQueue().clear()
                controller.fuck("⏹️ 모든 대기열을 지우고 재생을 종료했습니다.")
                event.reply("정지 명령을 실행했습니다.")
                    .setEphemeral(true)
                    .queue { event.hook.deleteOriginal().queueAfter(5, TimeUnit.SECONDS) }
                return
            }
        }

        if (id.startsWith("q_")) {
            val parts = id.split("_")
            val action = parts[1]
            val index = parts[2].toIntOrNull() ?: return

            if (action == "insert") {
                val input = TextInput.create("search_query", TextInputStyle.SHORT)
                    .setPlaceholder("여기에 곡 제목이나 링크를 입력하세요.")
                    .setRequired(true)
                    .build()

//                val modal = Modal.create("modal_insert_$index", "음악 검색 및 삽입")
//                    .addComponents(ActionRow.of(input))
//                    .build()
//
//                event.replyModal(modal).queue()
                return
            }

            var trackName = ""
            when (action) {
                "play" -> trackName = controller.playTrackNow(index)?.info?.title ?: ""
                "front" -> trackName = controller.moveTrackToFront(index)?.info?.title ?: ""
                "back" -> trackName = controller.moveTrackToBack(index)?.info?.title ?: ""
                "remove" -> trackName = controller.removeTrackAt(index)?.info?.title ?: ""
//                "insert" -> {// 💡 3. 곡 삽입 버튼 처리 (Modal 입력 창 띄우기)
//                    if (action == "insert") {
//                        // 1. 말씀하신 대로 라벨 없이 2개의 파라미터만 넣는 create를 사용합니다.
//                        val input = TextInput.create("search_query", TextInputStyle.SHORT)
//                            .setPlaceholder("여기에 곡 제목이나 링크를 입력하세요.")
//                            .setRequired(true)
//                            .build()
//
//                        // 2. ActionRow.of()나 addActionRow를 절대 쓰지 않고, addComponents에 직접 넣습니다!
//                        val modal = Modal.create("modal_insert_$index", "음악 검색 및 삽입")
//                            .addComponents(input)
//                            .build()
//
//                        event.replyModal(modal).queue()
//                        return // 모달을 띄운 후 버튼 이벤트는 종료
//                    }}
            }

            if (trackName.isNotEmpty()) {
                event.editMessage(trackName)
                    .setEmbeds(emptyList())
                    .setComponents(emptyList())
                    .queue { event.hook.deleteOriginal().queueAfter(1, TimeUnit.MINUTES) }
            } else {
                event.editMessage("❌ 이미 제거되었거나 처리할 수 없는 곡입니다.")
                    .setEmbeds(emptyList())
                    .setComponents(emptyList())
                    .queue { event.hook.deleteOriginal().queueAfter(10, TimeUnit.SECONDS) }
            }
        }
    }

    // ==========================================
    // 3. 모달 텍스트 입력 제출 이벤트 (실제 곡 삽입 처리)
    // ==========================================
    override fun onModalInteraction(event: ModalInteractionEvent) {
        val id = event.modalId

        if (id.startsWith("modal_insert_")) {
            val index = id.split("_")[2].toIntOrNull() ?: return
            val query = event.getValue("search_query")?.asString ?: return
            val guild = event.guild ?: return

            val controller = ControllerManager.getController(guild, event.channel)

            event.deferReply(true).queue()

            val searchPrefix = if (query.startsWith("http")) "" else "ytsearch:"

            ControllerManager.playerManager.loadItemOrdered(controller, searchPrefix + query, object : AudioLoadResultHandler {
                override fun trackLoaded(track: AudioTrack) {
                    track.userData = event.user
                    controller.insertTrackAfter(index, track)

                    event.hook.editOriginal("✅ **${track.info.title}**을(를) 선택한 곡 바로 뒤에 성공적으로 삽입했습니다.")
                        .queue { it.delete().queueAfter(1, TimeUnit.MINUTES) }
                }

                override fun playlistLoaded(playlist: AudioPlaylist) {
                    val track = playlist.selectedTrack ?: playlist.tracks.firstOrNull()
                    if (track != null) {
                        track.userData = event.user
                        controller.insertTrackAfter(index, track)

                        event.hook.editOriginal("✅ **${track.info.title}**을(를) 선택한 곡 바로 뒤에 성공적으로 삽입했습니다.")
                            .queue { it.delete().queueAfter(10, TimeUnit.SECONDS) }
                    } else {
                        event.hook.editOriginal("❌ 검색 결과를 찾을 수 없습니다.")
                            .queue { it.delete().queueAfter(10, TimeUnit.SECONDS) }
                    }
                }

                override fun noMatches() {
                    event.hook.editOriginal("❌ '$query'에 대한 검색 결과가 없습니다.")
                        .queue { it.delete().queueAfter(10, TimeUnit.SECONDS) }
                }

                override fun loadFailed(exception: FriendlyException) {
                    event.hook.editOriginal("❌ 곡을 불러오는 중 오류가 발생했습니다: ${exception.message}")
                        .queue { it.delete().queueAfter(10, TimeUnit.SECONDS) }
                }
            })
        }
    }
}