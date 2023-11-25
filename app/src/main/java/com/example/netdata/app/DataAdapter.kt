package com.example.netdata.app

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.netdata.R

class DataAdapter(private val mcontext: Context, private val list: MutableList<List<SpannableString>>): ArrayAdapter<List<SpannableString>>(mcontext, 0, list) {

    private lateinit var sqlManager: SQLManager

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mcontext).inflate(R.layout.item_data, parent, false)
        sqlManager = SQLManager(mcontext)

        val item = list[position]
        val tvId = layout.findViewById<TextView>(R.id.tvid)
        val tvCod = layout.findViewById<TextView>(R.id.tvcod)
        val tvDetails = layout.findViewById<TextView>(R.id.tvdetails)
        val tvMetrics = layout.findViewById<TextView>(R.id.tvmetrics)
        tvId.text = item[0]
        tvCod.text = item[1]
        tvDetails.text = item[2]
        tvMetrics.text = item[3]

        return layout
    }
}