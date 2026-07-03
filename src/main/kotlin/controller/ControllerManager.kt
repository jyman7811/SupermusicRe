package org.example.controller

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import dev.lavalink.youtube.YoutubeAudioSourceManager
import dev.lavalink.youtube.clients.*
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.managers.AudioManager
import org.example.audio.AudioPlayerSendHandler


object ControllerManager {
    private val controllers = HashMap<Long, Controller>()
    val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    val youtubeAudioSourceManager = YoutubeAudioSourceManager( /*allowSearch:*/true, MusicWithThumbnail(), WebWithThumbnail(), MWeb(), AndroidMusic(), Tv(), AndroidVr())



    init {
        playerManager.registerSourceManager(youtubeAudioSourceManager)
        Web.setPoTokenAndVisitorData("MnTViYTqpJrpatWxzjQ6xvG2eEZOumCBEWkm5bqPlL6WzkV5Vn302DIjtp9cY1tCD86xofZr3yC59mfKZBkABEl9TDMfGKBUfVRgvlGt8alQw0ldlfDiRvv86qdy3YE3UdQVD9JSy5l8PJbSVzv-SMUFsuLLrA==", "CgtMVnRkcjhhMnZRdyjJ-KO7BjIKCgJLUhIEGgAgGw%3D%3D")
    }

    private fun makeController(manager: AudioManager, guildId: Long, channel: MessageChannel): Controller {

        val player = playerManager.createPlayer()
        player.volume = 35
        manager.sendingHandler = AudioPlayerSendHandler(player)
        val newController = Controller(player, manager, channel)
        controllers[guildId] = newController
        player.addListener(newController)
        return newController
    }

    fun destroy(guildId: Long, msg: String) {
        controllers[guildId]?.fuck(msg)
        controllers.remove(guildId)
    }

    /**
     *
     * @param guild 해당하는 Guild 값입니다.
     *
     */
    fun getController(guild: Guild, channel: MessageChannel) : Controller {
        if (controllers.containsKey(guild.id.toLong())) return controllers[guild.id.toLong()]!!
        return this.makeController(guild.audioManager, guild.idLong, channel)
    }

    fun controllerCount(): Int {
        return controllers.size
    }

    fun isExist(guildId: Long): Boolean {
        return controllers.containsKey(guildId)
    }
}