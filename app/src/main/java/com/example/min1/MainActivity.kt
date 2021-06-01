package com.example.min1

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ActionMenuView
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

// 메인 액티비티
class MainActivity : AppCompatActivity() {
    lateinit var bottomNavi : BottomNavigationView  // 하단 네비게이션
    lateinit var container : FrameLayout            // 프레임 레이아웃

    val fragmentShowMemo = FragmentShowMemo()       // 내 기록 보기 프레그먼트
    val fragmentHome = FragmentHome()               // 홈 프레그먼트
    val fragmentFindArea = FragmentFindArea()       // 지역 찾기 프레그먼트
    val fragmentSetting = FragmentSetting()         // 설정 프레그먼트

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavi = findViewById(R.id.bottomNavi)
        container = findViewById(R.id.container)

        // 사용자 정보 받기(이메일)
        email = intent.getStringExtra("email").toString()
        Log.d("mmm 메인에서 받은 이메일", "${email}")

        // 시작은 Home 프레그먼트로 초기화
        with(supportFragmentManager.beginTransaction()) {
            replace(R.id.container, fragmentHome)
            commit()
        }

        // 네비게이션
        bottomNavi.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                // 옷차림 기록하기
                R.id.tab1_write -> {
                    var intent = Intent(applicationContext, WriteActivity::class.java)
                    startActivity(intent)
                    return@setOnNavigationItemSelectedListener true
                }
                // 내 기록 보기
                R.id.tab2_show_memo -> {
                    with(supportFragmentManager.beginTransaction()) {
                        replace(R.id.container, fragmentShowMemo)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                // 메인 화면
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
                // 설정
                R.id.tab5_setting -> {
                    with(supportFragmentManager.beginTransaction()) {
                        replace(R.id.container, fragmentSetting)
                        commit()
                    }
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }

    // 데이터가 셋팅된 프레그먼트 띄우기
    fun setHomeFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragmentHome)
        transaction.commit()
    }

    // 이메일 전역변수
    companion object {
        var email = ""
    }

}