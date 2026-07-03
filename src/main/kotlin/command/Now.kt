package org.example.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.example.Util
import org.example.controller.Controller
import org.example.controller.ControllerManager
import java.util.concurrent.TimeUnit

class Now : Command() {
    override val name: String = "now"
    override val isOwnerOnly: Boolean = false
    override val description: String = "현재 재생 중인 노래의 세부 정보와 플레이어를 표시합니다."
    override val options: Array<OptionData> = emptyArray()

    override fun run(event: CommandEvent) {
        Checker
            .checkIsInVoice(event)
            .checkControllerExsit(event)

        if (!ControllerManager.isExist(event.guild!!.idLong)) {
            return event.reply("음악은 틀고 얘기합시다..").queue {
                it.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
            }
        }

        val controller: Controller = event.controller!!
        val playingTrack: AudioTrack = controller.getPlayingTrack()
            ?: return event.reply("지금 재생 중인 노래가 없네요! `/play` 명령으로 음악을 재생해보세요!").queue {
                it.deleteOriginal().queueAfter(5, TimeUnit.SECONDS)
            }

        // 1. 시간 및 진행률 바 계산
        val position = playingTrack.position
        val duration = playingTrack.duration
        val progressString = buildProgressBar(position, duration)

        val currentPosStr = if (Util.convertTime(position) == "") "00:00" else Util.convertTime(position)
        val durationStr = Util.convertTime(duration)

        // 2. 반복 상태 텍스트화
        val repeatStatus = when (controller.repeat.status) {
            1 -> "전체 반복 🔁"
            2 -> "한 곡 반복 🔂"
            else -> "비활성화 ➡️"
        }

        // 3. 대기열의 다음 곡 정보 가져오기
        val nextTrack = controller.getQueue().peek()
        val nextTrackInfo = if (nextTrack != null) nextTrack.info.title else "없음"

        // 4. 화려한 Embed 빌드
        val embed: MessageEmbed = EmbedBuilder()
            .setAuthor("🎧 현재 재생 중인 음악")
            .setTitle(playingTrack.info.title, playingTrack.info.uri)
            .setDescription("**${playingTrack.info.author}**\n\n" +
                    "`$currentPosStr` $progressString `$durationStr`")
            .setThumbnail("http://i.ytimg.com/vi/${playingTrack.identifier}/hqdefault.jpg") // 0.jpg 대신 hqdefault가 화질이 더 좋습니다.
            .addField("👤 요청자", (playingTrack.userData as User).asMention, true)
            .addField("🔄 반복 상태", repeatStatus, true)
            .addField("🎵 다음 곡", nextTrackInfo, false)
            .setColor(Util.getRandomColor())
            .setFooter("요청 시간", event.user.effectiveAvatarUrl)
            .setTimestamp(java.time.Instant.now())
            .build()

        // 5. 인터랙티브 버튼 추가 (ActionRow)
        // 버튼 ID는 봇의 이벤트 리스너에서 식별할 수 있도록 임의로 지정했습니다.
        val buttons = ActionRow.of(
            Button.secondary("player_pause_play", if (controller.getPaused()) "▶️ 재생" else "⏸️ 일시정지"),
            Button.secondary("player_skip", "⏭️ 스킵"),
            Button.secondary("player_loop", "🔁 반복 모드"),
            Button.danger("player_stop", "⏹️ 정지")
        )

        // 이전에 수정한 방식대로 setComponents를 사용하여 버튼 부착
        event.replyEmbeds(embed).setComponents(buttons).queue {
            it.deleteOriginal().queueAfter(30, TimeUnit.SECONDS)
        }
    }

    /**
     * 시각적인 프로그레스 바(진행률 바)를 문자열로 생성하는 유틸리티 함수입니다.
     */
    private fun buildProgressBar(position: Long, duration: Long, length: Int = 15): String {
        if (duration == 0L) return "🔘" + "▬".repeat(length - 1)

        val percent = position.toDouble() / duration.toDouble()
        val progress = (percent * length).toInt().coerceIn(0, length - 1)

        val builder = StringBuilder()
        for (i in 0 until length) {
            if (i == progress) builder.append("🔘")
            else builder.append("▬")
        }
        return builder.toString()
    }
}