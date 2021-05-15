package com.example.min1

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FindAreaActivity  : AppCompatActivity() {
    lateinit var btnBack1 : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_area)

        btnBack1 = findViewById(R.id.btnBack1)

        btnBack1.setOnClickListener {
            finish()
        }
    }
}