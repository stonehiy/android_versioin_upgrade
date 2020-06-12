package com.stonehiy.upgrade.base.core


/**
 * 下载监听
 */
interface DownloadListener {
    fun onPrepare() //准备
    fun onDownLoading(progress: Int)//下载中
    fun onSuccess(path: String?) //下载成功
    fun onFailed(throwable: Throwable?) //下载失败
}










