package com.example.min1

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

// 옷차림을 기록하는 액티비티
class WriteActivity : AppCompatActivity() {
    lateinit var imgBack : ImageView                // 뒤로가기 버튼
    lateinit var imgCheck : ImageView               // 입력 제어 이미지
    lateinit var tvCheck : TextView                 // 입력 제어 문구
    lateinit var tvDate : TextView                  // 날짜
    lateinit var imgCalendar : ImageView            // 달력 이미지(누르면 데이트피터 다이얼로그)
    lateinit var editTemp : EditText                // 온도 입력
    lateinit var editTop : EditText                 // 상의 입력
    lateinit var editBottom : EditText              // 하의 입력
    lateinit var editOuter : EditText               // 아우터 입력
    lateinit var editMemo : EditText                // 메모 입력
    lateinit var btnCompleteMemo : Button           // <기록 완료> 버튼
    lateinit var databaseRef : DatabaseReference    // 파이어베이스 접근 가능한 자료형

    // 에디트 텍스트에 빈칸이 없는지 확인하는 변수
    var memoTemp = true
    var memoTop = false
    var memoBottom = false
    var memoOuter = false
    var memo = false

    var email = ""      // 이메일
    var curTemp = ""    // 현재 온도

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        imgBack = findViewById(R.id.imgBack)
        imgCheck = findViewById(R.id.imgCheck)
        tvCheck = findViewById(R.id.tvCheck)
        tvDate = findViewById(R.id.tvDate)
        imgCalendar = findViewById(R.id.imgCalendar)
        editTemp = findViewById(R.id.editTemp)
        editTop = findViewById(R.id.editTop)
        editBottom = findViewById(R.id.editBottom)
        editOuter = findViewById(R.id.editOuter)
        editMemo = findViewById(R.id.editMemo)
        btnCompleteMemo = findViewById(R.id.btnCompleteMemo)

        // 사용자 이메일 정보 받기
        email = intent.getStringExtra("email").toString()
        // 현재 온도 정보 받아서 설정
        curTemp = intent.getStringExtra("curTemp").toString()
        editTemp.setText(curTemp)

        // <뒤로가기> 버튼 누르면 액티비티 종료
        imgBack.setOnClickListener {
            finish()
        }

        // 텍스트뷰에 오늘 날짜 미리 보이기
        var calender = Calendar.getInstance()
        var year = calender.get(Calendar.YEAR)
        var month = calender.get(Calendar.MONDAY)
        var day = calender.get(Calendar.DAY_OF_MONTH)
        var m = "${month + 1}"
        var d = "${day}"
        if (month + 1 < 10) m = "0" + (month + 1)
        if (day < 10) d = "0" + day
        tvDate.text = "${year}.${m}.${d}"

        // 캘린더 이미지 누르면 데이트피커 다이얼로그 보이게
        imgCalendar.setOnClickListener {
            var listner = DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                // 한자리 숫자 앞에 0 붙여서 2자리로 통일
                m = "${i2 + 1}"
                d = "${i3}"
                if (i2 + 1 < 10) m = "0" + (i2 + 1)
                if (i3 < 10) d = "0" + i3

                tvDate.text = "${i}.${m}.${d}"
            }

            // 오늘 날짜가 선택된 다이얼로그 보이기
            var picker = DatePickerDialog(this@WriteActivity, R.style.DialogTheme, listner, year, month, day)
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

            // 텍스트뷰 초기화
            initTextView()

            Toast.makeText(this@WriteActivity, "기록 완료하였습니다.", Toast.LENGTH_SHORT).show()
        }

        // 에디드텍스트 리스너 설정
        setTextListener()
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
        var key : String? = databaseRef.child("memo/${email}").push().getKey()

        // 객체 생성
        val obj = WeatherMemo(key!!, date, temp, top, bottom, outer, memo, month, tempGroup)
        // 객체를 맵 형으로 변환
        val memotValues : HashMap<String, String> = obj.toMap()

        // 파이어베이스에 넣어주기(인자에 해시맵과 해시맵에 접근할 수 있는 경로 들어가야함)
        // -> 별도의 해시맵을 만들어줘야함
        val childUpdate : MutableMap<String, Any> = HashMap()
        childUpdate["/memo/${email}/$key"] = memotValues

        databaseRef.updateChildren(childUpdate)
    }

    // 에디트 텍스트 모두 값이 있어야지만 데아터 저장 가능
    fun setTextListener() {
        // 온도
        editTemp.addTextChangedListener {
            if (editTemp.text.trim().toString().length == 0) memoTemp = false
            else memoTemp = true
            check()
        }
        // 상의
        editTop.addTextChangedListener{
            if (editTop.text.trim().toString().length == 0) memoTop = false
            else memoTop = true
            check()
        }
        // 하의
        editBottom.addTextChangedListener{
            if (editBottom.text.trim().toString().length == 0) memoBottom = false
            else memoBottom = true
            check()
        }
        // 아우터
        editOuter.addTextChangedListener{
            if (editOuter.text.trim().toString().length == 0) memoOuter = false
            else memoOuter = true
            check()
        }
        // 메모
        editMemo.addTextChangedListener {
            if (editMemo.text.trim().toString().length == 0) memo = false
            else memo = true
            check()
        }
    }

    // 모두 입력 시 버튼 활성화
    fun check() {
        if (memoTemp && memoTop && memoBottom && memoOuter && memo) {
            btnCompleteMemo.isEnabled = true
            imgCheck.setImageResource(R.drawable.img_ok)
            tvCheck.text = "기록 완료 버튼을 눌러주세요."
        }
        else {
            btnCompleteMemo.isEnabled = false
            imgCheck.setImageResource(R.drawable.img_no)
            tvCheck.text="모든 항목을 입력해주세요."
        }
    }

    // 텍스트뷰 초기화
    fun initTextView() {
        // 온도
        editTemp.setText("")
        editTemp.clearFocus()
        // 상의
        editTop.setText("")
        editTop.clearFocus()
        // 하의
        editBottom.setText("")
        editBottom.clearFocus()
        // 아우터
        editOuter.setText("")
        editOuter.clearFocus()
        // 메모
        editMemo.setText("")
        editMemo.clearFocus()
    }

    // 종료 함수 오버라이드
    override fun finish() {
        super.finish()

        // 새 액티비티는 점점 보이고, 현재 액티비티는 내려가는 애니메니션 적용
        overridePendingTransition(R.anim.fadein, R.anim.translate_down)
    }

}