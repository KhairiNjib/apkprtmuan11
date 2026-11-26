package com.example.apkprtmuan11

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.apkprtmuan11.data.model.Post

class ReportAdapter(context: Context) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)
    private val reports = mutableListOf<Post>()

    fun replace(items: List<Post>) {
        reports.clear()
        reports.addAll(items)
        notifyDataSetChanged()
    }

    override fun getCount() = reports.size

    override fun getItem(position: Int) = reports[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = convertView ?: inflater.inflate(R.layout.item_report, parent, false)
        val report = getItem(position)
        row.findViewById<TextView>(R.id.reportTitle).text = report.title
        row.findViewById<TextView>(R.id.reportBody).text = report.body
        return row
    }
}
