package com.stonehiy.updrade.base.net

import android.content.Context
import com.stonehiy.updrade.base.Upgrade


object RequestNet {

    fun <U : Upgrade> requestVersionNet(context: Context, request: () -> Unit): INet<U> {
        request.invoke()
        return Net(context)
    }

}