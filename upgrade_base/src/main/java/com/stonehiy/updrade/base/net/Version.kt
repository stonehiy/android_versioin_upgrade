package com.stonehiy.updrade.base.net

import android.content.Context
import com.stonehiy.updrade.base.Upgrade
import android.os.Build


class Version<U : Upgrade>(private val ctx: Context) : IVersion<U> {

    private var _onDownload: ((u: U, download: Boolean) -> Unit)? = null
    private var _onFail: ((error: String?) -> Unit)? = null

    override fun success(u: U) {
        val versionCode = u.versionCode()
        val packageInfo = ctx
            .packageManager
            .getPackageInfo(ctx.packageName, 0)
        val longVersionCodeLong: Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
        _onDownload?.invoke(u, versionCode > longVersionCodeLong)

    }


    override fun fail(error: String?) {
        _onFail?.invoke(error)
    }

    fun onDownload(listener: (u: U, download: Boolean) -> Unit): IVersion<U> {
        _onDownload = listener
        return this
    }

    fun onFail(listener: (error: String?) -> Unit): IVersion<U> {
        _onFail = listener
        return this
    }
}