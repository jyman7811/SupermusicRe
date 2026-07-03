package org.example.controller

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import dev.lavalink.youtube.clients.Web
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.managers.AudioManager
import java.util.*


class Controller(private val player: AudioPlayer, private val manager: AudioManager, private val channel: MessageChannel) : AudioEventAdapter() {
    // 대기열
    private val queue: Queue<AudioTrack> = LinkedList()
    var repeat: Repeat = Repeat.NONE
//    var speed: Double = 1.0


    fun setPaused(boolean: Boolean) {
        player.isPaused = boolean
    }

    fun getPaused() : Boolean {
        return player.isPaused
    }

    fun getQueue() : Queue<AudioTrack> {
        return queue
    }

    fun getPlayingTrack(): AudioTrack? {
        return player.playingTrack
    }

    fun join(channel: VoiceChannel) {
        print("debug")
        manager.openAudioConnection(channel)
    }

//    fun disconnect() {
//        player.destroy()
//    }

    fun search(text: String) : List<AudioTrack> {
        val searchResult = Web().loadSearch(ControllerManager.youtubeAudioSourceManager, ControllerManager.youtubeAudioSourceManager.httpInterfaceManager.`interface`, text)
        searchResult as AudioPlaylist
        return searchResult.tracks
    }

    private var isSkipping = false

    fun skip() {
        val currentTrack = player.playingTrack

        // 💡 전체 반복(Repeat All)이 켜져 있다면, 스킵되는 곡을 복사해서 큐의 맨 뒤에 넣습니다.
        if (repeat.status == 1 && currentTrack != null) {
            queue.add(currentTrack.makeClone())
        }

        // 💡 onTrackEnd의 중복 실행을 막기 위해 플래그를 세웁니다.
        isSkipping = true

        val nextTrack = queue.poll()
        if (nextTrack != null) {
            player.playTrack(nextTrack)
        } else {
            player.stopTrack()
        }

        isSkipping = false
    }

    private fun play(track: AudioTrack, author: User) {
        track.userData = author

        queue.add(track)
        if (player.playingTrack == null) player.playTrack(queue.poll())
    }

    fun playURL(uri: String, author: User) : AudioItem? {
        var loadedItem: AudioItem? = null

        ControllerManager.playerManager.loadItemOrdered(player, uri, FunctionalResultHandler(
            { track: AudioTrack -> // track Consumer
                loadedItem = track
                play(track, author)
            },
            { playlist: AudioPlaylist -> // playlist Consumer
                loadedItem = playlist

                var firstTrack = playlist.selectedTrack

                if (firstTrack == null) {
                    firstTrack = playlist.tracks[0]
                }


                play(firstTrack, author)
                val tracks = playlist.tracks
                tracks.remove(firstTrack)
                tracks.forEach {
                    play(it, author)
                }
            },   // empty result Consumer
            {
                println("읎어요")
            },
            {    // exception Consumer
                println("망했어요")
            }
        )).get()
        return loadedItem
    }

    fun fuck(msg: String) {
        manager.closeAudioConnection()
        player.destroy()
        channel.sendMessage(msg)
    }

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {

    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {
        // 💡 스킵 버튼으로 종료되었거나, 강제 정지(STOPPED)된 경우 onTrackEnd 내부 로직을 실행하지 않습니다.
        // (스킵 시 필요한 큐 추가 작업은 위 skip() 메서드 안에서 이미 처리했습니다.)
        if (isSkipping || endReason == AudioTrackEndReason.STOPPED) {
            return
        }

        // --- 자연스럽게 곡이 끝났을 때의 정상적인 반복 로직 ---
        if (repeat.status == 1) { // Repeat all
            queue.add(track!!.makeClone())
        }
        if (repeat.status == 2) { // Repeat one
            player!!.playTrack(track!!.makeClone())
            return
        }

        if (queue.isNotEmpty()) {
            player!!.playTrack(queue.poll())
        }
    }

    // ===== 추가할 대기열 조작 로직 (Controller.kt 내부) =====

    // 특정 인덱스의 곡을 대기열에서 제거하고 반환합니다.
    fun removeTrackAt(index: Int): AudioTrack? {
        val linkedQueue = queue as java.util.LinkedList<AudioTrack>
        if (index < 0 || index >= linkedQueue.size) return null
        return linkedQueue.removeAt(index)
    }

    // 곡을 대기열의 맨 앞으로 이동시킵니다. (다음 곡으로 재생되게 함)
    fun moveTrackToFront(index: Int): AudioTrack? {
        val track = removeTrackAt(index) ?: return null
        val linkedQueue = queue as java.util.LinkedList<AudioTrack>
        linkedQueue.addFirst(track)
        return track
    }

    // 곡을 대기열 맨 뒤로 이동시킵니다.
    fun moveTrackToBack(index: Int): AudioTrack? {
        val track = removeTrackAt(index) ?: return null
        queue.add(track)
        return track
    }

    // 대기열에 있는 곡을 지금 당장 틀어버립니다. (기존 재생 중단)
    fun playTrackNow(index: Int): AudioTrack? {
        val track = removeTrackAt(index) ?: return null
        isSkipping = true // 강제 재생이므로 onTrackEnd 중복 실행 방지
        player.playTrack(track)
        isSkipping = false
        return track
    }

    fun insertTrackAfter(index: Int, track: AudioTrack) {
        val linkedQueue = queue as java.util.LinkedList<AudioTrack>
        // 현재 큐의 크기를 초과하지 않도록 방어 코드 작성 (최대 맨 뒤에 삽입)
        val targetIndex = (index + 1).coerceAtMost(linkedQueue.size)
        linkedQueue.add(targetIndex, track)
    }
}
