package com.stonehiy.upgrade.net

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class ViewModelCoroutineScope : CoroutineScope {


    // 创建一个Job,并用这个job来管理你的ViewModelCoroutineScope的所有子协程
    private val job = SupervisorJob()


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    // 当生命周期销毁时，结束所有子协程
    fun close() {
        job.cancel()
    }

}
