package com.example.min1


import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.HashMap

// 파이어 베이스에 저장할 내용들 작성 및 저장
// 데이트피커 다이얼로그 사용
class WriteActivity : AppCompatActivity() {
    lateinit var tvDate : TextView
    lateinit var imgCalendar : ImageView
    lateinit var editTemp : EditText
    lateinit var editTop : EditText
    lateinit var editBottom : EditText
    lateinit var editOuter : EditText
    lateinit var editMemo : EditText
    lateinit var btnCompleteMemo : Button
    lateinit var databaseRef : DatabaseReference    // 파이어베이스 접근 가능한 자료형

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        tvDate = findViewById(R.id.tvDate)
        imgCalendar = findViewById(R.id.imgCalendar)
        editTemp = findViewById(R.id.editTemp)
        editTop = findViewById(R.id.editTop)
        editBottom = findViewById(R.id.editBottom)
        editOuter = findViewById(R.id.editOuter)
        editMemo = findViewById(R.id.editMemo)
        btnCompleteMemo = findViewById(R.id.btnCompleteMemo)

        // intent에서 온도 정보 받이서 온도 설정하기
        //val temp = intent.getStringArrayExtra("temp")
        //editTemp.text = Editable.Factory.getInstance().newEditable(temp.toString())

        // 캘린더 이미지 누르면 데이트피커 다이얼로그 보이게
        imgCalendar.setOnClickListener {
            var calender = Calendar.getInstance()
            var year = calender.get(Calendar.YEAR)
            var month = calender.get(Calendar.MONDAY)
            var day = calender.get(Calendar.DAY_OF_MONTH)

            var listner = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                var month = ""
                if (i2 + 1 < 10) month = "0" + (i2 + 1)

                tvDate.text = "${i}.${month}.${i3}"
            }

            var picker = DatePickerDialog(this@WriteActivity, listner, year, month, day)
            picker.show()
        }

        // 연결된 파이어베이스에서 데이터 가져오기
        databaseRef = FirebaseDatabase.getInstance().reference

        // <기록 완료> 버튼 누르면 위에 적은 내용을 파이어베이스에 저장하고
        // 토스트 띄운 뒤 처음 화면으로 돌아가기
        btnCompleteMemo.setOnClickListener {
            val date = tvDate.text.toString()
            val temp = editTemp.text.toString()
            val top = editTop.text.toString()
            val bottom = editBottom.text.toString()
            val outer = editOuter.text.toString()
            val memo = editMemo.text.toString()

            val month = date.substring(5, 7)
            val tempGroup = getTempGroup(temp)

            // 파이어베이스에 데이터 저장하기
            saveMemo(date, temp, top, bottom, outer, memo, month, tempGroup)

            Toast.makeText(this@WriteActivity, "기록 완료하였습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // 온도 그룹(TempGroup) 정하기
    fun getTempGroup(temp : String) : String {
        val tempInt = temp.toInt()
        var result = ""
        when (tempInt) {
            in 5..8 -> result = "5_8"
            in 9..11 -> result = "9_11"
            in 12..16 -> result = "12_16"
            in 17..19 -> result = "17_19"
            in 20..22 -> result = "20_22"
            in 23..27 -> result = "23_27"
            in 28..50 -> result = "28_"
            else -> result = "_4"
        }
        return result
    }

    // 파이어베이스에 저장
    fun saveMemo(date: String, temp: String, top: String, bottom: String, outer: String,
                 memo: String, month: String, tempGroup: String) {
        // memo에 child로 감상평 추가(이때 키 자동 생성, 이 키 얻어오기)
        var key : String? = databaseRef.child("memo").push().getKey()

        // 객체 생성
        val obj = WeatherMemo(key!!, date, temp, top, bottom, outer, memo, month, tempGroup)
        // 객체를 맵 형으로 변환
        val memotValues : HashMap<String, String> = obj.toMap()

        // 파이어베이스에 넣어주기(인자에 해시맵과 해시맵에 접근할 수 있는 경로 들어가야함)
        // -> 별도의 해시맵을 만들어줘야함
        val childUpdate : MutableMap<String, Any> = HashMap()
        childUpdate["/memo/$key"] = memotValues

        databaseRef.updateChildren(childUpdate)
    }
}