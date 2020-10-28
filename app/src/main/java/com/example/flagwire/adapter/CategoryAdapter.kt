package com.example.flagwire.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.flagwire.R
import com.example.flagwire.model.category.CategoryDataItem
import com.google.android.material.textview.MaterialTextView


class CategoryAdapter (
    val items: ArrayList<CategoryDataItem>,
    val context: Context
    ) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {


    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

            val category_name = itemView.findViewById(R.id.txt_category_name) as MaterialTextView
            val category_description = itemview.findViewById(R.id.txt_category_description) as MaterialTextView
            val radio_red = itemview.findViewById(R.id.checkbox_red) as CheckBox
            val radio_green = itemview.findViewById(R.id.checkbox_green) as CheckBox
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_category_list, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.category_name.setText(items.get(position).categoryName)
            holder.category_description.setText(Html.fromHtml(items.get(position).categoryDescription))

            holder.radio_red.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    items.get(position).reminder_status_red = "true"
                }else{
                    items.get(position).reminder_status_red = "false"
                }
            }

            holder.radio_green.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    items.get(position).reminder_status_green = "true"
                }else{
                    items.get(position).reminder_status_green = "false"
                }
            }

        }
}