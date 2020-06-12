package com.stonehiy.upgrade.base.core

import android.content.Context
import com.stonehiy.upgrade.base.Upgrade


object RequestVersion {

    @JvmStatic
    fun <U : Upgrade> versionNet(context: Context): VersionListener<U> {
        return Version(context)
    }

}