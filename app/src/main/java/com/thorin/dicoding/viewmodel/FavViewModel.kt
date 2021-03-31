package com.thorin.dicoding.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.thorin.dicoding.model.DatabaseUser
import com.thorin.dicoding.model.FavoriteUser
import com.thorin.dicoding.model.FavoriteUserDao

class FavViewModel(application: Application) : AndroidViewModel(application) {

    private var userDao: FavoriteUserDao?
    private var userDB: DatabaseUser? = DatabaseUser.getDatabase(application)

    init {
        userDao = userDB?.favoriteUserDao()
    }

    fun getFavUser(): LiveData<List<FavoriteUser>>? {

        return userDao?.getFavoriteUser()

    }

}