package com.udacity.asteroidradar.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import com.udacity.asteroidradar.util.addDaysToCurrentDate
import com.udacity.asteroidradar.util.formattedDate
import com.udacity.asteroidradar.util.getToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class NasaRepository(private val database: AsteroidDatabase) {

    var client = NasaApi.retrofitService

    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it?.asDomainModel()
        }

    fun getFilteredAsteroids(selectedDate: String): LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getAsteroidsOfTheDay(selectedDate)) {
            it?.asDomainModel()
        }
    }


    suspend fun getAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidsStringFormat = client.getAsteroids(
                    getToday().formattedDate,
                    addDaysToCurrentDate(Constants.DEFAULT_END_DATE_DAYS).formattedDate, API_KEY
                )
                val networkAsteroidsList =
                    parseAsteroidsJsonResult(JSONObject(asteroidsStringFormat))

                val databaseAsteroidsList = networkAsteroidsList.asDatabaseModel()
                database.asteroidDao.insertAll(*databaseAsteroidsList)
            } catch (e: Exception) {
                Log.d("ExceptionInRepo", e.toString())
            }
        }
    }

    companion object {
        val START_DATE = "2021-02-11"
        val END_DATE = "2021-02-18"
    }
}