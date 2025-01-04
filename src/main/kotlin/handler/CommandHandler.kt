package org.example.handler

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.example.command.Command
import org.example.command.CommandEvent
import org.example.controller.ControllerManager
import org.example.exception.CommandException


class CommandHandler(private val owner: Long, private val commands: ArrayList<Command>, val commandSlash: ArrayList<SlashCommandData>) : ListenerAdapter() {
    private val exceptionListener: ExceptionListener = ExceptionListener()

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val commandName = event.name
        val commandEvent = CommandEvent(event.jda, event.responseNumber, event.interaction)

        commands.forEach {
            if (it.name == commandName) {
                if (!it.isOwnerOnly || (this.owner.toString() == event.user.id)) {
                    // 오류 핸들링
                    try {
                        it.run(commandEvent)
                    } catch (e: CommandException) {
                        exceptionListener.handle(e, commandEvent)
                    }
                }
            }
        }


    }

    override fun onReady(event: ReadyEvent) {
        println(event.jda.guildCache)
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        val id = event.componentId.split("/")
        if (id[0] == "play") {
            if (id[2] != event.user.id) return
            val controller = ControllerManager.getController(event.jda.getGuildById(id[1])!!, event.messageChannel)
            val track = controller.playURL(event.values[0], event.user)
            track as AudioTrack
            event.editComponents().queue()
            event.message.delete().queue()
            event.channel.sendMessage("${track.info.title}을 대기열에 추가했어.").queue()
        }
    }
}