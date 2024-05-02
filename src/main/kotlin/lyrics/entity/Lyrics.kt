package lyrics.entity

import java.util.Queue

data class Lyrics (
    val id : Int, //id
    val name : String, //곡
    val trackName : String, //트랙 이름
    val artistName : String, //아티스트
    val albumName : String, //앨범이름
    val duration : Int, //길이
    val instrumental : Boolean,
    val plainLyrics : String, //순수 가사
    val syncedLyrics : Queue<SyncedLyrics> //실제 사용되는 동기화된 가사
    )

data class SyncedLyrics(
    val time : Long,
    val lyrics : String
)