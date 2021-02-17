package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.main.adapter.AsteroidAdapter

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(requireActivity().application)).get(
            MainViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setHasOptionsMenu(true)
        setUpObservers()
        binding.asteroidRecycler.adapter =
            AsteroidAdapter(AsteroidAdapter.AsteroidClickListener { asteroid ->
                viewModel.displayAsteroidDetails(asteroid)
            })

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.navigateToAsteroid.observe(viewLifecycleOwner, Observer { it ->
            it?.let { asteroid ->
                findNavController()
                    .navigate(MainFragmentDirections.actionShowDetail(asteroid))
                viewModel.displayAsteroidComplete()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week_menu -> viewModel.setAsteroidsFilter(AsteroidFilter.ASTEROIDS_OF_THE_WEEK)
            R.id.show_today_menu -> viewModel.setAsteroidsFilter(AsteroidFilter.ASTEROIDS_OF_THE_DAY)
            R.id.show_all_menu -> viewModel.setAsteroidsFilter(AsteroidFilter.ALL_ASTEROIDS)
        }
        return true
    }
}
