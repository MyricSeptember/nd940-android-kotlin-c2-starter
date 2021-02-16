package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.detail.NasaRepository
import kotlinx.coroutines.launch
import java.util.*

enum class AsteroidFilter {
    ASTEROIDS_OF_THE_WEEK,
    ASTEROIDS_OF_THE_DAY
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateToAsteroid = MutableLiveData<Asteroid>()
    val navigateToAsteroid: LiveData<Asteroid>
        get() = _navigateToAsteroid

    private val database = getDatabase(application)

    private val nasaRepository = NasaRepository(database)
    private val selectedDate = MutableLiveData<String>()


    private val asteroidFilter = MutableLiveData(AsteroidFilter.ASTEROIDS_OF_THE_WEEK)

    val asteroids = Transformations.switchMap(asteroidFilter) {
        when (it!!) {
            AsteroidFilter.ASTEROIDS_OF_THE_WEEK -> nasaRepository.allAsteroids
            AsteroidFilter.ASTEROIDS_OF_THE_DAY -> {
                selectedDate.value?.let { it1 -> nasaRepository.getFilteredAsteroids(it1) }
            }
        }
    }


    var testList = ArrayList<Asteroid>()

    init {
        viewModelScope.launch {
            nasaRepository.getAsteroids()
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToAsteroid.value = asteroid
    }

    fun displayAsteroidComplete() {
        _navigateToAsteroid.value = null
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}