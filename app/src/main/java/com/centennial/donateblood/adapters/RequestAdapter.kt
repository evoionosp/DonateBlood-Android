package com.centennial.donateblood.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.centennial.donateblood.R
import com.centennial.donateblood.models.Request
import com.centennial.donateblood.utils.Constants


class RequestAdapter(var mContext: Context, var requestList: List<Request>) :
    RecyclerView.Adapter<RequestAdapter.MyViewHolder>() {


    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview_request, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RequestAdapter.MyViewHolder, position: Int) {
        val request = requestList.get(position)
        holder.title.text = request.orgName
        holder.midTitle.text = "BloodGroup Required: "+ Constants.BGArray[request.bloodGroup]
        holder.subTitle.text = request.orgAddress+", "+request.orgPostalCode
    }

   inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var title: TextView = view.findViewById(R.id.tv_main)
        var subTitle: TextView = view.findViewById(R.id.tv_other)
        var midTitle: TextView = view.findViewById(R.id.tv_mid)

    }

}