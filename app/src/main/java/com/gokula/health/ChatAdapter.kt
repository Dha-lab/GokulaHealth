package com.gokula.health.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gokula.health.databinding.ItemChatAiBinding
import com.gokula.health.databinding.ItemChatUserBinding
import com.gokula.health.models.ChatMessage

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<ChatMessage>()
    fun submit(list: List<ChatMessage>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    companion object {
        const val TYPE_USER = 0
        const val TYPE_AI = 1
    }

    override fun getItemViewType(position: Int) = if (items[position].isUser) TYPE_USER else TYPE_AI

    inner class UserVH(val binding: ItemChatUserBinding) : RecyclerView.ViewHolder(binding.root)
    inner class AiVH(val binding: ItemChatAiBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_USER) {
            UserVH(ItemChatUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            AiVH(ItemChatAiBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = items[position]
        when (holder) {
            is UserVH -> holder.binding.tvMessage.text = msg.content
            is AiVH -> holder.binding.tvMessage.text = msg.content
        }
    }

    override fun getItemCount() = items.size
}