package com.stonehiy.upgrade.base.core


/**
 * 下载监听
 */
interface DownloadListener {
    fun onPrepare(downloadId: Long, taskId: Int) //准备
    fun onDownLoading(progress: Int, taskId: Int)//下载中
    fun onSuccess(path: String?, taskId: Int) //下载成功
    fun onFailed(throwable: Throwable?, taskId: Int) //下载失败
}










