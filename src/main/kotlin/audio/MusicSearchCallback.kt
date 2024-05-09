// play comman에서 사용하는 콜백
interface MusicSearchCallback() {

    // 만약 현재 방식이 아니라 (검색된 음악중 첫번째꺼 선택), 직접 목록에서 골라야 한다면 onTrackSelection 메소드 추가
    fun onSuccess(track : AudioTrack) 

    fun onFailure(reason : FailReason)
}

enum class FailReason() {
    NO_MATCH, LOAD_FAILED
}