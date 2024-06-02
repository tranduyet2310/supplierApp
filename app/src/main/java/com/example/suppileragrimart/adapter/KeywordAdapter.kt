package com.example.suppileragrimart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.suppileragrimart.R

class KeywordAdapter (context: Context, keyword: ArrayList<String>
) : ArrayAdapter<String>(context, 0, keyword) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val listItemView: View

        if(convertView == null){
            listItemView = LayoutInflater.from(context).inflate(R.layout.word_list_item, parent, false)
        } else {
            listItemView = convertView
        }

        val currentKeyWord: String? = getItem(position)

        val name = listItemView.findViewById<TextView>(R.id.tvKeyword)
        name.text = currentKeyWord

        return listItemView
    }
}