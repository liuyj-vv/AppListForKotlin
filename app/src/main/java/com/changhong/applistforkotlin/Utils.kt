package com.changhong.applistforkotlin

import android.util.Log
import java.util.concurrent.Executors
import kotlin.concurrent.thread

inline infix fun <T : Any> T?.ifNull(block: (T?) -> T): T {
    if (this == null) {
        return block(this)
    }
    return this
}


fun logi(msg: String) {
    Log.i("exec", msg)
}

fun execThread(f: () -> Unit) {
    Executors.newSingleThreadExecutor().execute(f)
}

interface ExecCallback {
    fun std(str: String) //其他名称都可以不写operator
}