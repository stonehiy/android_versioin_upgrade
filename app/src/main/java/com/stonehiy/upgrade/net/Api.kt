package com.stonehiy.upgrade.net

import com.stonehiy.upgrade.entity.VersionEntity
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("versionJson.json")
    fun checkVersion(): Deferred<Response<VersionEntity>>

    @GET("{apkUrl}")
    fun downloadApk(@Path("apkUrl") apkUrl: String): Deferred<Response<Any>>
}