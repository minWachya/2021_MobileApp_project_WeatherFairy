package com.example.min1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView    // 파이어베이스 접근 가능한 자료형

class WeatherMemoAdapter : RecyclerView.Adapter<WeatherMemoAdapter.ViewHolder>() {
    // WeatherMemo 배열
    var items = ArrayList<WeatherMemo>()

    // 뷰홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherMemoAdapter.ViewHolder {
        // weather_memo.xml 파일과 연결
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.weather_memo, parent, false)

        return ViewHolder(itemView)
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