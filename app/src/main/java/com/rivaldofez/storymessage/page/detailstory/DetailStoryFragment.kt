package com.rivaldofez.storymessage.page.detailstory

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rivaldofez.storymessage.R
import com.rivaldofez.storymessage.data.local.entity.StoryEntity
import com.rivaldofez.storymessage.databinding.FragmentDetailStoryBinding
import com.rivaldofez.storymessage.extension.setLocaleDateFormat
import com.rivaldofez.storymessage.util.wrapEspressoIdlingResource

class DetailStoryFragment : Fragment(){

    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wrapEspressoIdlingResource {
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.explode)
    }}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val story = DetailStoryFragmentArgs.fromBundle(arguments as Bundle).story
        requireActivity().supportPostponeEnterTransition()

        if(story != null){
            binding.apply {
                imgStory.transitionName = "image_${story.id}"
                tvDate.transitionName = "date_${story.id}"
                tvDescription.transitionName = "description_${story.id}"
                tvName.transitionName = "name_${story.id}"
                toolbarDetailStory.title = getString(R.string.detail_toolbar_title, story.name.lowercase().replaceFirstChar { it.titlecase() })
            }

            setStoryToView(story)
            setToolbarAction()
        }
    }

    private fun setToolbarAction(){
        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.toolbarDetailStory)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appCompatActivity.supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbarDetailStory.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setStoryToView(story: StoryEntity){
        binding.apply {
            tvName.text = story.name
            tvDescription.text = story.description
            tvDate.setLocaleDateFormat(story.createdAt)

            Glide
                .with(requireContext())
                .load(story.photoUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        requireActivity().supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        requireActivity().supportStartPostponedEnterTransition()
                        return false
                    }
                })
                .into(imgStory)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}