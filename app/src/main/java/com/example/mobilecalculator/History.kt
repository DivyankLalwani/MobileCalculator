package com.example.mobilecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.example.mobilecalculator.R.layout.activity_history
import kotlinx.android.synthetic.main.activity_history.*

class History : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_history)

        val extras = intent.extras
        if (extras != null) {
            historyT.text= extras.getString("history")
        }

    }
}