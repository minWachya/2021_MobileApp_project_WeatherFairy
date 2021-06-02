package com.example.min1


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.data.OAuthLoginState
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton

// 로그인 액티비티(시작 화면)
class LoginActivity : AppCompatActivity() {
    lateinit var btnKakaoLogin : ImageButton        // 카카오 로그인
    lateinit var btnNaverLogin : OAuthLoginButton   // 네이버 로그인
    lateinit var mOAuthLoginInstance : OAuthLogin
    lateinit var mContext: Context

    var TAG = "Login"

    private lateinit var nhnOAuthLoginModule: OAuthLogin

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

                UserApiClient.instance.me { user, error ->
                    // 정보 요청 실패했을 때
                    if (error != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", error)
                    }
                    // 정보 요청 성공했을 때
                    else if (user != null) {
                        var scopes = mutableListOf<String>()

                        // 이메일 바로 얻을 수 있으면 로그로 출력해보기
                        if (user.kakaoAccount?.email != null) {
                            Log.i("mmmm Login-email 성공", "email:${user.kakaoAccount?.email.toString()}")
                            Log.d("mmm user id", user.id.toString())
                        }
                        // 사용자가 이메일 동의 했는지 확인
                        // 동의 했으면
                        else if (user.kakaoAccount?.emailNeedsAgreement == true) {
                            // 동의 요청 후 이메일 획득
                            scopes.add("account_email") // 이메일 동의 구하기
                            UserApiClient.instance.loginWithNewScopes(this@LoginActivity, scopes) { token, erroe ->
                                // 사용자 동의 구하기 실패
                                if (error != null) {
                                    Log.e(TAG, "사용자 추가 동의 실패", error)
                                }
                                // 사용지 동의 구하기 성공
                                else {
                                    // 다시 사용자 정보 가져오기
                                    UserApiClient.instance.me { user, error ->
                                        // 에러가 있으면
                                        if (error != null) {
                                            Log.e(TAG, "사용자 정보 요청 실패", error)
                                        }
                                        // 성공하면
                                        else if (user != null){
                                            Log.i(TAG, "사용자 정보 요청 성공 : ${user.kakaoAccount?.email}")
                                        }

                                    }
                                }
                            }

                        }
                        // 사용자 동의를 설정하지 않았다면 이메일 확득 불가
                        else {
                            Toast.makeText(this@LoginActivity, "이메일 획득 불가", Toast.LENGTH_SHORT).show()
                        }

                        // MainActivity로 넘어가고 정보 전달하기
                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                        var email = user.kakaoAccount?.email!!.split("@")
                        intent.putExtra("email", email[0])
                        startActivity(intent)
                        finish()
                    }
                }
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
        val naver_client_name = "쩝쩝"

        mContext = this@LoginActivity
        mOAuthLoginInstance = OAuthLogin.getInstance()
        mOAuthLoginInstance.init(mContext, naver_client_id, naver_client_secret, naver_client_name)
        //btnNaverLogin.setOAuthLoginHandler(mOAuthLoginHandler)

        // 복붙
        nhnOAuthLoginModule = OAuthLogin.getInstance()
        nhnOAuthLoginModule.init(this, naver_client_id, naver_client_secret, naver_client_name)
        initOnClickListener()

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

    // 복붙
    fun successLogin(){
        startActivity(Intent(this, MainActivity::class.java))
        finish() }

    private fun initOnClickListener(){ //네이버 로그인
        btnNaverLogin.setOnClickListener { nhnSignIn() }
    }


    /** * 네이버 로그인 콜백 */
    private fun nhnSignIn(){
        if(nhnOAuthLoginModule.getState(this@LoginActivity) == OAuthLoginState.OK){
            Log.d(TAG, "Nhn status don't need login")
            RequestNHNLoginApiTask().execute()
        }
        else{
            Log.d(TAG, "Nhn status need login")
            nhnOAuthLoginModule.startOauthLoginActivity(this@LoginActivity, @SuppressLint("HandlerLeak")
            object: OAuthLoginHandler(){
                override fun run(success: Boolean) {
                    if (success) {
                        val accessToken = nhnOAuthLoginModule.getAccessToken(this@LoginActivity)
                        val refreshToken = nhnOAuthLoginModule.getRefreshToken(this@LoginActivity)
                        val expiresAt = nhnOAuthLoginModule.getExpiresAt(this@LoginActivity)
                        val tokenType = nhnOAuthLoginModule.getTokenType(this@LoginActivity)
                        Log.i(TAG, "nhn Login Access Token : $accessToken")
                        Log.i(TAG, "nhn Login refresh Token : $refreshToken")
                        Log.i(TAG, "nhn Login expiresAt : $expiresAt")
                        Log.i(TAG, "nhn Login Token Type : $tokenType")
                        Log.i(TAG, "nhn Login Module State : " + nhnOAuthLoginModule.getState(this@LoginActivity).toString())
                        successLogin()
                    } else {
                        val errorCode = nhnOAuthLoginModule.getLastErrorCode(this@LoginActivity).getCode()
                        val errorDesc = nhnOAuthLoginModule.getLastErrorDesc(this@LoginActivity)
                        Toast.makeText(this@LoginActivity, "errorCode:$errorCode, errorDesc:$errorDesc", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    inner class RequestNHNLoginApiTask : AsyncTask<Void, Void, String>() {
        override fun onPostExecute(result: String?) {
            Log.d(TAG, "onPreExecute : $result")
            val startToken = "<message>"
            val endToken = "</message>"
            val startIndex = result?.indexOf(startToken) ?: -1
            val endIndex = result?.indexOf(endToken) ?: -1
            if (startIndex == -1 || endIndex <= 0){ return }
            val message = result?.substring(startIndex + startToken.length, endIndex)?.trim()
            if(message.equals("success")) {
                Log.d(TAG, "Success RequestNHNLoginApiTask")
                successLogin()
            }
            else Log.e(TAG, "Login Fail")
        }

        override fun doInBackground(vararg params: Void?): String {
            val url = "https://apis.naver.com/nidlogin/nid/getHashId_v2.xml"
            val at = nhnOAuthLoginModule.getAccessToken(this@LoginActivity)
            return nhnOAuthLoginModule.requestApi(this@LoginActivity, at, url) }

        override fun onPreExecute() { } }

}