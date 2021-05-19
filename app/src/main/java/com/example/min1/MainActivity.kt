package com.example.min1

import android.app.Activity
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

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

// 메인 액티비티
class MainActivity : AppCompatActivity() {
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
    lateinit var btnWrite : Button                  // <옷 기록하기> 버튼
    lateinit var btnSeeMemo : Button                // <내 기록보기> 버튼
    lateinit var bottomNavi : BottomNavigationView  // 하단 네비게이션

    var base_date = ""          // 발표 일자
    var base_time = ""          // 발표 시각
    var nx = "60"               // 예보지점 X 좌표(덕성여대)
    var ny = "129"              // 예보지점 Y 좌표(덕성여대)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDate = findViewById(R.id.tvDate)
        tvAreaName = findViewById(R.id.tvAreaName)
        tvTimes = arrayOf(findViewById(R.id.tvTime), findViewById(R.id.tvTime2))
        tvTemps = arrayOf(findViewById(R.id.tvTemp), findViewById(R.id.tvTemp2))
        imgWeathers = arrayOf(findViewById(R.id.imgWeather), findViewById(R.id.imgWeather2))
        imgSearchArea = findViewById(R.id.imgSearchArea)
        tvHumiditys = arrayOf(findViewById(R.id.tvHumidity), findViewById(R.id.tvHumidity2))
        tvSkys = arrayOf(findViewById(R.id.tvSky), findViewById(R.id.tvSky2))
        tvRainRatios = arrayOf(findViewById(R.id.tvRainRatio), findViewById(R.id.tvRainRatio2))
        tvRainTypes = arrayOf(findViewById(R.id.tvRainType), findViewById(R.id.tvRainType2))
        tvRecommends = arrayOf(findViewById(R.id.tvRecommend), findViewById(R.id.tvRecommend2))
        btnWrite = findViewById(R.id.btnWrite)
        btnSeeMemo = findViewById(R.id.btnSeeMemo)
        bottomNavi = findViewById(R.id.bottomNavi)

        // 날짜 초기화
        setDate()

        // nx, ny지점의 날씨 가져와서 설정하기
        setWeather(0, nx, ny)       // 현재 시간대 날씨 설정
        setWeather(1, nx, ny)       // 다음 시간대 날씨 설정

        // 돋보기 이미지 누르면 지역칮기 액티비티(FindAreaActivity)로 이동
        imgSearchArea.setOnClickListener {
            var intent = Intent(this@MainActivity, FindAreaActivity::class.java)
            startActivityForResult(intent, 0)
        }
        // 지역명 클릭해도 돋보기 이미지 누른 것과 같은 처리
        tvAreaName.setOnClickListener {
            imgSearchArea.callOnClick()
        }

        // <옷 기록하기> 버튼 누르면 기록하기 액티비티(WriteActivity)로 이동
        btnWrite.setOnClickListener {
            var intent = Intent(this@MainActivity, WriteActivity::class.java)
            intent.putExtra("temp", tvTemps[0].text.toString())
            startActivity(intent)
        }

        // <내 기록보기> 버튼 누르면 기록 보는 액티비티(SeeMemoActivity)로 이동
        btnSeeMemo.setOnClickListener {
            var intent = Intent(this@MainActivity, SeeMemoActivity::class.java)
            startActivity(intent)
        }

        // 네비게이션
        bottomNavi.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.tab1_write -> {
                    btnWrite.callOnClick()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab2_show_memo -> {
                    btnSeeMemo.callOnClick()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab3_home -> {
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab4_find_addreaa -> {
                    imgSearchArea.callOnClick()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.tab5_setting -> {
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
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
        // 현재 시각이 00시가 넘었다면 어제 예보한 데이터를 가져와야함
        if (base_time >= "2000") {
            cal.add(Calendar.DATE, -1).toString()
            base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        }

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
            if (temp >= "21") temp = (time.toInt() - 24).toString()
            tvTimes[index].text = temp
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
        }

        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            nx = data!!.getStringExtra("Find_nx")!!
            ny = data!!.getStringExtra("Find_ny")!!
            Log.d("mmm 메인:변환된 위경도", nx + " " + ny)
            var areaName = data!!.getStringExtra("areaName")

            // 해당 지역의 날씨 정보 보이기
            tvAreaName.text = areaName  // 해당 지역명으로 바꾸기
            setWeather(0, nx, ny)   // 현재 시간대 낼씨 설정
            setWeather(1, nx, ny)   // 다음 시간대 날씨 설정
        }
    }

}