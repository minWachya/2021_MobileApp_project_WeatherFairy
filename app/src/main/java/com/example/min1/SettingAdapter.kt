package com.example.min1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class SettingAdapter : RecyclerView.Adapter<SettingAdapter.ViewHolder>() {
    // SettingArea 배열
    var settingAreaArr = ArrayList<SettingArea>()


    // 뷰 홀더 생성(area_list.xml을 어댑터에 붙여주기)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.area_list, parent, false)

        // 클릭하면 토스트 띄우기
        return ViewHolder(view).apply {
            val currentPosition : Int = adapterPosition // 현재 위치
            val sArea : SettingArea = settingAreaArr.get(currentPosition)  // 현재 위치 아이템
            Toast.makeText(parent.context, "클릭 ${sArea.settingAreaName}", Toast.LENGTH_SHORT).show()
        }
    }

    // 뷰와 데이터 연결해주기
    override fun onBindViewHolder(holder: SettingAdapter.ViewHolder, position: Int) {
        val item = settingAreaArr[position]
        holder.setItem(item)
    }

    // 아이템 갯수 리턴
    override fun getItemCount(): Int = settingAreaArr.size

    // SettingArea.kt와 area_list.xml을 연결
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var tvSettingAreaName = itemView.findViewById<TextView>(R.id.tvSettingAreaName)

        fun setItem(item : SettingArea) {
            tvSettingAreaName.text = item.settingAreaName // 관심지역 이름
        }
    }
}