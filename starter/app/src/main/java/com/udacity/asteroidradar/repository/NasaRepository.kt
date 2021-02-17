package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import com.udacity.asteroidradar.util.addDaysToCurrentDate
import com.udacity.asteroidradar.util.formattedDate
import com.udacity.asteroidradar.util.getToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

class NasaRepository(private val database: AsteroidDatabase) {

    var client = NasaApi.retrofitService

    val allAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it?.asDomainModel()
        }

    fun getAsteroidsOfTheDay(): LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getAsteroidsOfTheDay(getToday().formattedDate)) {
            it?.asDomainModel()
        }
    }

    fun getAsteroidsOfTheWeek(): LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getAsteroidsForWeek(getToday().formattedDate)) {
            it?.asDomainModel()
        }
    }

    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(database.pictureOfDayDao.getPictureOfDay()) {
            it?.asDomainModel()
        }

    suspend fun refreshData() {
        getAsteroids()
        getPictureOfDay()
    }

    private suspend fun getAsteroids() {
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
                Timber.e(e.toString())
            }
        }
    }


    private suspend fun getPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                val databasePictureOfDay = client.getPictureOfTheDay(API_KEY).asDatabaseModel()

                if (databasePictureOfDay.mediaType == IMAGE_MEDIA_TYPE) {
                    database.pictureOfDayDao.deletePictureOfDay()
                    database.pictureOfDayDao.insertPictureOfDay(databasePictureOfDay)
                } else {
                    Timber.d("video")
                }
            } catch (e: java.lang.Exception) {
                Timber.e(e.toString())
            }
        }
    }

    companion object {
        val IMAGE_MEDIA_TYPE = "image"
        val START_DATE = "2021-02-11"
        val END_DATE = "2021-02-18"
    }
}