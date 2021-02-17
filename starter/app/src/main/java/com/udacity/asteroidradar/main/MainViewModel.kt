package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.NasaRepository
import kotlinx.coroutines.launch

enum class AsteroidFilter {
    ALL_ASTEROIDS,
    ASTEROIDS_OF_THE_WEEK,
    ASTEROIDS_OF_THE_DAY
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateToAsteroid = MutableLiveData<Asteroid?>()
    val navigateToAsteroid: LiveData<Asteroid?>
        get() = _navigateToAsteroid

    private val database = getDatabase(application)

    private val nasaRepository = NasaRepository(database)

    private val asteroidFilter = MutableLiveData(AsteroidFilter.ASTEROIDS_OF_THE_WEEK)

    val pictureOfDay = nasaRepository.pictureOfDay

    val asteroids = Transformations.switchMap(asteroidFilter) {
        when (it!!) {
            AsteroidFilter.ALL_ASTEROIDS -> nasaRepository.allAsteroids
            AsteroidFilter.ASTEROIDS_OF_THE_WEEK -> nasaRepository.getAsteroidsOfTheWeek()
            AsteroidFilter.ASTEROIDS_OF_THE_DAY -> nasaRepository.getAsteroidsOfTheDay()
        }
    }

    init {
        viewModelScope.launch {
            nasaRepository.refreshData()
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToAsteroid.value = asteroid
    }

    fun displayAsteroidComplete() {
        _navigateToAsteroid.value = null
    }

    fun setAsteroidsFilter(filter: AsteroidFilter) {
        asteroidFilter.postValue(filter)
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