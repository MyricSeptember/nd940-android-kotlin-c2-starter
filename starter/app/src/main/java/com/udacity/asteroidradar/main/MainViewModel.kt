package com.udacity.asteroidradar.main

import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid
import java.util.ArrayList

class MainViewModel : ViewModel() {

    var testList = ArrayList<Asteroid>()

    fun getTestAsteroids():List<Asteroid>{
        testList.add(Asteroid(0,"68347 (2001 KB67)","2020-02-08",1.1,1.1,1.1,1.1,true))
        testList.add(Asteroid(1,"68347 (2001 KB68)","2020-02-08",1.1,1.1,1.1,1.1,true))
        testList.add(Asteroid(2,"68347 (2001 KB69)","2020-02-08",1.1,1.1,1.1,1.1,true))
        testList.add(Asteroid(3,"68347 (2001 KB610)","2020-02-08",1.1,1.1,1.1,1.1,true))

        return testList
    }


}