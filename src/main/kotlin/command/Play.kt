package org.example.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.example.controller.ControllerManager
import org.example.Util

class Play : Command() {

    override val name: String = "play"
    override var isOwnerOnly: Boolean = false
    override val options: Array<OptionData> = arrayOf(OptionData(OptionType.STRING, "검색어", "Youtube에서 검색할 내용입니다."))
    override val description: String = "노래를 재생합니다."


    override fun run(event: CommandEvent) {
        Checker
            .checkIsInGuild(event)
            .checkIsInVoice(event)
            .checkIsOptionFilled(event, "검색어", "검색어를 입력해야 노래를 찾을 수 있어요!")


        val option = event.getOption("검색어")!!
        val controller = event.controller
        controller!!.join(event.member!!.voiceState!!.channel!!.asVoiceChannel())
        controller.play(option.asString, event.user)
    }
}