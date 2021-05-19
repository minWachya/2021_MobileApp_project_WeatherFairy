package com.example.min1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// xml 파일 형식을 data class로 구현
data class WEATHER (val response : RESPONSE)
data class RESPONSE(val header : HEADER, val body : BODY)
data class HEADER(val resultCode : Int, val resultMsg : String)
data class BODY(val dataType : String, val items : ITEMS)
data class ITEMS(val item : List<ITEM>)
// category : 자료 구분 코드, fcstDate : 예측 날짜, fcstTime : 예측 시간, fcstValue : 예보 값
data class ITEM(val category : String, val fcstDate : String, val fcstTime : String, val fcstValue : String)

// retrofit을 사용하기 위한 빌더 생성
private val retrofit = Retrofit.Builder()
    .baseUrl("http://apis.data.go.kr/1360000/VilageFcstInfoService/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object ApiObject {
    val retrofitService: WeatherInterface by lazy {
        retrofit.create(WeatherInterface::class.java)
    }
}


class FragmentHome : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var tvDate : TextView                  // 현재 날짜
    lateinit var tvAreaName : TextView              // 지역명
    lateinit var tvTimes : Array<TextView>          // 현재/다음 시간
    lateinit var tvTemps : Array<TextView>          // 온도
    lateinit var imgWeathers : Array<ImageView>     // 날씨 이미지
    lateinit var imgSearchArea : ImageView          // 지역 찾기 이미지 버튼
    lateinit var tvHumiditys : Array<TextView>      // 습도
    lateinit var tvSkys : Array<TextView>           // 하늘 상태
    lateinit var tvRainRatios : Array<TextView>     // 강수 확률
    lateinit var tvRainTypes : Array<TextView>      // 강수 형태
    lateinit var tvRecommends : Array<TextView>     // 기본 옷 추천

    var base_date = ""          // 발표 일자
    var base_time = ""          // 발표 시각
    var nx = "60"               // 예보지점 X 좌표(덕성여대)
    var ny = "129"              // 예보지점 Y 좌표(덕성여대)
    var areaName = "쌍문동"      // 사용자가 설정한 위치(덕성여대)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            nx = it.getString("nx").toString()
            ny = it.getString("ny").toString()
            areaName = it.getString("areaName").toString()
            Log.d("mmm 받은 데이터", nx + "," + ny + ", " + areaName)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tvDate = view.findViewById(R.id.tvDate)
        tvAreaName = view.findViewById(R.id.tvAreaName)
        tvTimes = arrayOf(view.findViewById(R.id.tvTime), view.findViewById(R.id.tvTime2))
        tvTemps = arrayOf(view.findViewById(R.id.tvTemp), view.findViewById(R.id.tvTemp2))
        imgWeathers = arrayOf(view.findViewById(R.id.imgWeather), view.findViewById(R.id.imgWeather2))
        imgSearchArea = view.findViewById(R.id.imgSearchArea)
        tvHumiditys = arrayOf(view.findViewById(R.id.tvHumidity), view.findViewById(R.id.tvHumidity2))
        tvSkys = arrayOf(view.findViewById(R.id.tvSky), view.findViewById(R.id.tvSky2))
        tvRainRatios = arrayOf(view.findViewById(R.id.tvRainRatio), view.findViewById(R.id.tvRainRatio2))
        tvRainTypes = arrayOf(view.findViewById(R.id.tvRainType), view.findViewById(R.id.tvRainType2))
        tvRecommends = arrayOf(view.findViewById(R.id.tvRecommend), view.findViewById(R.id.tvRecommend2))

        // 날짜 초기화
        setDate()

        // nx, ny지점의 날씨 가져와서 설정하기
        setWeather(0, nx, ny)       // 현재 시간대 날씨 설정
        setWeather(1, nx, ny)       // 다음 시간대 날씨 설정
        tvAreaName.text = areaName         // 지역 이름 설정

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentHome().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // 날짜 설정하기
    fun setDate() {
        // 현재 날짜 텍스트뷰에 보이기
        val cal = Calendar.getInstance()
        // 현재 날짜
        var date = SimpleDateFormat("M/dd", Locale.getDefault()).format(cal.time)
        // 현재 요일
        var dayInt = cal.get(Calendar.DAY_OF_WEEK)
        var day = "?"
        when (dayInt) {
            1 -> day = "일"
            2 -> day = "월"
            3 -> day = "화"
            4 -> day = "수"
            5 -> day = "목"
            6 -> day = "금"
            7 -> day = "토"
        }
        tvDate.text = date + "(" + day + ")"
    }

    // 날씨 가져와서 설정하기
    fun setWeather(index : Int, nx : String, ny : String) {
        // 준비 단계 : base_date(발표 일자), base_time(발표 시각)
        // 현재 날짜, 시간 정보 가져오기
        val cal = Calendar.getInstance()
        base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time) // 현재 날짜
        val time = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 시간
        // API 가져오기 적당하게 변환
        base_time = getTime(index, time)
        // 동네예보  API는 3시간마다 현재시간+4시간 뒤의 날씨 예보를 알려주기 때문에
        // 현재 시각이 00시가 넘었다면(=base_time이 2000이상이라면) 어제 예보한 데이터를 가져와야함
        if (index == 0) {
            if (base_time == "2000" || base_time == "2300") {
                cal.add(Calendar.DATE, -1).toString()
                base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
            }
        }
        else {
             if (base_time == "2300") {
                cal.add(Calendar.DATE, -1).toString()
                base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
            }
        }
        Log.d("mmm basetime", "${index}, " + base_time)
        Log.d("mmm basetdate", "${index}, " + base_date)

        // 날씨 정보 가져오기
        // (응답 자료 형식-"JSON", 한 페이지 결과 수 = 10, 페이지 번호 = 1, 발표 날싸, 발표 시각, 예보지점 좌표)
        val call = ApiObject.retrofitService.GetWeather("JSON", 10, 1, base_date, base_time, nx, ny)

        // 비동기적으로 실행하기
        call.enqueue(object : retrofit2.Callback<WEATHER> {
            // 응답 성공 시
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful) {
                    // 날씨 정보 가져오기
                    var it: List<ITEM> = response.body()!!.response.body.items.item

                    var temp = ""           // 기온
                    var humidity = ""       // 습도
                    var sky = ""            // 하능 상태
                    var rainRatio = ""      // 강수 확률
                    var rainType = ""       // 강수 형태
                    for (i in 0..9) {
                        when(it[i].category) {
                            "T3H" -> temp = it[i].fcstValue         // 기온
                            "REH" -> humidity = it[i].fcstValue     // 습도
                            "SKY" -> sky = it[i].fcstValue          // 하늘 상태
                            "POP" -> rainRatio = it[i].fcstValue    // 강수 기온
                            "PTY" -> rainType = it[i].fcstValue     // 강수 형태
                            else -> continue
                        }

                    }
                    // 날씨 정보 텍스트뷰에 보이게 하기
                    setWeather(index, temp, humidity, sky, rainRatio, rainType, time)

                    Toast.makeText(context, "${index}, baseTime = ${base_time}, baseDate = ${base_date}, fcstTime = ${it[0].fcstTime}의 날씨 정보입니다.", Toast.LENGTH_SHORT).show()
                }
            }

            // 응답 실패 시
            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                Log.d("api fail", t.message.toString())
            }
        })
    }

    // 텍스트 뷰에 날씨 정보 보여주기
    fun setWeather(index : Int, temp : String, humidity : String, sky : String, rainRatio : String, rainType : String, time : String) {
        // 온도
        tvTemps[index].text = temp
        // 습도
        tvHumiditys[index].text = humidity + "%"
        // 하늘 상태
        var resultText = ""
        var resultImg = R.drawable.sun
        when(sky) {
            "1" ->  {
                resultText = "맑음"
                resultImg = R.drawable.sun
            }
            "3" -> {
                resultText = "구름 많음"
                resultImg = R.drawable.very_cloudy
            }
            "4" -> {
                resultText = "흐림"
                resultImg = R.drawable.cloudy
            }
            else -> "오류"
        }
        tvSkys[index].text = resultText
        // 강수 확률
        tvRainRatios[index].text = rainRatio + "%"
        // 강수 형태
        resultText = ""
        when(rainType) {
            "0" -> resultText = "없음"
            "1" -> {
                resultText = "비"
                resultImg = R.drawable.rainy
            }
            "2" -> {
                resultText = "비/눈"
                resultImg = R.drawable.snowy_and_rainy
            }
            "3" -> {
                resultText = "눈"
                resultImg = R.drawable.snowy
            }
            "4" -> {
                resultText = "소나기"
                resultImg = R.drawable.rainy
            }
            "5" -> {
                resultText = "빗방울"
                resultImg = R.drawable.rainy_drop
            }
            "6" -> {
                resultText = "빗방울/눈날림"
                resultImg = R.drawable.snowy_and_rainy
            }
            "7" -> {
                resultText = "눈날림"
                resultImg = R.drawable.snowy
            }
            else -> "오류"
        }
        tvRainTypes[index].text = resultText
        imgWeathers[index].setImageResource(resultImg)
        // 기본 옷 추천
        Log.d("mmm 현재 기온", temp)
        when (temp.toInt()) {
            in 5..8 -> resultText = "울 코트, 가죽 옷, 기모"
            in 9..11 -> resultText = "트렌치 코트, 야상, 점퍼"
            in 12..16 -> resultText = "자켓, 가디건, 청자켓"
            in 17..19 -> resultText = "니트, 맨투맨, 후드, 긴바지"
            in 20..22 -> resultText = "블라우스, 긴팔 티, 슬랙스"
            in 23..27 -> resultText = "얇은 셔츠, 반바지, 면바지"
            in 28..50 -> resultText = "민소매, 반바지, 린넨 옷"
            else -> resultText = "패딩, 누빔 옷, 목도리"
        }
        tvRecommends[index].text = resultText
        // 현재/다음 시간 설정
        if (index == 0) tvTimes[index].text = time
        else {
            var temp = (time.toInt() + 3).toString()
            Log.d("mmm 몇시 날씨?1", "${index}, " + temp)
            if (temp >= "21") temp = "0" + (26 - temp.toInt())
            tvTimes[index].text = temp
            Log.d("mmm 몇시 날씨?2", "${index}, " + temp)
        }

        if (index == 0) {
            // 번들에 담아서 메인 액티비티에 보내기
            val bundle = Bundle()
            bundle.putString("temp", tvTemps[index].text.toString())
            // 메인 액티비티는 Weite 프레그먼트에 데이터를 보냄
            val mActivity = activity as MainActivity
            mActivity.setDataAtWriteFragment(bundle)
        }
    }

    // 시간 설정하기
    // 동네 예보 API는 3시간마다 현재시각+4시간 뒤의 날씨 예보를 보여줌
    // 따라서 현재 시간대의 날씨를 알기 위해서는 아래와 같은 과정이 필요함. 자세한 내용은 함께 제공된 파일 확인
    fun getTime(index : Int, time : String) : String {
        var result = ""
        // 현재 시간대 base_time 정하기
        if (index == 0) {
            when(time) {
                in "00".."02" -> result = "2000"    // 00~02
                in "03".."05" -> result = "2300"    // 03~05
                in "06".."08" -> result = "0200"    // 06~08
                in "09".."11" -> result = "0500"    // 09~11
                in "12".."14" -> result = "0800"    // 12~14
                in "15".."17" -> result = "1100"    // 15~17
                in "18".."20" -> result = "1400"    // 18~20
                else -> result = "1700"             // 21~23
            }
            Log.d("현재 다음 시간대", result)
        }
        // 다음 시간대 base_time 정하기
        else {
            when(time) {
                in "00".."02" -> result = "2300"    // 00~02
                in "03".."05" -> result = "0200"    // 03~05
                in "06".."08" -> result = "0500"    // 06~08
                in "09".."11" -> result = "0800"    // 09~11
                in "12".."14" -> result = "1100"    // 12~14
                in "15".."17" -> result = "1400"    // 15~17
                in "18".."20" -> result = "1700"    // 18~20
                else -> result = "2000"             // 21~23
            }
            Log.d("mmm 다음 시간대", result)
        }

        return result
    }

}