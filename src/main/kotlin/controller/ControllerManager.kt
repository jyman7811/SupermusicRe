package org.example.controller

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import dev.lavalink.youtube.YoutubeAudioSourceManager
import dev.lavalink.youtube.clients.AndroidTestsuite
import dev.lavalink.youtube.clients.Music
import dev.lavalink.youtube.clients.Web
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.managers.AudioManager
import org.example.audio.AudioPlayerSendHandler


object ControllerManager {
    private val controllers = HashMap<Long, Controller>()
    private val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()
    val youtubeAudioSourceManager = YoutubeAudioSourceManager( /*allowSearch:*/true, Music(), Web(), AndroidTestsuite())

    init {
        playerManager.registerSourceManager(youtubeAudioSourceManager)
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
        try {
            controllers[guildId]!!.fuck(msg)
            controllers.remove(guildId)
        } catch (e: RuntimeException) {
            return
        }
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