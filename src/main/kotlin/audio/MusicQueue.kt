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

class MusicQueue : AudioEventAdapter() {

    private val queue : Queue<AudioTrack> = LinkedList() //Queue의 구현체로 LinkedList 사용


    //해당 addMusic을 쓰는 클래스는 PlayerManger이므로 audioPlayer 원본을 갖고 있음.
    fun addMusic(audioPlayer: AudioPlayer, audioTrack: AudioTrack) {
        if (!audioPlayer.startTrack(audioTrack, true)) //no interrupt가 되어 있기 때문에 이미 재생중인경우 false를 반환함
            queue.add(audioTrack) //이때 큐에 추가한다.
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