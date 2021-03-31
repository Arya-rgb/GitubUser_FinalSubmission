package com.thorin.dicoding.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thorin.dicoding.model.DataUsers
import com.thorin.dicoding.model.UserResponse
import com.thorin.dicoding.model.service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {

    val listUser = MutableLiveData<ArrayList<DataUsers>>()
    var status = MutableLiveData<Boolean?>()

    fun setSearchQuery(query: String) {
        RetrofitClient.apiInstance
            .getSearchUsers(query)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful) {
                        listUser.postValue(response.body()?.items)
                        status.value = true
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    t.message?.let { Log.d("Failure", it) }
                    status.value = false
                }

            })
    }

    fun getSearchQuery(): LiveData<ArrayList<DataUsers>> {
        return listUser
    }

}