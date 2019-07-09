package com.mariefismi02.messagehiding

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mariefismi02.messagehiding.db.LogData
import com.mariefismi02.messagehiding.utils.UriUtils
import org.jetbrains.anko.find

class LogDataAdapter(private val context: Context, private val items: MutableList<LogData>, private val listener: (LogData) -> Unit) : RecyclerView.Adapter<LogDataAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.log_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        holder.bindItem(items[i], listener)
    }

    fun clearData() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){

        private val type = view.find<TextView>(R.id.logType)
        private val name = view.find<TextView>(R.id.logName)
        private val date = view.find<TextView>(R.id.logCreated)
        private val typeImage = view.find<ImageView>(R.id.logTypeImage)

        fun bindItem(item: LogData, listener: (LogData) -> Unit){
            val icon = if(item.type=="encrypt") R.drawable.ic_lock_white else R.drawable.ic_lock_open_white
            typeImage.setImageResource(icon)
            type.text = item.type?.capitalize()
            name.text = UriUtils.UriFileName(itemView.context, Uri.parse(item.imageURI))
            date.text = item.executedTime
            itemView.setOnClickListener{listener(item)}
        }
    }
}