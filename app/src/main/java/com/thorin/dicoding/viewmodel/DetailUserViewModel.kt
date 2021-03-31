package com.thorin.dicoding.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.thorin.dicoding.model.DatabaseUser
import com.thorin.dicoding.model.FavoriteUser
import com.thorin.dicoding.model.FavoriteUserDao
import com.thorin.dicoding.model.UserDetailResponse
import com.thorin.dicoding.model.service.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserViewModel(application: Application) : AndroidViewModel(application) {

    val user = MutableLiveData<UserDetailResponse>()
    var status = MutableLiveData<Boolean?>()

    private var userDao: FavoriteUserDao?
    private var userDB: DatabaseUser? = DatabaseUser.getDatabase(application)

    init {
        userDao = userDB?.favoriteUserDao()
    }

    fun setUserDetail(username: String) {
        RetrofitClient.apiInstance
            .getUserDetail(username)
            .enqueue(object : Callback<UserDetailResponse> {
                override fun onResponse(
                    call: Call<UserDetailResponse>,
                    response: Response<UserDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        user.postValue(response.body())
                        status.value = true
                    }
                }

                override fun onFailure(call: Call<UserDetailResponse>, t: Throwable) {
                    t.message?.let { Log.d("Failure", it) }
                    status.value = false
                }
            })
    }

    fun getUserDetail(): LiveData<UserDetailResponse> {
        return user
    }

    fun addToFav(username: String, id: Int, avatar_url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = FavoriteUser(
                username,
                id,
                avatar_url
            )
            userDao?.favoriteAdd(user)
        }
    }

    suspend fun checkUser(id: Int) = userDao?.checkUser(id)

    fun userRemoveFromFav(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao?.deleteUserFav(id)
        }
    }

}