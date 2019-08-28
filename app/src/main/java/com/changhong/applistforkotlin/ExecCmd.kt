package com.changhong.applistforkotlin

fun runExecCmd(cmd: String) {
    val runtime = Runtime.getRuntime()
    runtime.exec(cmd)
}