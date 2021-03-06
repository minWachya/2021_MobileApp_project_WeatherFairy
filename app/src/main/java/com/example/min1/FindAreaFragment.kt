package com.example.min1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Spinner
import androidx.fragment.app.Fragment
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView

// 지역 찾기
class FindAreaFragment : Fragment(), MapReverseGeoCoder.ReverseGeoCodingResultListener, MyMapViewEventListener  {
    lateinit var tvAreaName : TextView                  // 지역명
    lateinit var spinner : Spinner                      // 지역 스피너
    lateinit var btnBack1 : Button                      // <지역 설정 완료> 버튼
    lateinit var map : LinearLayout                     // 지도가 담길 레이아웃
    lateinit var marker : MapPOIItem                    // 마커
    lateinit var mapView : MapView                      // 지도
    lateinit var reverseGeoCoder : MapReverseGeoCoder   // 위경도로 주소 가져오기 위해 필요

    // 위경도 초기화(덕성여자대학교)
    var lat = 37.65136866943945
    var lng = 127.01617112670128

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_area, container, false)

        spinner = view.findViewById(R.id.spinner)
        tvAreaName = view.findViewById(R.id.tvAreaName)
        btnBack1 = view.findViewById(R.id.btnBack1)
        map = view.findViewById(R.id.map)

        // 카카오맵
        mapView = MapView(activity)
        val mapViewContainer = map as ViewGroup
        mapView.setMapViewEventListener(this)
        mapViewContainer.addView(mapView)

        var point = MapPoint.mapPointWithGeoCoord(lat, lng)             // 초기값 : 서울 - 덕성여대
        marker = MapPOIItem()                                       // 마커
        marker.markerType = MapPOIItem.MarkerType.BluePin           // 마커 타입 설정

        // 지역 스피너 설정
        val areaAdapter = ArrayAdapter.createFromResource(context!!, R.array.area, android.R.layout.simple_spinner_item)
                .also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
        val areaSpinnerAdapter = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position) {
                    0 -> point = MapPoint.mapPointWithGeoCoord(lat, lng)   // 사용자가 선택한 위치
                    1 -> point = MapPoint.mapPointWithGeoCoord(37.65136866943945, 127.01617112670128)   // 서울 - 덕성여대
                    2 -> point = MapPoint.mapPointWithGeoCoord(37.274949938001555, 127.00919154930807)  // 경기
                    3 -> point = MapPoint.mapPointWithGeoCoord(37.456103748325994, 126.70591458688807)  // 인천
                    4 -> point = MapPoint.mapPointWithGeoCoord(37.8854610363771, 127.7297623959766)     // 강원
                    5 -> point = MapPoint.mapPointWithGeoCoord(36.63883971304833, 127.49099047782575)   // 충북
                    6 -> point = MapPoint.mapPointWithGeoCoord(36.66051322400456, 126.67255999783636)   // 충남
                    7 -> point = MapPoint.mapPointWithGeoCoord(36.48030441303869, 127.2888077131762)    // 세종
                    8 -> point = MapPoint.mapPointWithGeoCoord(36.35067270663006, 127.38476505364646)   // 대전
                    9 -> point = MapPoint.mapPointWithGeoCoord(36.57740564624811, 128.50536398249469)   // 경븍
                    10 -> point = MapPoint.mapPointWithGeoCoord(35.238548121379566, 128.69234305917618) // 경남
                    11 -> point = MapPoint.mapPointWithGeoCoord(35.87388911526642, 128.60132641559755)  // 대구
                    12 -> point = MapPoint.mapPointWithGeoCoord(35.5630857475929, 129.30740773156734)   // 울산
                    13 -> point = MapPoint.mapPointWithGeoCoord(35.179984202358604, 129.07495481128606) // 부산
                    14 -> point = MapPoint.mapPointWithGeoCoord(35.82132252146746, 127.10871827435929)  // 전북
                    15 -> point = MapPoint.mapPointWithGeoCoord(34.81643878614751, 126.46290274011228)  // 전남
                    16 -> point = MapPoint.mapPointWithGeoCoord(37.429586595374424, 127.25523865738475) // 광주
                    17 -> point = MapPoint.mapPointWithGeoCoord(33.48921428666983, 126.49837084551882)  // 제주
                }

                onMapViewSingleTapped(mapView, point)                                       // 클릭한 위치에 마커와 주소 보이기
                mapView.setMapCenterPointAndZoomLevel(point, 7, true)   // 지도 확대/축소 비율은 지역 전체가 적절히 보이게
                mapView.setMapCenterPoint(marker.mapPoint, true)                  // 지도 화면의 중심점 설정
            }
        }
        // 스피너 초기값은 서울 - 덕성여대
        spinner.adapter = areaAdapter
        spinner.onItemSelectedListener = areaSpinnerAdapter

        // <지역 설정 완료>하면 메인 화면으로 돌아감
        // 변환한 격자 좌표와 지역명 반환
        btnBack1.setOnClickListener {
            // 격자 좌표 담기
            var (x, y) = dfs_xy_conv(lat, lng)
            // 지역명 담기 준비(주소 마지막을 지역명으로 설정, 마지막이 '산'이면 그 앞의 주소로 지역명 설정)
            var splitArray = tvAreaName.text.split(" ")
            var areaName = splitArray[splitArray.size - 2]
            if (areaName == "산") areaName = splitArray[splitArray.size - 3]

            // FragmentHome의 지역 정보 수정
            FragmentHome.nx = x
            FragmentHome.ny = y
            FragmentHome.areaName = areaName

            // 시도 이름 설정(경기 북부/남부, 강원 영동/영서 구분)
            var sidoName = splitArray[0].substring(0, 2)
            if (sidoName == "경기") {
                var NorthernGyeonggi = "가평, 고양, 구리, 남양, 동두천, 양주, 연천, 의정부, 파주, 포천"   // 경기 북부
                var area = splitArray[1].substring(0, 2)
                if (NorthernGyeonggi.contains(area)) sidoName = "경기북부"
                else sidoName = "경기남부"
            }
            if (sidoName == "강원") {
                var NorthernGyeonggi = "고성, 속초, 강릉, 양양, 동해, 삼척"   // 영동
                var area = splitArray[1].substring(0, 2)
                if (NorthernGyeonggi.contains(area)) sidoName = "영동"
                else sidoName = "영서"
            }
            FragmentHome.sidoName = sidoName

            // 하얀 별로 변경
            FragmentHome.lastImgStar = R.drawable.img_star_white

            // 메인 액티비티는 FramgentHome 띄워주기
            val mActivity = activity as MainActivity
            mActivity.setHomeFragment()
        }
        return view
    }

    // ReverseGeoCodingResultListener 재정의
    // 주소 찾기 성공 - 지도에 마커 달기 + 지역명 텍스트뷰에 보이기
    override fun onReverseGeoCoderFoundAddress(p0: MapReverseGeoCoder?, p1: String?) {
        tvAreaName.text = p1!!
        marker.itemName = p1!!
        btnBack1.isEnabled = true
        mapView.addPOIItem(marker)
    }
    // 주소 찾기 실패
    override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
        tvAreaName.text = "주소를 찾지 못하였습니다."
        btnBack1.isEnabled = false
    }

    // 지도를 한 번 클릭하였을 때
    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        super.onMapViewSingleTapped(p0, p1)

        if (p1 != null) {
            mapView.removeAllPOIItems()           // 이전 마커 삭제하기
            spinner.setSelection(0)               // 선택<으로 돌아가기

            // 지도상 위경도 얻기
            lat = p1!!.mapPointGeoCoord.latitude
            lng = p1!!.mapPointGeoCoord.longitude

            // 클릭한 위치에 마커와 주소 보이기
            marker.mapPoint = p1!!
            reverseGeoCoder = MapReverseGeoCoder(
                    "869b0ecf65dacae7d89ac1bba906e8cf",
                    p1!!,
                    this@FindAreaFragment,
                    activity
            )
            reverseGeoCoder.startFindingAddress()
            mapView.addPOIItem(marker)
        }
    }

    // 위경도를 기상청에서 사용하는 격자 좌표로 변환
    fun dfs_xy_conv(v1: Double, v2: Double): Pair<String, String> {
        var RE = 6371.00877; // 지구 반경(km)
        var GRID = 5.0; // 격자 간격(km)
        var SLAT1 = 30.0; // 투영 위도1(degree)
        var SLAT2 = 60.0; // 투영 위도2(degree)
        var OLON = 126.0; // 기준점 경도(degree)
        var OLAT = 38.0; // 기준점 위도(degree)
        var XO = 43; // 기준점 X좌표(GRID)
        var YO = 136; // 기1준점 Y좌표(GRID)

        var DEGRAD = Math.PI / 180.0

        var re = RE / GRID
        var slat1 = SLAT1 * DEGRAD
        var slat2 = SLAT2 * DEGRAD
        var olon = OLON * DEGRAD
        var olat = OLAT * DEGRAD

        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)

        var ra = Math.tan(Math.PI * 0.25 + (v1) * DEGRAD * 0.5)
        ra = re * sf / Math.pow(ra, sn)
        var theta = v2 * DEGRAD - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        var x = (ra * Math.sin(theta) + XO + 0.5).toInt().toString()
        var y = (ro - ra * Math.cos(theta) + YO + 0.5).toInt().toString()
        return Pair(x, y)
    }

}