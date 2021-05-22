package com.example.min1


import com.google.firebase.database.Exclude

// 데이터 클래스
// 파이어베이스에 저장할 내용들
data class WeatherMemo (
    var objectId : String,  // 키값(시스템에서 자동 생성)
    var date:String,        // 날짜
    var temp:String,        // 온도
    var top:String,         // 상의
    var bottom:String,      // 하의
    var outer:String,       // 아우터
    var memo:String,        // 메모
    var month:String,       // 월별
    var tempGroup:String,   // 온도별
    var email:String        // 이메일
) {
    // 파이어베이스에 있는 데이터를 가지고와서 리사이클러뷰를 만들 때
    // Memo 맵이라는 자료형으로 저장해주어야함.
    // 데이터 저장 시 맵 형태로 저장하고있기 때문

    // 위의 아이템을 Map으로 만들어주는 함수
    @Exclude
    // 필드명:String, 타입:String(모든 인자는 String)
    fun toMap() : HashMap<String, String> {
        // 해시맵 만들기
        val result : HashMap<String, String> = HashMap()

        // 첫번째 인자의 스트링 부분인  objectID는 objectId와 매칭된다.
        result["objectID"] = objectId
        result["date"] = date
        result["temp"] = temp
        result["top"] = top
        result["bottom"] = bottom
        result["outer"] = outer
        result["memo"] = memo
        result["month"] = month
        result["tempGroup"] = tempGroup
        result["email"] = email

        return result   // 파이어베이스의 DB에 저장 가능한 자료형으로 변환 완료
    }
}