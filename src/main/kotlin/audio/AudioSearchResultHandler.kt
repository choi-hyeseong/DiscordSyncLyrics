package audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import lyrics.LyricsWriter

//음악 로드 결과를 핸들링하는 콜백
class AudioSearchResultHandler(
    private val audioPlayer: AudioPlayer,
    private val musicQueue: MusicQueue,
    private val lyricsWriter: LyricsWriter,
    private val searchQuery: String //검색했을때 쿼리
) : AudioLoadResultHandler {
    override fun trackLoaded(track: AudioTrack) {
        lyricsWriter.addLyrics(searchQuery) //먼저 writer에 추가해줘야 함.
        //일단 로드되면 스타트 해보고, noInterrupt이기 때문에 이미 재생중이라 재생 안되면 큐에 등록
        if (!audioPlayer.startTrack(track, true))
            musicQueue.addMusic(track)


    }

    override fun playlistLoaded(playlist: AudioPlaylist) {
        lyricsWriter.addLyrics(searchQuery)
        val firstTrack = playlist.tracks.first()
        if (!audioPlayer.startTrack(firstTrack, true))
            musicQueue.addMusic(firstTrack)

    }

    override fun noMatches() {
        println("not matched")
    }

    override fun loadFailed(exception: FriendlyException?) {
        println("load failed $exception")
    }
}