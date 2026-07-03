package org.example.command

import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.selections.SelectOption
import net.dv8tion.jda.api.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

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
            .checkIsOptionFilled(event, "검색어", "검색어가 있어야 노래를 찾지요...")


        val option = event.getOption("검색어")!!
        val controller = event.controller
        controller!!.join(event.member!!.voiceState!!.channel!!.asVoiceChannel())


        val arg = option.asString

        if (Util.URLMatch(arg)) {
            val track: AudioItem? = controller.playURL(arg, event.user)
            track?: return event.reply("검색 결과가 없네.").queue()

            try { // 플리일때
                val playlist = track as AudioPlaylist
                return event.reply("${playlist.name}의 곡 ${playlist.tracks.size}개를 대기열에 추가했어.").queue()
            } catch (_: Exception) {
                track as AudioTrack
                return event.reply("${track.info.title}을 대기열에 추가했어.").queue()
            }
        }

        val results = controller.search(arg)
        if (results.isEmpty()) {
            return event.reply("검색 결과가 없네.").queue()
        }


        val selectMenu = StringSelectMenu.create("play/${event.guild!!.id}/${event.user.id}")
        var i = 1
        results.forEach { track ->
            if (i > 5) return@forEach
            selectMenu.addOptions(
                SelectOption.of(track.info.title, track.info.uri)
                    //.withEmoji(Util.numberToEmoji(i))
                    .withDescription("${Util.convertTime(track.duration)}/${track.info.author}")
            )
            i += 1
        }


        event.reply(String.format("`%s`에 대한 **검색결과**야.", arg))
            .setComponents(ActionRow.of(selectMenu.build()))
            .queue();
    }
}