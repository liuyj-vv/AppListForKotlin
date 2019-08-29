package com.changhong.applistforkotlin

import android.util.Log

inline infix fun <T : Any> T?.ifNull(block: (T?) -> T): T {
    if (this == null) {
        return block(this)
    }
    return this
}

fun logi(msg: String) {
    Log.i("exec", msg)
}


fun logii(msg: String) {
    Log.i("sssssssssssssssss", msg)
}

interface ExecCallback {
    fun std(str: String) //其他名称都可以不写operator
}