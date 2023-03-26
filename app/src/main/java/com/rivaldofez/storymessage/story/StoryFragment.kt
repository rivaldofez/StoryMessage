package com.rivaldofez.storymessage.story

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
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
import com.rivaldofez.storymessage.databinding.DialogConfirmationBinding
import com.rivaldofez.storymessage.databinding.FragmentStoryBinding
import com.rivaldofez.storymessage.databinding.ItemStoryBinding
import com.rivaldofez.storymessage.extension.animateVisibility
import dagger.hilt.android.AndroidEntryPoint
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
    ): View {
        _binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarAction()
        setStoriesDataToView()
        setActions()
    }

    private fun setToolbarAction(){
        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.toolbarStory)
        appCompatActivity.supportActionBar?.setDisplayShowTitleEnabled(false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.story_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId){
                    R.id.submenu_language -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        true
                    }
                    R.id.submenu_theme_default -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        true
                    }
                    R.id.submenu_theme_light -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        true
                    }
                    R.id.submenu_theme_dark -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        true
                    }
                    R.id.submenu_logout -> {
                        showConfirmationDialog(getString(R.string.dialog_confirmation_logout))
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setStoriesDataToView(){
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            storyViewModel.getAuthenticationToken().collect { token ->
                if (!token.isNullOrEmpty()){
                    this@StoryFragment.token = token
                    setupStoryRecyclerView()
                    getStories()
                    setSwipeRefresh()
                }
            }
        }
    }

    private fun setActions(){
        binding.fabCreateStory.setOnClickListener {
            val goToCreateStory = StoryFragmentDirections.actionStoryFragmentToAddStoryFragment()
            findNavController().navigate(goToCreateStory)
        }
    }

    private fun showConfirmationDialog(message: String){
        val dialogBinding = DialogConfirmationBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.apply {
            tvDialogMessage.text = message

            btnYes.setOnClickListener {
                storyViewModel.saveAuthenticationToken(token = "")
                val goToLogin = StoryFragmentDirections.actionStoryFragmentToLoginFragment()
                findNavController().navigate(goToLogin)
                dialog.dismiss()
            }

            btnNo.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun setSwipeRefresh(){
        binding.srlStory.setOnRefreshListener {
            getStories()
        }
    }

    private fun getStories(){

        viewLifecycleOwner.lifecycleScope.launch{
            binding.srlStory.isRefreshing = true
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                storyViewModel.getStories(token = token).collect { result ->
                    result.onSuccess { storiesResponse ->
                        updateRecyclerViewStoryData(stories = storiesResponse.stories)
                        binding.srlStory.isRefreshing = false
                        showError(storiesResponse.stories.isEmpty())
                    }

                    result.onFailure {
                        Snackbar.make(binding.root, getString(R.string.error_while_fetch_stories), Snackbar.LENGTH_SHORT).show()
                        showError(true)
                        binding.srlStory.isRefreshing = false
                    }
                }
            }
        }
    }

    private fun showError(isError: Boolean){
            binding.layoutError.root.animateVisibility(isError)
            binding.rvStory.animateVisibility(!isError)
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