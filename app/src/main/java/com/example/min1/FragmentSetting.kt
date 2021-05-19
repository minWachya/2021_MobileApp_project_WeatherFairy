package com.example.min1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.kakao.sdk.user.UserApiClient

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentSetting : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var btnLogOut : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        btnLogOut = view.findViewById(R.id.btnLogOut)

        // 로그아웃하기
        btnLogOut.setOnClickListener {
            // 카카오 디벨로퍼 홈페이지의 "로그아웃" 복붙
            // 로그아웃
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e("mmm", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                }
                else {
                    Log.i("mmm", "로그아웃 성공. SDK에서 토큰 삭제됨")
                    Toast.makeText(context, "정상적으로 로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentSetting().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}