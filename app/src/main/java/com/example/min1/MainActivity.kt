package com.example.min1

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

// 메인 액티비티(하단 네비게이션)
class MainActivity : AppCompatActivity() {
    lateinit var bottomNavi : BottomNavigationView      // 하단 네비게이션
    lateinit var container : FrameLayout                // 프레임 레이아웃

    val fragmentShowMemo = ShowMemoFragment()           // 내 기록 보기 프레그먼트
    val fragmentHome = FragmentHome()                   // 홈 프레그먼트
    val fragmentFindArea = FindAreaFragment()           // 지역 찾기 프레그먼트
    val fragmentInterestArea = InterestAreaFragment()   // 관심 지역 설정 프레그먼트

    var curTemp = ""            // 현재 온도

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavi = findViewById(R.id.bottomNavi)
        container = findViewById(R.id.container)

        // 사용자 정보 받기(이메일)
        val email = intent.getStringExtra("email").toString()

        // 시작은 Home 프레그먼트로 초기화
        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.container, fragmentHome)
            commit()
        }
        bottomNavi.menu.getItem(2).isChecked = true         // 홈 버튼을 기본 선택으로

        // 네비게이션
        bottomNavi.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                // 옷차림 기록하기
                R.id.tab1_write -> {
                    var intent = Intent(applicationContext, WriteActivity::class.java)
                    intent.putExtra("email", email)     // 이메일(파이어베이스 경로 저장에 필요)
                    intent.putExtra("curTemp", curTemp) // 현재 온도
                    startActivity(intent)

                    // 새 액티비티는 점점 올라오고, 현재 액티비티는 점점 사라지는 애니메니션 적용
                    overridePendingTransition(R.anim.translate_up, R.anim.fadeout)

                    return@setOnNavigationItemSelectedListener false    // 기록하기 액티비티 끝나면 이전 화면이 보여지기 때문에 해당 네비 버튼이 선택되어있지 않게
                }
                // 내 기록 보기
                R.id.tab2_show_memo -> {
                    with(supportFragmentManager.beginTransaction()) {
                        replace(R.id.container, fragmentShowMemo)

                        // 이메일 정보 보내기(파이어베이스 경로 찾을 때 필요)
                        val bundle = Bundle()
                        bundle.putString("email", email)
                        fragmentShowMemo.setArguments(bundle)

                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                // 홈 화면
                R.id.tab3_home -> {
                    with(supportFragmentManager.beginTransaction()) {
                        replace(R.id.container, fragmentHome)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                // 지역 찾기
                R.id.tab4_find_addreaa -> {
                    with(supportFragmentManager.beginTransaction()) {
                        replace(R.id.container, fragmentFindArea)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                // 관심 지역 설정
                R.id.tab5_interest_area -> {
                    with(supportFragmentManager.beginTransaction()) {
                        replace(R.id.container, fragmentInterestArea)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }

    }

    // 현재 온도 설정하기
    fun setTemp(temp : String) {
        curTemp = temp
    }

    // 데이터가 셋팅된 프레그먼트 띄우기
    fun setHomeFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragmentHome)
        transaction.commit()

        bottomNavi.menu.getItem(2).isChecked = true         // 홈 버튼을 기본 선택으로
    }

}