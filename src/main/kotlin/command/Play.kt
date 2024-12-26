package org.example.command

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
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


        val arg = option.asString

        if (Util.URLMatch(arg)) {
            val track = controller.playURL(arg, event.user)
            return event.reply("${track.info.title}을 대기열에 추가했어요!").queue()
        }

        val results = controller.search(arg)
        if (results.isEmpty()) {
            return event.reply("검색 결과가 없어요!").queue()
        }


        val selectMenu = StringSelectMenu.create("play/${event.guild!!.id}/${event.user.id}")
        var i = 1
        results.forEach({track ->
            if (i > 5) return@forEach
            selectMenu.addOptions(SelectOption.of(track.info.title, track.info.uri)
                //.withEmoji(Util.numberToEmoji(i))
                .withDescription("${Util.convertTime(track.duration)}/${track.info.author}")
            )
            i+=1
        })


        event.reply("`${arg}`에 대한 **검색결과**")
            .addActionRow(selectMenu.build())
            .queue()
    }
}