package com.udacity.asteroidradar.detail

import android.util.Log
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.network.NasaApi
import com.udacity.asteroidradar.network.NasaApiService
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import com.udacity.asteroidradar.util.addDaysToCurrentDate
import com.udacity.asteroidradar.util.formattedDate
import com.udacity.asteroidradar.util.getToday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class NasaRepository() {
    var client: NasaApiService = NasaApi.retrofitService

    suspend fun getAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidsStringFormat = client.getAsteroids(
                    getToday().formattedDate,
                    addDaysToCurrentDate(Constants.DEFAULT_END_DATE_DAYS).formattedDate, API_KEY
                )
                val networkAsteroidsList =
                    parseAsteroidsJsonResult(JSONObject(asteroidsStringFormat))
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