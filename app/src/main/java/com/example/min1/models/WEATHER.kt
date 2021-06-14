package com.example.min1

import com.example.min1.models.WeatherInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 날씨 정보
// xml 파일 형식을 data class로 구현
data class WEATHER (val response : RESPONSE_ITEM_WEATHER)
data class RESPONSE_ITEM_WEATHER(val header : HEADER_ITEM_WEATHER, val body : BODY_WEATHER)
data class HEADER_ITEM_WEATHER(val resultCode : Int, val resultMsg : String)
data class BODY_WEATHER(val dataType : String, val items : ITEMS_WEATHER)
data class ITEMS_WEATHER(val item : List<ITEM_WEATHER>)
// category : 자료 구분 코드, fcstDate : 예측 날짜, fcstTime : 예측 시간, fcstValue : 예보 값
data class ITEM_WEATHER(val category : String, val fcstDate : String, val fcstTime : String, val fcstValue : String)

// retrofit을 사용하기 위한 빌더 생성(날씨)
private val retrofit_weather = Retrofit.Builder()
        .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

object ApiObjectWeather {
    val retrofitService: WeatherInterface by lazy {
        retrofit_weather.create(WeatherInterface::class.java)
    }
}