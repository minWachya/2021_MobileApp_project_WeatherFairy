package com.example.min1

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView    // 파이어베이스 접근 가능한 자료형
import com.google.firebase.database.FirebaseDatabase

// 날씨 기록을 관리하는 어댑터
class WeatherMemoAdapter : RecyclerView.Adapter<WeatherMemoAdapter.ViewHolder>() {
    // WeatherMemo 배열
    var items = ArrayList<WeatherMemo>()
    val databaseRef = FirebaseDatabase.getInstance().reference

    // 뷰홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherMemoAdapter.ViewHolder {
        // weather_memo.xml 파일과 연결
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.weather_memo, parent, false)
        val imgDataDelete : ImageView = itemView.findViewById(R.id.imgDataDelete)

        return ViewHolder(itemView).apply {
            // x 이미지 클릭하면 데이터 삭제하기
            imgDataDelete.setOnClickListener {
                var alert = AlertDialog.Builder(parent.context)
                alert.setTitle("삭제 확인")
                alert.setMessage("해당 기록을 삭제하시겠습니까?")
                alert.setPositiveButton("네") { dialog, which ->
                    val key = items[position].objectId
                    databaseRef.child("memo/${MainActivity.email}/$key").setValue(null)
                    Toast.makeText(parent.context,  "삭제하였습니다.", Toast.LENGTH_SHORT).show()
                }
                alert.setNegativeButton("아니오", null)
                alert.show()
            }
        }
    }

    // position 번째 아이템 설정하기
    override fun onBindViewHolder(holder: WeatherMemoAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.setItem(item)
    }

    // 아이템 갯수 리턴
    override fun getItemCount() = items.size

    // 뷰홀더에서 연결한 weather_memo.xml을 이용해서 WeatherMemo 클래스에 데이터 넣어주기
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        var tvTemp = itemView.findViewById<TextView>(R.id.tvTemp)
        var tvTop = itemView.findViewById<TextView>(R.id.tvTop)
        var tvBottom = itemView.findViewById<TextView>(R.id.tvBottom)
        var tvOuter = itemView.findViewById<TextView>(R.id.tvOuter)
        var tvMemo = itemView.findViewById<TextView>(R.id.tvMemo)

        fun setItem(item: WeatherMemo) {
            tvDate.text = item.date
            tvTemp.text = item.temp
            tvTop.text = item.top
            tvBottom.text = item.bottom
            tvOuter.text = item.outer
            tvMemo.text = item.memo
        }
    }
}