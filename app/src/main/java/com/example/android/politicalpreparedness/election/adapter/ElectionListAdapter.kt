package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionItemBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener) : ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    var elections: List<Election> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getItemCount(): Int = elections.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val electionItem = elections[position]
        holder.itemView.setOnClickListener {
            clickListener.onClick(electionItem)
        }
        holder.bind(electionItem)
    }
}

//ElectionViewHolder
class ElectionViewHolder(private val electionlistItemBinding: ElectionItemBinding) : RecyclerView.ViewHolder(electionlistItemBinding.root) {
    fun bind(item: Election) {
        electionlistItemBinding.election = item
        electionlistItemBinding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ElectionItemBinding.inflate(layoutInflater, parent, false)
            return ElectionViewHolder(binding)
        }
    }
}

//ElectionDiffCallback
class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }

}

//ElectionListener
class ElectionListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}