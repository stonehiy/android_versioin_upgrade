package com.stonehiy.updrade.base.net

import android.content.Context
import com.stonehiy.updrade.base.Upgrade
import android.os.Build


class Net<U : Upgrade>(private val ctx: Context) : INet<U> {

    private var _onDownload: ((u: U, download: Boolean) -> Unit)? = null
    private var _onFail: ((error: String?) -> Unit)? = null

    override fun success(u: U) {
        val versionCode = u.versionCode()
        val packageInfo = ctx
            .packageManager
            .getPackageInfo(ctx.packageName, 0)
        var longVersionCodeLong: Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
        u.let {
            if (it != null) {
                _onDownload?.invoke(it, versionCode > longVersionCodeLong)
            } else {
                fail("apkUrl is null")
            }
        }


    }

    override fun fail(error: String?) {
        _onFail?.invoke(error)
    }

    fun onDownload(listener: (u: U, download: Boolean) -> Unit) {
        _onDownload = listener
    }

    fun onFail(listener: (error: String?) -> Unit) {
        _onFail = listener
    }
}