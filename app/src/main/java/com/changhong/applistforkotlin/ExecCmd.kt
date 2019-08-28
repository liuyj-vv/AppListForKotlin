package com.changhong.applistforkotlin
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.locks.ReentrantLock

class ExecCmd{
    val lock = ReentrantLock()
    private var process: Process? = null
    private var threadWait: Thread? = null
    private var threadStdout: Thread? = null
    private var threadStderr: Thread? = null

    fun runExecCmd(cmd: String, execCallback: ExecCallback? = null) {
        lock.lock()
        val command = arrayOf("sh", "-c", cmd)
        val runtime = Runtime.getRuntime()

        logi("[[[[[ start ]]]]]  ${command[0]} ${command[1]} ${command[2]} =================== ")
        process = runtime.exec(command)

        threadStdout = printExecStdoutMessage(process, execCallback)
        threadStderr = printExecStderrMessage(process, execCallback)

        process?.let { it ->
            threadWait = Thread {
                it.waitFor()
                it.destroy()
                process = null
                logi("[[[[[ end ]]]]]  ${command[0]} ${command[1]} ${command[2]} =================== ")
            }
        }

        threadWait?.start()
        threadStderr?.start()
        threadStdout?.start()

        lock.unlock()
    }

    fun waitFor() {
        println("waitFor 1")
        threadWait?.join()
        println("waitFor 2")
        threadStdout?.join()
        println("waitFor 3")
        threadStderr?.join()
        println("waitFor 4")
    }

    fun printExecMessage(tag: String, input: InputStream, execCallback: ExecCallback? = null): Thread {
        return Thread {
            var count: Int = 0;
            logi("这里是$tag start: ${Thread.currentThread().name}")

            InputStreamReader(input).use { reader ->
                BufferedReader(reader).use { bufferedReader ->
                    while (true) {
                        try {
                            bufferedReader.readLine()?.let {
                                //logi("$tag: $it")
                                count++
                                execCallback?.std(it)
                            } ifNull {
                                Thread.sleep(100)
                            }
                        } catch (e: Exception) {
                            break
                        }
                    }
                }
            }

            logi("这里是$tag end: ${Thread.currentThread().name} $count")
        }
    }

    fun printExecStdoutMessage(process: Process?, execCallback: ExecCallback? = null): Thread? {
        process?.inputStream?.let { return printExecMessage("stdout", it, execCallback) } ?: return null
    }

    fun printExecStderrMessage(process: Process?, execCallback: ExecCallback? = null): Thread? {
        process?.errorStream?.let { return printExecMessage("stderr", it, execCallback) } ?: return null
    }
}
