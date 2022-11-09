package com.izilab.livenessdemo

import android.text.TextUtils
import android.util.Log
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class NetworkHelper {

    var mClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS) //设置连接超时时间
        .readTimeout(20, TimeUnit.SECONDS).build() //设置读取超时时间


    fun requestLicense(callback: (String, String) -> Unit) {

        if (TextUtils.isEmpty(URL)) {
            callback.invoke("be9a61c9b6790d149df811c838148509a4273de8b14ad2bded6d14456a3c1afd","")
            return
        }
        val request: Request = Request.Builder()
            .url(URL)
            .build()
        val call = mClient.newCall(request)

        try {
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.invoke("", e.localizedMessage)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body
                    val jsonObject = JSONObject(responseBody!!.string())
                    val status = jsonObject.optString("status")
                    val messageObj = jsonObject.optJSONObject("message")
                    if (status == "OK") {
                        val license = messageObj.optString("license")
                        callback.invoke(license, "")
                    } else {
                        callback.invoke("", jsonObject.toString())
                    }
                }
            })

        } catch (e: IOException) {
            Log.d("ok", "error:" + e.message)
            e.printStackTrace()
            callback.invoke("", e.localizedMessage)
        } catch (e: JSONException) {
            callback.invoke("", e.localizedMessage)
            e.printStackTrace()
        }
    }

    companion object {
        private val sInstance = NetworkHelper()
        const val URL = ""
        fun getInstance(): NetworkHelper {
            return sInstance
        }
    }

}