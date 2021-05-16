package com.example.min1

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView

class FindAreaActivity  : AppCompatActivity(), MapReverseGeoCoder.ReverseGeoCodingResultListener {
    lateinit var btnBack1 : Button
    lateinit var map : LinearLayout
    lateinit var marker : MapPOIItem
    lateinit var mapView : MapView

    var areaName = "지역명"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_area)

        btnBack1 = findViewById(R.id.btnBack1)
        map = findViewById(R.id.map)

        // 카카오맵
        mapView = MapView(this)
        val mapViewContainer = map as ViewGroup
        mapViewContainer.addView(mapView)

        ////var areaName = MapReverseGeoCoder.findAddressForMapPoint("", area)
        ////Log.d("mmm 지역명", areaName)
        marker = MapPOIItem()
        marker.itemName = areaName
        marker.tag = 0
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.65136866943945, 127.01617112670128)
        marker.markerType = MapPOIItem.MarkerType.BluePin   // 마커 타입 설정
        mapView.addPOIItem(marker)
        mapView.setMapCenterPoint(marker.mapPoint, true)               // 지도 화면의 중심점 설정

        val reverseGeoCoder = MapReverseGeoCoder(
            "869b0ecf65dacae7d89ac1bba906e8cf",
            marker.mapPoint,
            this,
            this
        )
        reverseGeoCoder.startFindingAddress()

        btnBack1.setOnClickListener {
            finish()
        }
    }

    // ReverseGeoCodingResultListener 재정의
    // 주소 찾기 성공
    override fun onReverseGeoCoderFoundAddress(p0: MapReverseGeoCoder?, p1: String?) {
        Log.d("mmm 주소 성공", p1!!)
        marker.itemName = p1!!
        mapView.addPOIItem(marker)

    }
    // 주소 찾기 실패
    override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
        marker.itemName = "주소를 찾지 못하였습니다."
    }

}