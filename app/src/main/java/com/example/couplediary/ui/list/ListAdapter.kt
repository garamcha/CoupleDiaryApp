package com.example.couplediary.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.couplediary.R
import com.example.couplediary.databinding.ListItemBinding
import com.example.couplediary.ui.home.Diary

class ListAdapter( val diaryList: ArrayList<Diary>?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    // 감정 이미지 리스트
    private val emo = arrayListOf(
        R.drawable.ic_grinning, R.drawable.ic_neutral, R.drawable.ic_sadness,
        R.drawable.ic_disturb, R.drawable.ic_crying, R.drawable.ic_angry)
    // 날씨 이미지 리스트
    private val weath = arrayListOf(R.drawable.ic_sun, R.drawable.ic_cloud, R.drawable.ic_rain, R.drawable.ic_snow)

    //커스텀 리스너
    interface OnItemClickListener{ // onclick listener
        fun onItemClick(view: View, position: Int)
    }
    interface OnItemLongClickListener{ // LongClick listener
        fun onLongClick(view: View, position: Int)
    }
    // 객체 저장 변수
    private lateinit var itemClickListener: OnItemClickListener // onclick
    private lateinit var itemLongClickListener: OnItemLongClickListener // Longclick
    // 갹체 전달 메소드
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        itemClickListener = onItemClickListener
    }
    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener){
        itemLongClickListener = onItemLongClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
        = ListViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as ListViewHolder).binding
        val item = diaryList!![position]
        binding.listCount.text = (position+1).toString()
        binding.listDate.text = item.day
        binding.listWeather.setImageResource(weath[item.weather-1])
        binding.listEmotion.setImageResource(emo[item.emotions-1])
    }

    override fun getItemCount(): Int {
        return diaryList?.size ?: 0
    }

    // ListAdapter에서 사용할 list_item layout View Binding
    inner class ListViewHolder(var binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if(pos != RecyclerView.NO_POSITION && itemClickListener != null){
                    itemClickListener.onItemClick(binding.root, pos)
                }
            }
            binding.root.setOnLongClickListener {
                val pos = bindingAdapterPosition
                if(pos != RecyclerView.NO_POSITION && itemClickListener != null){
                    itemLongClickListener.onLongClick(binding.root, pos)
                }
                return@setOnLongClickListener true
            }
        }
    }

}

