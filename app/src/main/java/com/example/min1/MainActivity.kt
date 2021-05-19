package com.example.min1

import android.app.Activity
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.gms.dynamic.FragmentWrapper
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

// 메인 액티비티
class MainActivity : AppCompatActivity() {
    lateinit var bottomNavi : BottomNavigationView  // 하단 네비게이션
    lateinit var container : FrameLayout            // 프레임 레이아웃

    val fragmentWrite = FragmentWrite()
    val fragmentShowMemo = FragmentShowMemo()
    val fragmentHome = FragmentHome()
    val fragmentFindArea = FragmentFindArea()
    val fragmentSetting = FragmentSetting()

    var temp = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavi = findViewById(R.id.bottomNavi)
        container = findViewById(R.id.container)

        // 시작은 Home 프레그먼트로 초기화
        with(supportFragmentManager.beginTransaction()) {
            var fragmentHome = FragmentHome()
            replace(R.id.container, fragmentHome)
            commit()
        }

        // 네비게이션
        bottomNavi.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                // 옷차림 기록하기
                R.id.tab1_write -> {
                    // container 부분에 FrameHome 넣기
                    with(supportFragmentManager.beginTransaction()) {
                        replace(R.id.container, fragmentWrite)
                        commit()
                    }
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

    // 프레그먼트에서 전달하는 데이터 담아주기
    fun setDataAtFragment(bundle : Bundle) {
        fragmentHome.arguments = bundle

        setFragment(fragmentHome)
    }

    // 데이터가 셋팅된 프레그먼트 띄우기
    fun setFragment(fragment : Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

}