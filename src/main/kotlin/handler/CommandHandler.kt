package org.example.handler

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter


class CommandHandler(private val owner: Long, private val commands: ArrayList<Command>) : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val commandName = event.name
        commands.forEach {
            if (it.name == commandName) {
                if (!it.isOwnerOnly || (this.owner.toString() == event.user.id)) {
                    if (event.guild == null) {
                        event.reply("이 봇은 DM에서 사용하실 수 없어요!").queue()
                        return
                    }
                    it.run(event)
                }
            }
        }


    }

}