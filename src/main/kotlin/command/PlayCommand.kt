package command

import audio.PlayerManager
import audio.AudioPlayerHandler
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

//!play 명령어 담당
class PlayCommand(val manager: PlayerManager) : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        //bot의 메시지인경우 리턴
        if (event.author.isBot)
            return

        val guild = event.guild
        val message = event.message.contentStripped //string으로 strip된 메시지
        if (message.startsWith("!play")) {
            val music = message.replace("!play", "").trim() //play를 제거한 나머지 argument (url, 제목 ...)
            val parsedArgument = parseMusicArgument(music) //lavaplayer가 알 수 있는 argument
            event.channel.sendMessage("해당 노래가 목록에 추가되었습니다! $parsedArgument").queue()

            //chanel join
            val channel = guild.getVoiceChannelsByName("일반", true)[0]
            val audioManager = guild.audioManager


            //연결
            audioManager.sendingHandler = manager.getHandler()
            audioManager.openAudioConnection(channel)

            //재생 - 큐에 등록
            manager.loadMusic(parsedArgument)


        }
    }

    //URL인경우 그대로, 아닌경우 ytsearch 수행
    private fun parseMusicArgument(argument : String) : String {

            return "ytsearch:$argument"

    }
}