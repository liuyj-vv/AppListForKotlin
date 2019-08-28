package com.changhong.applistforkotlin

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val KEY_PROCESS_NAME = "processName"
    val KEY_UID = "uid"
    val KEY_PID = "pid"
    val KEY_PKGS = "pkgs"
    val dataList = arrayListOf<Map<String, Any>>()
    var adapter: ListPsBaseAdapter? = null
    private val execCmd: ExecCmd = ExecCmd()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_running.setOnClickListener(View.OnClickListener {
            val adapter = ListBaseAdapter(dataList)
            listview.adapter = adapter
            updateDataList()
            adapter.notifyDataSetChanged()
        })

        button_installed.setOnClickListener(View.OnClickListener {
            adapter = ListPsBaseAdapter(dataList)
            listview.adapter = adapter
            updatePsDataList()
            adapter?.notifyDataSetChanged()
        })
    }
    fun updateDataList() {
        dataList.clear()
        var activityManager:ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var runningAppProcessInfo = activityManager.runningAppProcesses
        for (runningAppProcessInfo in runningAppProcessInfo) {
            var map = mutableMapOf<String, Any>()
            var pkgList = arrayListOf<String>()

            map.apply {
                put(KEY_PROCESS_NAME, runningAppProcessInfo.processName)
                put(KEY_UID, runningAppProcessInfo.uid.toString())
                put(KEY_PID, runningAppProcessInfo.pid.toString())
            }

            for(pkg in runningAppProcessInfo.pkgList) {
                pkgList.add(pkg)
            }
            map.put(KEY_PKGS, pkgList)

            dataList.add(map)
        }
    }

    inner class ListBaseAdapter(dataList: List<Map<String, Any>>): BaseAdapter() {
        private val innerDataList = dataList
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_listview_item, parent, false)

            view.apply {
                findViewById<TextView>(R.id.textview_processName).text = innerDataList[position][KEY_PROCESS_NAME].toString()
                findViewById<TextView>(R.id.textview_uid).text = innerDataList[position][KEY_UID].toString()
                findViewById<TextView>(R.id.textview_pid).text = innerDataList[position][KEY_PID].toString()
                findViewById<TextView>(R.id.textview_pkg).text = innerDataList[position][KEY_PKGS].toString()
            }
            return view
        }

        override fun getItem(position: Int): Any {
            return innerDataList.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return innerDataList.size
        }
    }

    fun updatePsDataList() {
        dataList.clear()
        execCmd.runExecCmd("ps", object: ExecCallback{
            override fun std(str: String) {
                println(str)
                val arrayStr = str.split("\\s+".toRegex())
                dataList.add(mapOf(KEY_PROCESS_NAME to arrayStr[arrayStr.size-1]))
            }
        })
        execCmd.waitFor()
    }

    inner class ListPsBaseAdapter(dataList: List<Map<String, Any>>): BaseAdapter() {
        private val innerDataList = dataList
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_listview_item, parent, false)

            view.apply {
                findViewById<TextView>(R.id.textview_processName).text = innerDataList[position][KEY_PROCESS_NAME].toString()
                findViewById<TextView>(R.id.textview_uid).visibility = View.GONE
                findViewById<TextView>(R.id.textview_pid).visibility = View.GONE
                findViewById<TextView>(R.id.textview_pkg).visibility = View.GONE
            }

            return view
        }

        override fun getItem(position: Int): Any {
            return innerDataList.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return innerDataList.size
        }
    }
}
