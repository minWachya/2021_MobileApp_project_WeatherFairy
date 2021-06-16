package com.example.min1

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

// 카카오 로그인 위한 준비
class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Kakao SDK 초기화
        KakaoSdk.init(this, "869b0ecf65dacae7d89ac1bba906e8cf")
    }
}