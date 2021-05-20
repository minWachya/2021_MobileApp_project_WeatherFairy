package com.example.min1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentSetting : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var settingRecyclerView : RecyclerView     // 관심 지역 리사이클러뷰

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

        settingRecyclerView = view.findViewById(R.id.settingRecylerView)

        // 리사이클러뷰 매니저 설정
        settingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        settingRecyclerView.setHasFixedSize(true)
        // 리아시클러뷰에 어댑터 달기
        settingRecyclerView.adapter = adapter

        Log.d("mmm 아이템 갯수", adapter.itemCount.toString())

        return view
    }

    companion object {
        var adapter = SettingAdapter()  // 어댑터

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentSetting().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        // 해당 지역을 배열에 추가하고 토스트 보이기
        @JvmStatic
        fun addSettingArea(context : Context, sArea : SettingArea) {
            adapter.settingAreaArr.add(sArea)
            Toast.makeText(context, "관심 지역에 추가하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}