package com.example.min1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1초간 스플래시 보여주기
        val backgroundExecutable : ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        val mainExecutor : Executor = ContextCompat.getMainExecutor(this@Splash)
        backgroundExecutable.schedule({
            mainExecutor.execute {
                // LoginActivity 넘어가기
                var intent = Intent(this@Splash, LoginActivity::class.java)
                startActivity(intent)
                finish()
             }
        }, 1, TimeUnit.SECONDS)
    }
}