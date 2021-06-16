package com.example.min1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.min1.InterestAreaFragment.Companion.adapter
import android.app.AlertDialog
import android.widget.ImageView
import com.example.min1.FragmentHome
import com.example.min1.R
import com.example.min1.models.InterestArea

// InterestArea 프레그먼트의 리사이클러뷰 어댑터
class InterestAreaAdapter : RecyclerView.Adapter<InterestAreaAdapter.ViewHolder>() {
    // 뷰 홀더 생성(list_interest_area.xml을 어댑터에 붙여주기)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_interest_area, parent, false)
        val imgRemove : ImageView = view.findViewById(R.id.imgRemove)

        // 리스너 달기
        return ViewHolder(view).apply {
            // 클릭 시 해당 날씨 정보로 변경
            itemView.setOnClickListener {
                // FragmentHome의 지역 정보 수정
                FragmentHome.nx = INTEREST_AREA_ARR[position].nx
                FragmentHome.ny = INTEREST_AREA_ARR[position].ny
                FragmentHome.areaName = INTEREST_AREA_ARR[position].areaName

                // 노란 별로 변경
                FragmentHome.lastImgStar = R.drawable.img_star_yellow

                Toast.makeText(parent.context, INTEREST_AREA_ARR[position].areaName + "의 날씨 정보로 변경하였습니다.", Toast.LENGTH_SHORT).show()
            }
            // x 이미지 클릭 시 관심 지역 삭제
            imgRemove.setOnClickListener {
                // 삭제 확인 알림창 띄우기
                var alert = AlertDialog.Builder(parent.context)
                alert.setTitle("삭제 확인")
                alert.setMessage("관심 지역에서 삭제하시겠습니까? : " + INTEREST_AREA_ARR[position].areaName)
                // <네> 버튼 누르면
                alert.setPositiveButton("네") { dialog, which ->
                    // 홈 액티비티의 장소가 관심 지역이었다면 하얀 별로 변경
                    if (FragmentHome.areaName == INTEREST_AREA_ARR[position].areaName)
                        FragmentHome.lastImgStar = R.drawable.img_star_white

                    // 관심 지역 삭제하기
                    INTEREST_AREA_ARR.removeAt(position)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(parent.context,  "삭제하였습니다.", Toast.LENGTH_SHORT).show()
                }
                // <아니오> 버튼 누르면 아무 일도 없음
                alert.setNegativeButton("아니오", null)
                alert.show()
            }
        }
    }

    // 뷰와 데이터 연결해주기
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = INTEREST_AREA_ARR[position]
        holder.setItem(item)
    }

    // 아이템 갯수 리턴
    override fun getItemCount(): Int = INTEREST_AREA_ARR.size

    // InterestArea.kt와 list_interest_area.xml을 연결
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var tvAreaName = itemView.findViewById<TextView>(R.id.tvAreaName)

        fun setItem(item : InterestArea) {
            tvAreaName.text = item.areaName // 관심지역 이름
        }
    }

    companion object {
        // InterestArea 배열
        val INTEREST_AREA_ARR = ArrayList<InterestArea>()
    }
}