package com.example.min1

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// 미세먼지 정보 가져오기
interface AirPollutionInterface {
    // getVilageFcst : 동네 예보 조회
    @GET("getMinuDustFrcstDspth?serviceKey=edSnzhmFwkaoSFwGnzfI%2FVoqtQcqDM67Uzv%2BQmbp7OkjHCY6j%2B9Pq%2BriPr7jQXagfQA0GRllEZL%2BhWBQSljPIw%3D%3D")

    fun GetAirPollution(@Query("returnType") return_type : String,   // 데이터 표출 방식
                        @Query("numOfRows") num_of_rows : Int,       // 한 페이지 결과 수
                        @Query("pageNo") page_no : Int,              // 페이지 번호
                        @Query("searchDate") search_date : String,   // 조회 날짜
                        @Query("informCode") inform_code : String)   // 통보 코드
            : Call<AIR_POLLUTION>
}