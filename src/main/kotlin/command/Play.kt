package org.example.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import org.example.handler.Command
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.example.audio.ControllerManager
import org.example.Util

class Play : Command() {

    override val name: String = "play"
    override var isOwnerOnly: Boolean = false
    override val options: Array<OptionData> = arrayOf(OptionData(OptionType.STRING, "검색어", "Youtube에서 검색할 내용입니다."))
    override val description: String = "노래를 재생합니다."


    override fun run(event: SlashCommandInteractionEvent) {
        val searchOption = event.getOption("검색어")

        if (searchOption == null) {
            event.reply("검색어를 입력해주세요!").queue()
        }

        when {
            event.member!!.voiceState == null || !event.member!!.voiceState!!.inAudioChannel() -> return event.replyEmbeds(
                EmbedBuilder()
                .setTitle("아무 음성채널이나 들어가주세요!")
                .setDescription("노래는 음성채널에서 재생이 가능합니다!")
                .setColor(Util.getRandomColor())
                .build()).queue()
            searchOption!!.name.isEmpty() -> return event.replyEmbeds(
                EmbedBuilder()
                .setTitle("검색어를 입력하세요!")
                .setDescription("Youtube 에서 검색하려면 검색 키워드가 있어야 합니다!")
                .setColor(Util.getRandomColor())
                .build()).queue()
        }
        val controller = ControllerManager.getController(event.guild!!, event.guild!!.audioManager, event.channel)
        controller.join(event.member!!.voiceState!!.channel!!.asVoiceChannel())
        controller.play(searchOption!!.asString, event.user)
    }
}