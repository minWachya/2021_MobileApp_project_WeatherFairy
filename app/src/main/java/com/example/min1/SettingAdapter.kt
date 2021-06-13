package com.example.min1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.min1.SettingFragment.Companion.adapter
import android.app.AlertDialog
import android.widget.ImageView

// Setting 프레그먼트의 리사이클러뷰 어댑터
class SettingAdapter : RecyclerView.Adapter<SettingAdapter.ViewHolder>() {
    // 뷰 홀더 생성(area_list.xml을 어댑터에 붙여주기)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.area_list, parent, false)
        val imgRemove : ImageView = view.findViewById(R.id.imgRemove)

        // 리스너 달기
        return ViewHolder(view).apply {
            // 클릭 시 해당 날씨 정보로 변경
            itemView.setOnClickListener {
                // FragmentHome의 지역 정보 수정
                FragmentHome.nx = settingAreaArr[position].nx
                FragmentHome.ny = settingAreaArr[position].ny
                FragmentHome.areaName = settingAreaArr[position].settingAreaName

                var areaName = settingAreaArr[position].settingAreaName

                Toast.makeText(parent.context, areaName + "의 날씨 정보로 변경하였습니다.", Toast.LENGTH_SHORT).show()
            }
            //  x 이미지 클릭 시 관심 지역 삭제
            imgRemove.setOnClickListener {
                var alert = AlertDialog.Builder(parent.context)
                alert.setTitle("삭제 확인")
                alert.setMessage("관심 지역에서 삭제하시겠습니까? : " + settingAreaArr[position].settingAreaName)
                alert.setPositiveButton("네") { dialog, which ->
                    settingAreaArr.removeAt(position)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(parent.context,  "삭제하였습니다.", Toast.LENGTH_SHORT).show()
                }
                alert.setNegativeButton("아니오", null)
                alert.show()
            }
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

    companion object {
        // SettingArea 배열
        var settingAreaArr = ArrayList<SettingArea>()
    }
}