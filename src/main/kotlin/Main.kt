package org.example


import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import org.example.bot.Bot
import org.example.command.*

import java.nio.file.Files
import java.nio.file.Path


fun main() {

    val token = Files.readString(Path.of(ClassLoader.getSystemResource("config").toURI()))


    println("봇을 실행합니다.\nToken: $token")


    val bot = Bot(
        token, // Bot token
        571651913767059456 // Me
    )

    bot.addCommands(
        Play(),
        Info(),
        Now()
    )

    bot.run()

    bot.updateCommands()
    bot.setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.customStatus("코드 리라이트 중..."))
}