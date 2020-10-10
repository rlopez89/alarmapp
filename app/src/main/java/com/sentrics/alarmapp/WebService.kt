package com.sentrics.alarmapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class Post(val id: Int, val userId: Int, val title: String, val body: String)

interface WebService {

    @GET("posts/{user}")
    suspend fun getPost(@Path("user") user: String): Response<Post>
}