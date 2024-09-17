package org.example.controller

import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter

import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import dev.lavalink.youtube.clients.Web
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.managers.AudioManager
import org.example.Util
import java.util.*

class Controller(val player: AudioPlayer, val manager: AudioManager, val channel: MessageChannel) : AudioEventAdapter() {
    val queue: Queue<AudioTrack> = LinkedList()
    var isLoop: Boolean = false
    var isPaused: Boolean = player.isPaused
    var speed: Double = 1.0
        private set

    fun play(url: String, author: User) {
        try {
            setSpeed(1.0)
            val searchResult: AudioItem? = Web()
                .loadSearch(ControllerManager.youtubeAudioSourceManager, ControllerManager.youtubeAudioSourceManager.httpInterfaceManager.`interface`, url)


            searchResult as AudioPlaylist
            if (searchResult.tracks.isEmpty()) return channel.sendMessage("$url 에 대한 검색결과가 없네요..").queue()
            searchResult.tracks[0].userData = author
            queue.add(searchResult.tracks[0])
            channel.sendMessage("대기열에 ${searchResult.tracks[0].info.title} 을 추가하였습니다!").queue()
            if (player.playingTrack == null) player.playTrack(queue.poll())
        } catch (e: RuntimeException) {
            Locale.setDefault(Locale.KOREAN)
            channel.sendMessage("오류로 인해 재생할 수 없습니다! 자세한 내용은 다음을 참고해주세요: \n${e.localizedMessage}").queue()
        }
    }

    fun remove(i: Int) : AudioTrack {
        val track = queue.toList()[i]
        queue.remove(queue.toList()[i])
        return track
    }

    fun getPlayingTrack(): AudioTrack? {
        return player.playingTrack
    }

    fun skip() {
        if (queue.isNotEmpty()) {
            val track = queue.poll()
            player.playTrack(track)
        } else {
            player.stopTrack()
        }
    }

    fun join(channel: VoiceChannel) {
        manager.openAudioConnection(channel)
    }


    fun shuffle() {
        (queue as LinkedList<AudioTrack>).shuffle()
    }

    fun stop() {
        queue.clear()
        player.destroy()
        isLoop = false
        isPaused = false
        channel.sendMessage("모든 대기열을 초기화하고 곡을 취소하였습니다!").queue()
    }

    fun fuck(msg: String) {
        queue.clear()
        player.destroy()
        isLoop = false
        isPaused = false
        manager.closeAudioConnection()
        channel.sendMessage(msg).queue()
    }

    fun setSpeed(speed: Double) {
        this.speed = speed
        player.setFilterFactory { _: AudioTrack?, format: AudioDataFormat, output: UniversalPcmAudioFilter? ->
            val audioFilter =
                TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate)
            audioFilter.speed = speed
            listOf(audioFilter)
        }
    }

    override fun onTrackStart(player: AudioPlayer, track: AudioTrack) {
        if (!isLoop) this.channel.sendMessageEmbeds(
            EmbedBuilder()
            .setTitle("${track.info.author} 의 ${track.info.title}", track.info.uri)
            .setThumbnail("https://img.youtube.com/vi/${track.identifier}/mqdefault.jpg")
            .addField("길이", Util.convertTime(track.duration), false)
            .addField("속도", this.speed.toString(), false)
            .setColor(Util.getRandomColor())
            .build()).queue()
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        when {
            endReason == AudioTrackEndReason.STOPPED -> return
            isLoop -> {
                if (speed != 1.0) {
                    player.stopTrack()
                    player.playTrack(track.makeClone())
                } else {
                    player.playTrack(track.makeClone())
                }
            }
//            queue.isEmpty() -> manager.closeAudioConnection()
            queue.isNotEmpty() -> {
                if (speed != 1.0) {
                    player.stopTrack()
                } else {
                    player.playTrack(queue.poll())
                    this.setSpeed(1.0)
                }
            }
        }
    }
}