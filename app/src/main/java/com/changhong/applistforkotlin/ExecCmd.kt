package com.changhong.applistforkotlin

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.Executors

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

fun runExecCmd(cmd: String) {
    var process: Process?
    val command = arrayOf("sh", "-c", cmd)
    val runtime = Runtime.getRuntime()

    logi("[[[[[ start ]]]]]  ${command[0]} ${command[1]} ${command[2]} =================== ")
    process = runtime.exec(command)
    printExecStdoutMessage(process)
    printExecStderrMessage(process)

    process?.let { it ->
        execThread {
            it.waitFor()
            it.destroy()
            process = null
            logi("[[[[[ end ]]]]]  ${command[0]} ${command[1]} ${command[2]} =================== ")
        }
    }
}

fun printExecMessage(tag: String, input: InputStream) {
    execThread {
        logi("这里是$tag start: ${Thread.currentThread().name} ${input.toString()}")

        InputStreamReader(input).use { reader ->
            BufferedReader(reader).use { bufferedReader ->
                while (true) {
                    try {
                        bufferedReader.readLine()?.let {
                            logi("$tag: $it")
                        } ifNull {
                            Thread.sleep(100)
                        }
                    } catch (e: Exception) {
                        break
                    }
                }
            }
        }

        logi("这里是$tag end: ${Thread.currentThread().name}")
    }
}

fun printExecStdoutMessage(process: Process?) {
    process?.inputStream?.let { printExecMessage("stdout", it) }
}

fun printExecStderrMessage(process: Process?) {
    process?.errorStream?.let { printExecMessage("stderr", it) }
}