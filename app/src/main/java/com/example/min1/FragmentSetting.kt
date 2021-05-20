package com.example.min1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kakao.sdk.user.UserApiClient

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentSetting : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var settingRecyclerView : RecyclerView
    private lateinit var adapter : SettingAdapter

    var nx = ""
    var ny = ""
    var areaName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

            nx = it.getString("nx").toString()
            ny = it.getString("ny").toString()
            areaName = it.getString("areaName").toString()
            Log.d("mmm setting 받은 데이터", nx + "," + ny + ", " + areaName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        settingRecyclerView = view.findViewById(R.id.settingRecylerView)

        // 리사이클러뷰 매니저 설정
        settingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        settingRecyclerView.setHasFixedSize(true)
        // 리아시클러뷰에 어댑터 달기
        adapter = SettingAdapter()
        settingRecyclerView.adapter = adapter

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