package com.stonehiy.upgrade.entity

import com.stonehiy.upgrade.base.Upgrade

data class VersionEntity(
    val title: String?,
    val versionCode: Int,
    val versionName: String,
    val msg: String,
    val force: Boolean,
    val apkUrl: String
) : Upgrade {
    override fun apkUrl(): String? {
        return apkUrl
    }

    override fun title(): String? {
        return title ?: "版本更新啦"
    }

    override fun versionName(): String? {
        return versionName
    }

    override fun description(): String? {
        return msg
    }

    override fun force(): Boolean {
        return force
    }

    override fun versionCode(): Int {
        return versionCode
    }

}