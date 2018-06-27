package com.aldieemaulana.take.api

import com.aldieemaulana.take.App
import com.aldieemaulana.take.utils.AmInterceptor
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import okhttp3.OkHttpClient
import android.os.SystemClock
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import java.io.IOException


/**
 * Created by Al on 27/06/2018 for Cermati
 */

interface User {

    @GET("search/users")
    @Headers("Content-Type: application/json")
    fun search(@Query("q") q: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Observable<com.aldieemaulana.take.model.user.Response>

    companion object {
        fun create(): User {

            val builder = OkHttpClient.Builder()
            val dispatcher = Dispatcher()

            dispatcher.maxRequests = 1

            val interceptor = Interceptor { chain ->
                SystemClock.sleep(2550)
                chain.proceed(chain.request())
            }

            builder.addNetworkInterceptor(interceptor)
            builder.dispatcher(dispatcher)

            var client = builder.build()

            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(App.API)
                    .client(client)
                    .build()

            return retrofit.create(User::class.java)
        }
    }

}