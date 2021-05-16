package com.example.min1

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

class FindAreaActivity  : AppCompatActivity(), MapReverseGeoCoder.ReverseGeoCodingResultListener, MyMapViewEventListener {
    lateinit var btnBack1 : Button
    lateinit var map : LinearLayout
    lateinit var marker : MapPOIItem
    lateinit var mapView : MapView
    lateinit var reverseGeoCoder : MapReverseGeoCoder

    var lat = 37.65136866943945
    var lng = 127.01617112670128

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_area)

        btnBack1 = findViewById(R.id.btnBack1)
        map = findViewById(R.id.map)

        // 카카오맵
        mapView = MapView(this)
        val mapViewContainer = map as ViewGroup
        mapView.setMapViewEventListener(this)
        mapViewContainer.addView(mapView)

        marker = MapPOIItem()                                       // 마커
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(lat, lng)   // 기본 주소는 덕성여대
        marker.markerType = MapPOIItem.MarkerType.BluePin           // 마커 타입 설정
        mapView.addPOIItem(marker)                                  // 지도에 마커 붙이기
        mapView.setMapCenterPoint(marker.mapPoint, true)   // 지도 화면의 중심점 설정

        // 마커의 위경도로 주소 찾아서 마커 위에 띄우기
        reverseGeoCoder = MapReverseGeoCoder(
            "869b0ecf65dacae7d89ac1bba906e8cf",
            marker.mapPoint,
            this,
            this
        )
        reverseGeoCoder.startFindingAddress()

        // 지역 설정 완료하면 메인 화면으로 돌아감
        btnBack1.setOnClickListener {
            finish()
        }
    }

    // ReverseGeoCodingResultListener 재정의
    // 주소 찾기 성공 - 지도에 마커 달기
    override fun onReverseGeoCoderFoundAddress(p0: MapReverseGeoCoder?, p1: String?) {
        Log.d("mmm 주소 성공", p1!!)
        marker.itemName = p1!!
        mapView.addPOIItem(marker)
    }
    // 주소 찾기 실패
    override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
        marker.itemName = "주소를 찾지 못하였습니다."
        Log.d("mmm 주소 실패", ",...")
        mapView.addPOIItem(marker)
    }

    // 지도를 한 번 클릭하였을 때
    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        super.onMapViewSingleTapped(p0, p1)

        if (p1 != null) {
            lat = p1!!.mapPointGeoCoord.latitude
            lng = p1!!.mapPointGeoCoord.longitude
            Log.d("mmm 크릭", lat.toString())
            Log.d("mmm 크릭", lng.toString())

            // 클릭한 위치에 마커와 주소 보이기
            marker.mapPoint = p1!!
            reverseGeoCoder = MapReverseGeoCoder(
                "869b0ecf65dacae7d89ac1bba906e8cf",
                p1!!,
                this,
                this
            )
            reverseGeoCoder.startFindingAddress()
            mapView.addPOIItem(marker)
        }
    }
}