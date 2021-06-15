package com.example.min1

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.min1.adapter.InterestAreaAdapter
import com.example.min1.models.InterestArea

// 관심 지역 보이기
class InterestAreaFragment : Fragment() {
    lateinit var settingRecyclerView : RecyclerView     // 관심 지역 리사이클러뷰
    lateinit var tvSetting : TextView                   // 간단한 설명

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_interest_area, container, false)

        tvSetting = view.findViewById(R.id.tvSetting)
        settingRecyclerView = view.findViewById(R.id.settingRecylerView)

        // 리사이클러뷰 매니저 설정
        settingRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        settingRecyclerView.setHasFixedSize(true)
        // 리아시클러뷰에 어댑터 달기
        settingRecyclerView.adapter = adapter

        // 관심 지역 0개면 텍스트 보이기
        if (adapter.itemCount == 0)  tvSetting.setVisibility(View.VISIBLE)
        else  tvSetting.setVisibility(View.GONE)

        return view
    }


    // 어댑터 전역변수,  관심 지역 추가 전역 함수
    companion object {
        var adapter = InterestAreaAdapter()  // 어댑터

        // 해당 지역을 배열에 추가하고 토스트 보이기
        @JvmStatic
        fun addInterestArea(context : Context, sArea : InterestArea) {
            InterestAreaAdapter.interestAreaArr.add(sArea)
            Toast.makeText(context, "관심 지역에 추가하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}