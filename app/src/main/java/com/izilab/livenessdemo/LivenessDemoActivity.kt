package com.izilab.livenessdemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.izilab.liveness.api.LivenessDetectionSDK
import com.izilab.liveness.api.LivenessResult

class LivenessDemoActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var mActionButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_home_layout)
        mActionButton = findViewById(R.id.button_live_detection)
        mActionButton.setOnClickListener(this)
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (!(ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            // 没有权限，申请权限
            // 申请权限，其中RC_PERMISSION是权限申请码，用来标志权限申请的
            ActivityCompat.requestPermissions(this, permissions, RC_PERMISSION)
        } else {
        }
    }

    override fun onClick(v: View?) {

        NetworkHelper.getInstance().requestLicense { license, error ->

            runOnUiThread {

                if (!TextUtils.isEmpty(license)) {
                   // Toast.makeText(this, license, Toast.LENGTH_SHORT).show()
                    LivenessDetectionSDK.enableSDKHandleCameraPermission()
                    LivenessDetectionSDK.from(this@LivenessDemoActivity)
                        .setLicense(license)
                        .setRequestCode(REQUEST_LIVENESS_CODE).start()
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LIVENESS_CODE) {
            if (LivenessResult.isSuccess()) {
                val bitmap = LivenessResult.getLivenessBitmap()
                val intent = Intent(this, DetectionSuccessActivity::class.java)
                intent.putExtra("result", true)
                startActivity(intent)
            } else {
                if (!TextUtils.isEmpty(LivenessResult.getErrorMsg())) {
                    Toast.makeText(this, LivenessResult.getErrorMsg(), Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this, DetectionSuccessActivity::class.java)
                intent.putExtra("result", false)
                startActivity(intent)
            }
        }
    }

    companion object {
        private val permissions = arrayOf(Manifest.permission.CAMERA)
        private const val RC_PERMISSION = 101
        private const val REQUEST_LIVENESS_CODE = 10001
    }
}