package com.example.min1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// 스플래시 화면
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로그인 액티비티로 이동
        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}