package com.example.flagwire.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flagwire.R
import com.example.flagwire.activity.DescriptionActivity
import com.example.flagwire.model.todays_list.TodaysDataItem
import com.google.android.material.textview.MaterialTextView

class TodaysFlagListAdapter (
    val items: ArrayList<TodaysDataItem>,
    val context: Context
    ) : RecyclerView.Adapter<TodaysFlagListAdapter.ViewHolder>() {


        class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

            val flag_name = itemView.findViewById(R.id.txt_flag_name) as MaterialTextView
            val date = itemview.findViewById(R.id.txt_flag_hosting_date) as MaterialTextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_todays_list, parent, false)
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.flag_name.setText(items.get(position).flagName)
            holder.date.setText(items.get(position).flagHostingDate)

            holder.itemView.setOnClickListener {

                val intent  = Intent(context,DescriptionActivity::class.java)
                intent.putExtra("description",items.get(position).flagDescription)
                intent.putExtra("flagSource",items.get(position).flagSource)
                intent.putExtra("image_url","https://konnecthost.in/Flagwire/assets/upload/flag/"+items.get(position).flagImage)
                context.startActivity(intent)

            }


        }
}