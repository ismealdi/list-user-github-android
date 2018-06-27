package com.aldieemaulana.take.utils


import android.os.SystemClock
import android.util.Log

import org.json.JSONException

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Created by Al on 27/06/2018 for Cermati
 */

class AmInterceptor : Interceptor {
    private val TAG = javaClass.simpleName

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code() == 403) {
            return tryRequest(request, chain)
        }

        Log.d(TAG, "INTERCEPTED:$ " + response.toString())
        return response
    }

    @Throws(JSONException::class, IOException::class)
    private fun tryRequest(req: Request, chain: Interceptor.Chain): Response {
        SystemClock.sleep(60000)
        Log.d(TAG, "Retrying new request")
        val newRequest: Request = req.newBuilder().build()
        val another = chain.proceed(newRequest)

        return another
    }
}