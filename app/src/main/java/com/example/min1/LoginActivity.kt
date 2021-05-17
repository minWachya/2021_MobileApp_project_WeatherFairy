package com.example.min1


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton

// 로그인 액티비티(시작 화면)
class LoginActivity : AppCompatActivity() {
    lateinit var btnKakaoLogin : ImageButton        // 카카오 로그인
    lateinit var btnNaverLogin : OAuthLoginButton   // 네이버 로그인
    lateinit var mOAuthLoginInstance : OAuthLogin
    lateinit var mContext: Context

    var TAG = "Login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 로그인 액티비티와 레이아웃 연결
        setContentView(R.layout.activity_login)

        btnKakaoLogin = findViewById(R.id.btnKakaoLogin)
        btnNaverLogin = findViewById(R.id.btnNaverLogin)

        // 카카오 디벨로퍼 홈페이지의 "카카오 로그인 구현 예제" 복붙 후 수정
        // 로그인 공통 callback 구성
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오 로그인 실패", error)
            }
            // 로그인 성공하면 다음 화면 보이기
            else if (token != null) {
                Log.i(TAG, "카카오 로그인 성공 ${token.accessToken}")

                // MainActivity로 넘어가기
                var intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // 카카오톡 로그인 버튼 누르기
        btnKakaoLogin.setOnClickListener {
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
                UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this@LoginActivity, callback = callback)
            }
        }


        //  네이버 아이디로 로그인
        val naver_client_id = "KHicI1oFhj5zdt5VGjma"
        val naver_client_secret = "oBclxogSES"
        val naver_client_name = "끼양"

        mContext = this@LoginActivity
        mOAuthLoginInstance = OAuthLogin.getInstance()
        mOAuthLoginInstance.init(mContext, naver_client_id, naver_client_secret, naver_client_name)
        btnNaverLogin.setOAuthLoginHandler(mOAuthLoginHandler)
    }

    val mOAuthLoginHandler: OAuthLoginHandler = object : OAuthLoginHandler() {
        override fun run(success: Boolean) {
            if (success) {
                Log.d(TAG, "네이버 로그인 성공")

                // MainActivity로 넘어가기
                var intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.i(TAG, "네이버 로그인 실패")
            }
        }

    }

}