package org.example.handler

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.example.command.Command
import org.example.command.CommandEvent
import org.example.exception.CommandException


class CommandHandler(private val owner: Long, private val commands: ArrayList<Command>) : ListenerAdapter() {
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

}