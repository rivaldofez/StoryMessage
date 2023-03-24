package com.rivaldofez.storymessage.story

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.data.remote.response.StoryResponse
import com.rivaldofez.storymessage.databinding.FragmentStoryBinding
import com.rivaldofez.storymessage.databinding.ItemStoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StoryFragment : Fragment(), StoryItemCallback {
    private var _binding: FragmentStoryBinding? = null
    private val binding get() = _binding!!


    private val storyViewModel: StoryViewModel by viewModels()
    private lateinit var storyRecyclerView: RecyclerView

    private lateinit var storyAdapter: StoryAdapter
    private lateinit var token: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWlyc25sM1h2cW9fYlZJOXAiLCJpYXQiOjE2Nzk1NjcyOTZ9.YsQQy3NJG6mfEUzJZTql0hrEjs_Hw25xH90AkTOrl9U"
        setupStoryRecyclerView()
        getStories()


        binding.fabCreateStory.setOnClickListener {
            val goToCreateStory = StoryFragmentDirections.actionStoryFragmentToAddStoryFragment()
            findNavController().navigate(goToCreateStory)
        }
    }

    private fun getStories(){

        viewLifecycleOwner.lifecycleScope.launch{
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                storyViewModel.getStories(token = token).collect { result ->
                    result.onSuccess { storiesResponse ->
                        updateRecyclerViewStoryData(stories = storiesResponse.stories)
                    }

                    result.onFailure {
                        Snackbar.make(binding.root, "Login Gagal", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateRecyclerViewStoryData(stories: List<StoryResponse>){
        val recyclerViewState = storyRecyclerView.layoutManager?.onSaveInstanceState()

        storyAdapter.submitList(stories)

        storyRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun setupStoryRecyclerView(){
        val linearLayoutManager = LinearLayoutManager(requireContext())
        storyAdapter = StoryAdapter(this)

        storyRecyclerView = binding.rvStory
        storyRecyclerView.apply {
            adapter = storyAdapter
            layoutManager = linearLayoutManager
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStoryClicked(story: StoryResponse, itemBinding: ItemStoryBinding) {


        itemBinding.apply {
            val goToDetailStory = StoryFragmentDirections.actionStoryFragmentToDetailStoryFragment()
            goToDetailStory.story = story


            imgStory.transitionName = story.id

            val extras = FragmentNavigatorExtras(
                imgStory to "image_${story.id}",
                tvDate to "date_${story.id}",
                tvName to "name_${story.id}",
                tvDescription to "description_${story.id}"
            )

            findNavController().navigate(goToDetailStory, extras)
        }


    }

}