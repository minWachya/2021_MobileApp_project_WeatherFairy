package com.example.min1

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// 결과 xml 파일에 접근해서 정보 가져오기
interface WeatherInterface {
    // getVilageFcst : 동네 예보 조회
    @GET("getVilageFcst?serviceKey=edSnzhmFwkaoSFwGnzfI%2FVoqtQcqDM67Uzv%2BQmbp7OkjHCY6j%2B9Pq%2BriPr7jQXagfQA0GRllEZL%2BhWBQSljPIw%3D%3D")

    fun GetWeather(@Query("dataType") data_type : String,
                   @Query("numOfRows") num_of_rows : Int,
                   @Query("pageNo") page_no : Int,
                   @Query("base_date") base_date : String,
                   @Query("base_time") base_time : String,
                   @Query("nx") nx : String,
                   @Query("ny") ny : String)
            : Call<WEATHER>
}