package org.example.command

import net.dv8tion.jda.api.interactions.commands.build.OptionData

class Skip : Command() {
    override val name: String = "skip"
    override val isOwnerOnly: Boolean = false
    override val description: String = "대기열의 다음 노래로 넘어갑니다."
    override val options: Array<OptionData> = emptyArray()

    override fun run(event: CommandEvent) {
        Checker
            .checkIsInVoice(event)
            .checkControllerExsit(event)
            .checkIsPlaying(event)

        val controller = event.controller!!
        event.reply("${controller.getPlayingTrack()!!.info.title}을 스킵했어요!").queue()
        controller.skip()
    }
}