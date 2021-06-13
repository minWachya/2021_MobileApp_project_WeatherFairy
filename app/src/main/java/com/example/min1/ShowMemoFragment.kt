package com.example.min1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


// 추가된 Memo 아이템이 리사이클러뷰에서 보여짐
// 월별/온도별 Memo 보이기
class ShowMemoFragment : Fragment() {
    lateinit var tvSelect : TextView                // 월별/온도별 텍스트뷰
    lateinit var spinner : Spinner                  // 원별/온도별 값을 선택하는 스피너
    lateinit var btnToggle : ToggleButton           // 월별/온도별을 선택하는 토글 버튼
    lateinit var recyclerView : RecyclerView        // 검색 결과를 보여주는 리사이클러뷰
    lateinit var memoAdapter : WeatherMemoAdapter
    lateinit var databaseRef : DatabaseReference
    var dataSanpshot : DataSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_show_memo, container, false)

        tvSelect = view.findViewById(R.id.tvSelect)
        spinner = view.findViewById(R.id.spinner)
        btnToggle = view.findViewById(R.id.btnToggle)
        recyclerView = view.findViewById(R.id.recyclerView)

        // 검색 설정 월별/1월로 초기화
        var searchWord = "01"
        var searchOption = "month"

        // 연결된 파이어베이스에서 데이터 가져오기
        databaseRef = FirebaseDatabase.getInstance().reference

        // 데이터 불러오기
        databaseRef.orderByChild("temp").addValueEventListener(object : ValueEventListener {
            // 내용 추가될 때마다 자동으로 화면 바뀌게
            override fun onDataChange(snapshot: DataSnapshot) { // snapshot : 데이터베이스에서 조회되는 객체들을 접근할 수 있는 권한이 있는 객체
                dataSanpshot = snapshot
                search(dataSanpshot!!, searchWord, searchOption)
            }

            // 취소되었을 때
            override fun onCancelled(error: DatabaseError) {
                Log.e("test", "loadItem:onCancelled : ${error.toException()}")
            }
        })

        // 스피너 어댑터 설정
        // 온도별
        val tempAdapter = ArrayAdapter.createFromResource(context!!, R.array.tempGroup, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        val tempSpinnerAdapter = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position) {
                    0 -> searchWord = "28_"
                    1 -> searchWord = "23_27"
                    2 -> searchWord = "20_22"
                    3 -> searchWord = "17_19"
                    4 -> searchWord = "12_16"
                    5 -> searchWord = "9_11"
                    6 -> searchWord = "5_8"
                    7 -> searchWord = "_4"
                }
                if (dataSanpshot != null) search(dataSanpshot!!, searchWord, searchOption)
            }
        }
        // 월별
        val monthAdapter = ArrayAdapter.createFromResource(context!!, R.array.month, android.R.layout.simple_spinner_item)
            .also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        val monthSpinnerAdapter = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position) {
                    0 -> searchWord = "01"
                    1 -> searchWord = "02"
                    2 -> searchWord = "03"
                    3 -> searchWord = "04"
                    4 -> searchWord = "05"
                    5 -> searchWord = "06"
                    6 -> searchWord = "07"
                    7 -> searchWord = "08"
                    8 -> searchWord = "09"
                    9 -> searchWord = "10"
                    10 -> searchWord = "11"
                    11 -> searchWord = "12"
                }
                if (dataSanpshot != null) search(dataSanpshot!!, searchWord, searchOption)
            }
        }
        // 스피너 초기화는 월별/1월
        spinner.adapter = monthAdapter
        spinner.onItemSelectedListener = monthSpinnerAdapter

        // 리사이클러뷰 매니저 설정
        val layoutManager = LinearLayoutManager(context)
        // 최신 글 먼저 보기(가장 나중에 저장된 글 제일 먼저 보기)
        // 파이어베이스에서 역조회 안 됨
        // -> 저장을 역순으로 하겟다.
        layoutManager.setReverseLayout(true)
        layoutManager.setStackFromEnd(true)
        recyclerView.layoutManager = layoutManager
        // 리아시클러뷰에 어댑터 달기
        memoAdapter = WeatherMemoAdapter()
        recyclerView.adapter = memoAdapter

        // 토글버튼으로 월별/온도별 변경시
        btnToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tvSelect.text = "온도 선택"
                spinner.adapter = tempAdapter
                spinner.onItemSelectedListener = tempSpinnerAdapter
                searchOption = "tempGroup"
            } else {
                tvSelect.text = "월 선택"
                spinner.adapter = monthAdapter
                spinner.onItemSelectedListener = monthSpinnerAdapter
                searchOption = "month"
            }
        }
        return view
    }

    // 월별/온도별 검색하여 해당 결과만 보이기
    fun search(dataSanpshot: DataSnapshot, searchWord: String, option: String) {
        // memo에서 쭉 내려옴
        val collectionIterator = dataSanpshot.children.iterator()   // Firebase DB
        // memo가 있다 == 사용자가 작성한 Memo가 존재한다
        if (collectionIterator.hasNext()) {
            // 예전 아이템 지우기
            memoAdapter.items.clear()
            // 모든 기록 읽어오기
            val memos = collectionIterator.next()  // memo 폴더
            val itemsIterator = memos.child("${MainActivity.email}").children.iterator()   // memo/이메일/child 폴더
            while (itemsIterator.hasNext()) {
                // 매 반복마다 itemsIterator가 가리키는 아이템 가져오기
                val currentItem = itemsIterator.next()
                // 해시맵 형태로 읽어오기(저장도 해시맵 형태로 해야하니까)
                val map = currentItem.value as HashMap<String, String>
                if (map[option] != searchWord) continue             // 설정한 값이 아니면 지나가기

                // 데이터 변수로 만들기
                val objectId = currentItem.key.toString()
                val date = map["date"].toString()
                val temp = map["temp"].toString()
                val top = map["top"].toString()
                val bottom = map["bottom"].toString()
                val outer = map["outer"].toString()
                val memo = map["memo"].toString()
                val month = map["month"].toString()
                val tempGroup = map["tempGroup"].toString()

                // 리사이클러뷰에 연결
                memoAdapter.items.add(WeatherMemo(objectId, date, temp, top, bottom, outer, memo, month, tempGroup))
            }
            // 데이터 바뀌었다고 알려주기
            memoAdapter.notifyDataSetChanged()
        }
    }

}