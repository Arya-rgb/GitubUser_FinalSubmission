package com.thorin.consumerapp.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thorin.consumerapp.R
import com.thorin.consumerapp.model.DataUsers

class UserListAdapter : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    private val dataList = ArrayList<DataUsers>()

    override fun onCreateViewHolder(viewgroup: ViewGroup, position: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewgroup.context).inflate(R.layout.item_user, viewgroup, false)
        return ViewHolder(view)
    }

    fun setList(users: ArrayList<DataUsers>) {
        dataList.clear()
        dataList.addAll(users)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        viewholder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val id: TextView = itemView.findViewById(R.id.id)
        private val name: TextView = itemView.findViewById(R.id.username)
        private val avatar: ImageView = itemView.findViewById(R.id.avatar)
        private val textID = "Id"

        fun bind(user: DataUsers) {
            id.text = StringBuilder(textID).append(" ${user.id}")
            name.text = user.login
            Glide.with(itemView)
                .load(user.avatar_url)
                .into(avatar)
        }

    }

}