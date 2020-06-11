package com.stonehiy.upgrade.base.net

import android.content.Context
import com.stonehiy.upgrade.base.Upgrade


object RequestVersion {

    @JvmStatic
    fun <U : Upgrade> requestVersionNet(context: Context, request: () -> Unit): IVersion<U> {
        val version = Version<U>(context)
        request.invoke()
        return version
    }

    @JvmStatic
    fun requestDownloadNet(request: () -> Unit): Download {
        val download = Download()
        request.invoke()
        return download
    }

}