package com.rivaldofez.storymessage.page.story

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rivaldofez.storymessage.databinding.SubLoadingRecyclerBinding

class LoadingStateAdapter(private val retry: () -> Unit): LoadStateAdapter<LoadingStateAdapter.LoadingStateViewHolder>() {
    override fun onBindViewHolder(
        holder: LoadingStateAdapter.LoadingStateViewHolder,
        loadState: LoadState
    ) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateAdapter.LoadingStateViewHolder {
        val binding = SubLoadingRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return LoadingStateViewHolder(binding, retry)
    }

    inner class LoadingStateViewHolder(private val binding: SubLoadingRecyclerBinding, retry: () -> Unit): RecyclerView.ViewHolder(binding.root){
        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState){
            if (loadState is LoadState.Error){
                binding.tvErrorMessage.text = "Unable to fetch stories data"
            }

            binding.apply {
                binding.ltLoading.isVisible = loadState is LoadState.Loading
                binding.btnRetry.isVisible = loadState is LoadState.Error
                binding.tvErrorMessage.isVisible = loadState is LoadState.Error
            }
        }
    }
}