package com.thorin.dicoding.model.service

import com.thorin.dicoding.BuildConfig.GITHUB_TOKEN
import com.thorin.dicoding.model.DataUsers
import com.thorin.dicoding.model.UserDetailResponse
import com.thorin.dicoding.model.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("search/users")
    @Headers("Authorization: $GITHUB_TOKEN")
    fun getSearchUsers(
        @Query("q") query: String
    ): Call<UserResponse>

    @GET("users/{username}")
    @Headers("Authorization: $GITHUB_TOKEN")
    fun getUserDetail(
        @Path("username") username: String
    ): Call<UserDetailResponse>

    @GET("users/{username}/followers")
    @Headers("Authorization: $GITHUB_TOKEN")
    fun getFollowers(
        @Path("username") username: String
    ): Call<ArrayList<DataUsers>>

    @GET("users/{username}/following")
    @Headers("Authorization: $GITHUB_TOKEN")
    fun getFollowing(
        @Path("username") username: String
    ): Call<ArrayList<DataUsers>>
}