package com.changhong.applistforkotlin

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.ArrayMap
import android.util.ArraySet
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var listBaseAdapter: ListBaseAdapter
    var dataList = arrayListOf<Map<String, Any>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listBaseAdapter = ListBaseAdapter(dataList)
        listview.adapter = listBaseAdapter

        button_running.setOnClickListener(View.OnClickListener {
            updateDataList()
        })
    }

    fun updateDataList() {
        dataList.clear()
        var activityManager:ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var runningAppProcessInfo = activityManager.runningAppProcesses
        for (runningAppProcessInfo in runningAppProcessInfo) {
            var map = mutableMapOf<String, Any>()
            var pkgList = arrayListOf<String>()

            map.put("processName", runningAppProcessInfo.processName)
            map.put("uid", runningAppProcessInfo.uid.toString())
            map.put("pid", runningAppProcessInfo.pid.toString())

            for(pkg in runningAppProcessInfo.pkgList) {
                pkgList.add(pkg)
            }
            map.put("pkgs", pkgList)

            dataList.add(map)
        }

        listBaseAdapter.notifyDataSetChanged()
    }

    inner class ListBaseAdapter(dataList: List<Map<String, Any>>): BaseAdapter() {
        private var innerDataList = dataList
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var layoutInflater = LayoutInflater.from(parent?.context)
            var view = layoutInflater.inflate(R.layout.layout_listview_item, parent, false)

            view.findViewById<TextView>(R.id.textview_processName).text = innerDataList[position]["processName"].toString()
            view.findViewById<TextView>(R.id.textview_uid).text = innerDataList[position]["uid"].toString()
            view.findViewById<TextView>(R.id.textview_pid).text = innerDataList[position]["pid"].toString()
            view.findViewById<TextView>(R.id.textview_pkg).text = innerDataList[position]["pkgs"].toString()

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
