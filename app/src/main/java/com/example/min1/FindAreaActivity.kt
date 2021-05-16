package com.example.min1

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapPoint as MapPoint

class FindAreaActivity  : AppCompatActivity() {
    lateinit var btnBack1 : Button
    lateinit var map : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_area)

        btnBack1 = findViewById(R.id.btnBack1)
        map = findViewById(R.id.map)

        // 카카오맵
        val mapView = MapView(this)
        val mapViewContainer = map as ViewGroup
        mapViewContainer.addView(mapView)

        //var maker = MapPOIItem()
        //var area = MapPoint.mapPointWithGeoCoord(37.541889,127.095388)
        //area.mapPointGeoCoord       // 위경도 나타내기
        ////var areaName = MapReverseGeoCoder.findAddressForMapPoint("", area)

        ////Log.d("mmm 좌표", maker.mapPoint.toString())
        ////Log.d("mmm 지역명", areaName)
        //maker.markerType = MapPOIItem.MarkerType.BluePin    // 마커 타입 설정

        //mapView.setShowCurrentLocationMarker(true)  // 마커 보이기
        //mapView.setDefaultCurrentLocationMarker()   // 기본 제공되는 현위치 아이콘
        ////mapView.setMapCenterPoint()               // 지도 화면의 중심점 설정

        ////var area = MapPoint.mapPointWithCONGCoord(double, double)   // 해당 위경도 좌표의 객체 반환


        val marker = MapPOIItem()
        marker.itemName = "마커위에 나올 이름"
        marker.tag = 0
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.68741320960496, 126.77534893056148)
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker)
        mapView.setMapCenterPoint(marker.mapPoint, true)               // 지도 화면의 중심점 설정

        
        btnBack1.setOnClickListener {
            finish()
        }
    }
}