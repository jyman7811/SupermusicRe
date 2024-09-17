package org.example.handler


import net.dv8tion.jda.api.EmbedBuilder
import org.example.command.CommandEvent
import org.example.exception.*
import java.awt.Color

class ExceptionListener {

    fun handle(exception: CommandException, event: CommandEvent) {
        when (exception) {

            // 길드에 속하지 않았을 때
            is NotInGuildException -> return event.replyEmbeds(
                EmbedBuilder()
                    .setTitle("DM에서는 이 기능을 사용할 수 없어요!")
                    .setDescription("저와 겹치는 서버를 찾는 것이 어떨까요?")
                    .setColor(Color.RED)
                    .build()
            ).queue()


            is NotInVoiceException -> return event.replyEmbeds(
                EmbedBuilder()
                    .setTitle("음성 채널에 먼저 들어가 주세요!")
                    .setDescription("노래는 음성 채널에서 재생 가능합니다.")
                    .setColor(Color.RED)
                    .build()
            ).queue()

            is ControllerNotExsitException -> return event.replyEmbeds(
                EmbedBuilder()
                    .setTitle("에..? 누구세요..?")
                    .setDescription("이 명령어를 사용하려면, 봇이 음성 채널에 접속해있는지, 같은 채팅 채널에서 명령어를 사용했는지 확인하세요!")
                    .setColor(Color.RED)
                    .build()
            ).queue()

            is NotPlayingException -> return event.replyEmbeds(
                EmbedBuilder()
                    .setTitle("먼저 음악을 재생하세요!")
                    .setDescription("이 명령어를 사용하려면, 먼저 음악이 재생 중이어야 해요.")
                    .setColor(Color.RED)
                    .build()
            ).queue()

            is OptionIsEmptyException -> return event.replyEmbeds(
                EmbedBuilder()
                    .setTitle("주어진 옵션을 전부 채워주세요!")
                    .setDescription(exception.reply)
                    .setColor(Color.RED)
                    .build()
            ).queue()
        }
    }
}