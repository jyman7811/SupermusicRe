package org.example.command

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.example.Util
import org.example.audio.Controller
import org.example.audio.ControllerManager
import org.example.handler.Command

class Now : Command() {
    override val name: String = "now"
    override val isOwnerOnly: Boolean = false
    override val description: String = "현재 재생 중인 노래를 표시합니다."
    override val options: Array<OptionData> = emptyArray()

    override fun run(event: SlashCommandInteractionEvent) {
        if (event.member!!.voiceState == null || !event.member!!.voiceState!!.inAudioChannel()) return event.replyEmbeds(
            EmbedBuilder()
            .setTitle("아무 음성채널이나 들어가주세요!")
            .setDescription("이 기능은 음성채널에 들어가야 사용 가능합니다!")
            .setColor(Util.getRandomColor())
            .build()).queue()
        if (!ControllerManager.musicManager.isExist(event.guild!!.idLong)) return event.reply("음악은 틀고 얘기합시다..").queue()
        val controller: Controller = ControllerManager.getController(event.guild!!, event.guild!!.audioManager, event.channel)
        val playingTrack: AudioTrack = controller.getPlayingTrack() ?: return event.reply("지금 재생 중인 노래가 없네요! +play 명령으로 음악을 재생해보세요!").queue()
        val embed: MessageEmbed = EmbedBuilder()
            .setTitle("${playingTrack.info.author} - ${playingTrack.info.title}", playingTrack.info.uri)
            .setDescription("${Util.convertTime(playingTrack.duration)} **|** ${if (Util.convertTime(playingTrack.position) == "" ) "방금" else Util.convertTime(playingTrack.position)} 재생됨")
            .setThumbnail("http://i.ytimg.com/vi/${playingTrack.identifier}/0.jpg")
            .addField("요청자", (playingTrack.userData as User).asTag, false)
            .addField("반복 ", if (controller.isLoop) "활성화" else "비활성화", false)
            .setColor(Util.getRandomColor())
            .build()
        event.replyEmbeds(embed).queue()
    }
}