package com.example.min1

import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

// 카카오맵 인터페이스
interface MyMapViewEventListener : MapView.MapViewEventListener {
    override fun onMapViewInitialized(p0: MapView?) { }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) { }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) { }

    // 사용자가 지도를 한 번 클릭했을 때
    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) { }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) { }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) { }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) { }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) { }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) { }
}