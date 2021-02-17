package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PictureOfDayDao {
    @Query("SELECT * FROM pictureOfDay")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictureOfDay(pictureOfDay: DatabasePictureOfDay)

    @Query("DELETE FROM pictureOfDay")
    suspend fun deletePictureOfDay()
}