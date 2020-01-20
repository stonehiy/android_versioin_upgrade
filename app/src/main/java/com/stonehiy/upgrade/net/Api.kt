package com.stonehiy.upgrade.net

import com.stonehiy.upgrade.entity.VersionEntity
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface Api {

    @GET("versionJson.json")
    fun checkVersion(): Deferred<Response<VersionEntity>>

    @Streaming
    @GET
    fun downloadApk(@Url fileUrl: String): Deferred<Response<ResponseBody>>
}