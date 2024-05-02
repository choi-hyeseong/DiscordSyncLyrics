package audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import lyrics.LyricsWriter

//음악 재생, 로드, 정지를 담당하는 클래스
class PlayerManager(
    private val playerManager: AudioPlayerManager,
    private val musicQueue: MusicQueue,
    private val lyricsWriter: LyricsWriter
) {
    val audioPlayer: AudioPlayer by lazy {
        playerManager.createPlayer().apply {
            addListener(musicQueue) //큐 등록
            addListener(lyricsWriter) //가사 작성기
        }
    }

    init {
        AudioSourceManagers.registerRemoteSources(playerManager) //resource 등록
    }

    fun getHandler() : AudioPlayerHandler = AudioPlayerHandler(audioPlayer) //음악 재생에 필요한 Handler 제공

    //음악로드, search는 검색할때 사용한 파라미터
    fun loadMusic(identifier: String, searchIdentifier : String) {
        playerManager.loadItem(identifier, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack?) {
                if (track == null)
                    println("해당 트랙을 불러올 수 없습니다.")
                lyricsWriter.addLyrics(searchIdentifier) //먼저 writer에 추가해줘야 함. addMusic의 경우 큐가 비어있으면 바로 재생됨. 따라서 먼저 추가해주는게 맞음
                musicQueue.addMusic(audioPlayer, track!!)

            }

            override fun playlistLoaded(playlist: AudioPlaylist?) {
                lyricsWriter.addLyrics(searchIdentifier)
                musicQueue.addMusic(audioPlayer, playlist!!.tracks[0]!!)

            }

            override fun noMatches() {
                println("not matched")
            }

            override fun loadFailed(exception: FriendlyException?) {
                println("load failed $exception")
            }
        })
    }

    fun stopMusic() {
        audioPlayer.stopTrack()
    }




}