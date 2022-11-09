package com.izilab.livenessdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.izilab.livenessdemo.R
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.izilab.liveness.api.LivenessResult

class DetectionSuccessActivity : AppCompatActivity() {
    private lateinit var btnSure: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detection_success_layout)
        val isCheckResult = intent.getBooleanExtra("result", true)
        val iconView = findViewById<AppCompatImageView>(R.id.result_status_icon)
        val statusTextView = findViewById<AppCompatTextView>(R.id.result_status)
        if (!isCheckResult) {
            iconView.setImageResource(R.mipmap.result_status_failed)
            statusTextView.setText(R.string.live_detection_failed)
        } else {
            iconView.setImageBitmap(LivenessResult.getLivenessBitmap())
            Log.d("sdk", "path=" + LivenessResult.getPath())
        }
        btnSure = findViewById(R.id.btn_sure)
        btnSure.setOnClickListener(View.OnClickListener { finish() })
    }
}