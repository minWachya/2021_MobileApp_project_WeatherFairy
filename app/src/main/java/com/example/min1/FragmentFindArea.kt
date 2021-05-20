package com.example.min1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FragmentFindArea : Fragment(), MapReverseGeoCoder.ReverseGeoCodingResultListener, MyMapViewEventListener  {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var tvAreaName : TextView                  // 지역명
    lateinit var btnBack1 : Button                      // <지역 설정 완료> 버튼
    lateinit var map : LinearLayout                     // 지도가 담길 레이아웃
    lateinit var marker : MapPOIItem                    // 마커
    lateinit var mapView : MapView                      // 지도
    lateinit var reverseGeoCoder : MapReverseGeoCoder   // 위경도로 주소 가져오기 위해 필요

    // 위경도 초기화(덕성여자대학교)
    var lat = 37.65136866943945
    var lng = 127.01617112670128

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_area, container, false)

        tvAreaName = view.findViewById(R.id.tvAreaName)
        btnBack1 = view.findViewById(R.id.btnBack1)
        map = view.findViewById(R.id.map)
        // 카카오맵
        mapView = MapView(activity)
        val mapViewContainer = map as ViewGroup
        mapView.setMapViewEventListener(this)
        mapViewContainer.addView(mapView)

        marker = MapPOIItem()                                       // 마커
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(lat, lng)   // 기본 주소는 덕성여대
        marker.markerType = MapPOIItem.MarkerType.BluePin           // 마커 타입 설정
        mapView.addPOIItem(marker)                                  // 지도에 마커 붙이기
        mapView.setMapCenterPoint(marker.mapPoint, true)   // 지도 화면의 중심점 설정
        Log.d(
            "mmm 지도 중심좌표",
            "${marker.mapPoint.mapPointGeoCoord.latitude}, ${marker.mapPoint.mapPointGeoCoord.longitude}"
        )

        // 마커의 위경도로 주소 찾아서 마커 위에 띄우기
        reverseGeoCoder = MapReverseGeoCoder(
            "869b0ecf65dacae7d89ac1bba906e8cf",
            marker.mapPoint,
            this@FragmentFindArea,
            activity
        )
        reverseGeoCoder.startFindingAddress()

        // 지역 설정 완료하면 메인 화면으로 돌아감
        // 변환한 격자 좌표와 지역명 반환
        btnBack1.setOnClickListener {
            // 격자 좌표 담기
            var (x, y) = dfs_xy_conv(lat, lng)
            // 지역명 담기
            var splitArray = tvAreaName.text.split(" ")
            var areaName = splitArray[splitArray.size - 2]
            if (areaName == "산") areaName = splitArray[splitArray.size - 3]

            // 번들에 담아서 메인 액티비티에 보내기
            val bundle = Bundle()
            bundle.putString("nx", x)
            bundle.putString("ny", y)
            bundle.putString("areaName", areaName)
            // 메인 액티비티는 fragmentHome에 데이터를 보냄
            val mActivity = activity as MainActivity
            mActivity.setDataAtHomeFragment(bundle)
        }
        return view
    }

    // ReverseGeoCodingResultListener 재정의
    // 주소 찾기 성공 - 지도에 마커 달기 + 지역명 텍스트뷰에 보이기
    override fun onReverseGeoCoderFoundAddress(p0: MapReverseGeoCoder?, p1: String?) {
        Log.d("mmm 주소 성공", p1!!)
        tvAreaName.text = p1!!
        marker.itemName = p1!!
        mapView.addPOIItem(marker)
    }
    // 주소 찾기 실패
    override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
        tvAreaName.text = "주소를 찾지 못하였습니다."
        Log.d("mmm 주소 실패", "주소 찾기 실패")
    }

    // 지도를 한 번 클릭하였을 때
    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {
        super.onMapViewSingleTapped(p0, p1)

        if (p1 != null) {
            mapView.removeAllPOIItems()           // 이전 마커 삭제하기

            // 지도상 위경도 얻기
            lat = p1!!.mapPointGeoCoord.latitude
            lng = p1!!.mapPointGeoCoord.longitude
            Log.d("mmm 지도 클릭", lat.toString())
            Log.d("mmm 지도 클릭", lng.toString())

            // 클릭한 위치에 마커와 주소 보이기
            marker.mapPoint = p1!!
            reverseGeoCoder = MapReverseGeoCoder(
                "869b0ecf65dacae7d89ac1bba906e8cf",
                p1!!,
                this@FragmentFindArea,
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentFindArea().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}