package org.example.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.example.Util

class Queue : Command() {
    override val name: String = "queue"
    override val isOwnerOnly: Boolean = false
    override val description: String = "대기열의 곡들을 확인하고 관리할 수 있습니다."
    override val options: Array<OptionData> = emptyArray()

    override fun run(event: CommandEvent) {
        Checker
            .checkIsInGuild(event)
            .checkIsInVoice(event)
            .checkControllerExsit(event)

        val controller = event.controller!!
        val queueList = controller.getQueue().toList()

        if (queueList.isEmpty()) {
            return event.reply("현재 대기열이 비어있습니다. `/play`로 음악을 추가해보세요!").queue()
        }

        val embed = EmbedBuilder()
            .setTitle("🎧 현재 대기열 (${queueList.size}곡)")
            .setColor(Util.getRandomColor())

        val description = StringBuilder()
        val menuBuilder = StringSelectMenu.create("queue_select")
            .setPlaceholder("관리할 곡을 선택해주세요...")

        val limit = minOf(queueList.size, 25)

        for (i in 0 until limit) {
            val track = queueList[i]
            val title = if (track.info.title.length > 45) track.info.title.substring(0, 42) + "..." else track.info.title
            val duration = Util.convertTime(track.duration)

            // 💡 마크다운을 이용해 제목에 파란색 하이퍼링크를 적용합니다.
            description.append("**${i + 1}.** [${title}](${track.info.uri}) `$duration`\n")

            menuBuilder.addOption("${i + 1}. $title", i.toString())
        }

        if (queueList.size > limit) {
            description.append("\n*...외 ${queueList.size - limit}곡이 더 있습니다.*")
        }

        embed.setDescription(description.toString())

        event.replyEmbeds(embed.build())
            .setComponents(ActionRow.of(menuBuilder.build()))
            .queue()
    }
}