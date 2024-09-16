package org.example.handler

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData


/*
 * Works only for Slash-Commands.
 */
abstract class Command {
    /**
     * Name of the command.
     */
    abstract val name: String

    /**
     * Check true if it's owner-only command.
     */
    abstract val isOwnerOnly: Boolean

    /**
     * 슬래쉬 커맨드에 들어갈 설명입니다.
     */
    abstract val description: String


    /**
     * 슬래쉬 커맨드에 들어갈 옵션들을 정합니다.
     */
    abstract val options: Array<OptionData>



    abstract fun run(event: SlashCommandInteractionEvent)
}