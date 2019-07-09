package com.mariefismi02.messagehiding

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.Toast
import com.mariefismi02.messagehiding.db.LogData
import com.mariefismi02.messagehiding.db.database
import kotlinx.android.synthetic.main.fragment_log.*
import org.jetbrains.anko.db.SelectQueryBuilder
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.sdk25.coroutines.onItemSelectedListener
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.toast

class LogFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {


    private var items: MutableList<LogData> = mutableListOf()
    private lateinit var adapter : LogDataAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swipeContainer.isRefreshing = true

        adapter = LogDataAdapter(context!!, items){

            val layout = if(it.type=="encrypt") R.layout.layout_encrypt_detail else R.layout.layout_decrypt_detail

            context?.startActivity<DetailActivity>( "stegoData" to it.logToStegoData(), "layout" to layout, "log" to true)
        }
        swipeContainer.setOnRefreshListener(this)
        logList.layoutManager = LinearLayoutManager(context)
        logList.adapter = adapter

        showData()

        logSpinner.onItemSelectedListener = this
    }

    override fun onRefresh() {
        showData()
    }

    private fun showData(whereArgs: String? = null){
        items.clear()
        context?.database?.use {
            swipeContainer.isRefreshing = false
            var result : SelectQueryBuilder
            if(whereArgs==null)
                result = select(LogData.TABLE_NAME)
            else
                result = select(LogData.TABLE_NAME).whereArgs(whereArgs)
            val favorite = result.parseList(classParser<LogData>())
            items.addAll(favorite)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val type = resources.getStringArray(R.array.log_type).get(position).toLowerCase()

        if(type=="all"){
            showData()
        } else {
            showData("${LogData.TYPE} == '${type}'")
        }
    }
}