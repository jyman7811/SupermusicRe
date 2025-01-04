package org.example.command

import net.dv8tion.jda.api.interactions.commands.build.OptionData

class Queue : Command() {
    override val name: String = "queue"
    override val isOwnerOnly: Boolean = false
    override val description: String = "대기열의 곡들을 확인해줄게."
    override val options: Array<OptionData> = emptyArray()

    override fun run(event: CommandEvent) {
        Checker
            .checkIsInGuild(event)
            .checkIsInVoice(event)
            .checkControllerExsit(event)

        val controller = event.controller!!
        if (controller.getQueue().isEmpty()) return event.reply("대기열은 비어있어.").queue()

        event.reply("\n${event.controller!!.getQueue().joinToString("\n") { it.info.title }}").queue()
    }
}