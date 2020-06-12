package com.stonehiy.upgrade.base.core

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class DownloadManagerReceiver : BroadcastReceiver() {
    val TAG = "DownloadManagerReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        when (action) {
            DownloadManager.ACTION_NOTIFICATION_CLICKED -> {
                Log.d(TAG, "点击了通知")
                // 点击下载进度通知时, 对应的下载ID以数组的方式传递
                val ids =
                    intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS)
                Log.d(TAG, "ids: " + Arrays.toString(ids))
            }
            DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                Log.d(TAG, "下载完成")
                //获取下载完成对应的下载ID, 这里下载完成指的不是下载成功, 下载失败也算是下载完成,
                //所以接收到下载完成广播后, 还需要根据 id 手动查询对应下载请求的成功与失败.
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                Log.d(TAG, "id: $id")

            }
        }

    }
}