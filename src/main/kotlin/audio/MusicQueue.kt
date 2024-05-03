package audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import jda
import net.dv8tion.jda.api.JDA
import java.util.LinkedList
import java.util.Queue
import kotlin.concurrent.thread


//오디오 플레이어 하나당 하나의 큐가 할당되어야 함
class MusicQueue() : AudioEventAdapter() {

    private val queue : Queue<AudioTrack> = LinkedList() //Queue의 구현체로 LinkedList 사용


    //addMusic의 파라미터로 받는 audioPlayer는 좋지 않음.
    //큐가 담당하는 player와 파라미터로 들어온 audioPlayer가 동일하지 않을 수 있음
    fun addMusic(audioTrack: AudioTrack) {
        queue.add(audioTrack) //큐에 추가한다.
    }

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        jda.getTextChannelsByName("일반", true)[0].sendMessage("노래가 재생됩니다. Track : ${track?.identifier}").queue()

    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason?) {

        //track 종료시
        if (endReason != null && endReason.mayStartNext) {
            //다음꺼 실행 하는거라면
            player?.startTrack(queue.poll(), false) //트랙 실행
        }
    }
}