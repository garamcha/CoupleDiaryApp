package com.example.couplediary.ui.home

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.couplediary.CoupleDiaryActivity
import com.example.couplediary.MyDiaryEditActivity
import com.example.couplediary.R

class DiaryAdapter(private val diaryList: ArrayList<Diary>) : RecyclerView.Adapter<DiaryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.diary_main_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = diaryList[position]
        holder.diary_user1.text = currentItem.diary

        // RecyclerView onClickListener
        holder.diary_user1.setOnClickListener{
            val intent = Intent(holder.itemView?.context, CoupleDiaryActivity::class.java)
            intent.putExtra("day", diaryList[position].day)
            startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return diaryList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val diary_user1 : TextView = itemView.findViewById(R.id.diary_user1)
    }
}