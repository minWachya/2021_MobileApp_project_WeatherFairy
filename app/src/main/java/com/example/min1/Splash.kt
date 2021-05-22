package com.example.min1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LoginActivity 넘어가기
        var intent = Intent(this@Splash, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}