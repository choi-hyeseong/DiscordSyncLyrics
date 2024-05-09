package command

import audio.PlayerManager
import audio.AudioPlayerHandler
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

//!play 명령어 담당
class PlayCommand(private val manager: PlayerManager) : AbstractCommand("!play") {

    override fun onCommand(event: MessageReceivedEvent) {
        val music = getConcatedArgument(1) //play를 제거한 나머지 argument (url, 제목 ...)
        if (music == null)
            // todo no arg given
            return
        
        manager.loadMusic(music, object : MusicSearchCallback {
            override fun onSuccess(track : AudioTrack) {
                event.channel.sendMessage("해당 노래가 목록에 추가되었습니다!").queue()
                joinChannel(event.guild)
            }

            override fun onFailure(reason : FailReason) {
                event.channel.sendMessage("해당 노래를 불러오는중 오류가 발생했습니다.. $reason").queue()
            }
        })
       
    }

    fun joinChannel(guild : Guild) {
         //chanel join
        val channel = guild.getVoiceChannelsByName("일반", true)[0]
        val audioManager = guild.audioManager
        //연결
        audioManager.sendingHandler = manager.getHandler()
        audioManager.openAudioConnection(channel)
    }

}