package org.example.bot

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import org.example.command.Command
import org.example.handler.CommandHandler
import org.example.handler.LeaveGuildHandler

class Bot(private val token: String, private val owner: Long) {
    private val slashCommandData: ArrayList<SlashCommandData> = ArrayList()
    private var commands: ArrayList<Command> = ArrayList()
    private var jda: JDA? = null

    /**
     * 커맨드를 추가합니다.
     **/
    fun addCommands(vararg commands: Command) {
        this.commands = ArrayList(commands.asList())
        commands.forEach {
            val commandInQueue = Commands.slash(it.name, it.description)

            it.options.forEach { optionData -> // 옵션 추가
                commandInQueue.addOptions(optionData)
            }
            this.slashCommandData.add(commandInQueue)
        }
    }

    fun setPresence(status: OnlineStatus, activity: Activity) {
        this.jda?.presence?.setPresence(status, activity)
    }

    fun updateCommands() {
        val action = jda!!.updateCommands()
        action.addCommands(this.slashCommandData).queue {
            println("명령어 업데이트됨.")
        }
    }

    /**
     * 봇의 작동을 시작합니다.
     * @return JDA의 값을 반환합니다.
     */
    fun run(): JDA? {
        this.jda = JDABuilder.createDefault(this.token)
            .addEventListeners(CommandHandler(owner, commands), LeaveGuildHandler())
            .build()
        return this.jda
    }
}