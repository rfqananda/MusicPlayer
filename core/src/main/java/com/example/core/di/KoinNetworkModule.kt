package com.example.core.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.core.wrapper.NetworkResponseAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val networkResponseAdapterFactory = "networkResponseAdapterFactory"

fun networkModule() =
    module {
        single(named(networkResponseAdapterFactory)) { NetworkResponseAdapterFactory() }
        single {
            provideInternalRetrofit(
                baseUrl = "https://itunes.apple.com/",
                ctx = androidContext(),
                get(named(networkResponseAdapterFactory))
            )
        }
    }

internal fun provideInternalRetrofit(
    baseUrl: String,
    ctx: Context,
    networkResponseAdapterFactory: NetworkResponseAdapterFactory
): Retrofit {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BODY

    val client = OkHttpClient.Builder()
        .addInterceptor(ChuckerInterceptor(ctx))
        .addInterceptor(interceptor)
        .build()
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(networkResponseAdapterFactory).build()
}


inline fun <reified T> provideApi(retrofit: Retrofit): T = retrofit.create(T::class.java)