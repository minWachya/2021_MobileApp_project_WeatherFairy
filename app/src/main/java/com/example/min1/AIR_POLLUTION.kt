package com.example.min1

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 미세먼지 정보
data class AIR_POLLUTION (val response : RESPONSE_AIR_POLLUTION)
data class RESPONSE_AIR_POLLUTION(val header : HEADER_AIR_POLLUTION, val body : BODY_AIR_POLLUTION)
data class HEADER_AIR_POLLUTION(val resultCode : String, val resultMsg : String)
data class BODY_AIR_POLLUTION(val items : List<ITEM_AIR_POLLUTION>)
// informCode : 통보 코드, informGrade : 예보 등급, dataTime : 통보 시간
data class ITEM_AIR_POLLUTION(val informCode : String, val informGrade : String, val dataTime : String)

// retrofit을 사용하기 위한 빌더 생성(미세먼지)
private val retrofit_air_pollutuon = Retrofit.Builder()
        .baseUrl("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

object ApiObjectAirPollutuon {
    val retrofitService: AirPollutionInterface by lazy {
        retrofit_air_pollutuon.create(AirPollutionInterface::class.java)
    }
}