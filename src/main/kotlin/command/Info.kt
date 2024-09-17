package org.example.command

import com.sun.management.OperatingSystemMXBean
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.example.Util
import org.example.controller.ControllerManager
import java.lang.management.ManagementFactory
import kotlin.math.*

class Info : Command() {

    override val name: String = "info"
    override var isOwnerOnly: Boolean = false
    override val description: String = "봇의 정보를 표시합니다."
    override val options: Array<OptionData> = emptyArray()


    override fun run(event: CommandEvent) {
        val jda = event.jda
        val owner = event.jda.getUserById(571651913767059456)!!
        val sysInfo = ManagementFactory.getPlatformMXBean(
            OperatingSystemMXBean::class.java
        )



        jda.restPing.queue {
            val embed = EmbedBuilder()
                .setTitle("**${jda.selfUser.name}**의 정보")
                .setThumbnail(jda.selfUser.avatarUrl)
                .setDescription(
                    """
                    *Bot*
                    **Gateway Ping**: ${jda.gatewayPing}ms
                    **RestAPI Ping**: ${it}ms
                    **Uptime**: ${Util.convertTime(ManagementFactory.getRuntimeMXBean().uptime)}
                    **운영체제 정보**: ${sysInfo.name}
                    **CPU 로드**: ${round(sysInfo.cpuLoad * 1000) / 10}%
                    **Memory 사용량**: ${round((sysInfo.committedVirtualMemorySize / 100000).toDouble()) /10}MB

                    **이 봇을 사용 중인 서버 수**: ${event.jda.guilds.size}개의 서버
                    **이 봇을 사용 중인 유저 수**: ${event.jda.users.size}명의 유저
                    **활성화 된 컨트롤러 수**: ${ControllerManager.controllerCount()}
                    
                    *Code*
                    **openJDK 버전**: ${System.getProperty("java.vm.version")}
                    **Kotlin 버전**: ${KotlinVersion.CURRENT}
                    **사용된 주요 라이브러리**: [Gradle](https://gradle.org/), [JDA](https://github.com/DV8FromTheWorld/JDA), [LavaPlayer](https://github.com/sedmelluq/lavaplayer)
                    
                    **개발자**: ${owner.asTag}
                    [**개발자 Github**](https://github.com/jyman7811)
                    [**Supermusic Rewrite 레포지터리**](https://github.com/jyman7811/SupermusicRe)
                    **현재 사용 중인 언어**: `kotlin`
            """.trimIndent()
                )
                .setFooter("${event.user.asTag}에 의해 요청됨", event.user.avatarUrl)
                .setColor(Util.getRandomColor())
                .build()
            event.replyEmbeds(embed).queue()
        }
    }
}