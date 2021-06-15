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

                // 새 액티비티는 점점 나타나고, 현재 액티비티는 점점 사라지는 애니메니션 적용
                overridePendingTransition(R.anim.fadein, R.anim.fadeout)

                finish()
             }
        }, 1, TimeUnit.SECONDS) // 1초
    }

}