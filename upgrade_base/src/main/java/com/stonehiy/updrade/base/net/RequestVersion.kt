package com.stonehiy.updrade.base.net

import android.content.Context
import com.stonehiy.updrade.base.Upgrade


object RequestVersion {

    fun <U : Upgrade> requestVersionNet(context: Context, request: () -> Unit): IVersion<U> {
        val version = Version<U>(context)
        request.invoke()
        return version
    }

    fun requestDownloadNet(request: () -> Unit): Download {
        val download = Download()
        request.invoke()
        return download
    }

}