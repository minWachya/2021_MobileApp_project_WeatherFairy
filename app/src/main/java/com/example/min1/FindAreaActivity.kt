package com.example.min1

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import net.daum.mf.map.api.MapView

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

        btnBack1.setOnClickListener {
            finish()
        }
    }
}