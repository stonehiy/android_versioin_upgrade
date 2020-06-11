package com.stonehiy.upgrade.base

interface Upgrade {

    /**
     * apk地址
     */
    fun apkUrl(): String?

    /**
     * 标题
     */
    fun title(): String?

    /**
     * 版本号
     */
    fun versionCode(): Int = 0

    /**
     * 版本名
     */
    fun versionName(): String?

    /**
     * 描述信息
     */
    fun description(): String?

    /**
     * 是否强制更新
     */
    fun force(): Boolean
}