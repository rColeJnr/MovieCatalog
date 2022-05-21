package com.rick.screen_movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rick.screen_movie.databinding.FragmentMovieCatalogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieCatalogFragment : Fragment() {

    private var _binding: FragmentMovieCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieCatalogViewModel by viewModels()
    private lateinit var adapter: MovieCatalogAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMovieCatalogBinding.inflate(inflater, container, false)
        val mActivity = requireActivity()
        adapter = MovieCatalogAdapter(mActivity, this::onMovieClick)

        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter

        viewModel.movieList.observe(viewLifecycleOwner) { list ->
            viewModel.movieMutableList.addAll(list)
            // ответ API иногда отправляет один и тот же фильм
            viewModel.movieMutableList.toSet()
            adapter.moviesDiffer.submitList(viewModel.movieMutableList.toList())
        }


        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!viewModel.isLoading.value!!) {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == adapter.moviesDiffer.currentList.size - 1) {
                        // проверьте, есть ли еще доступные данные из API
                        if (viewModel.hasMore.value == true) viewModel.loadMoreData()
                    }
                }
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            if (it.isNotBlank())
                Toast.makeText(context, getString(R.string.error_toast_message, it), Toast.LENGTH_LONG)
                    .show()
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner) {
            binding.root.isRefreshing = it
        }

        viewModel.hasMore.observe(viewLifecycleOwner) {
            if (!it) Toast.makeText(context, getString(R.string.no_more_movies), Toast.LENGTH_SHORT)
                .show()
        }

        binding.root.setOnRefreshListener {
            viewModel.refreshData()
        }
        return binding.root
    }

    private fun onMovieClick(position: Int) {
        val action = MovieCatalogFragmentDirections.actionMovieCatalogFragmentToMovieDetailsFragment(adapter.moviesDiffer.currentList[position])
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}