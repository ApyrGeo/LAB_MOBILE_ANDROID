package com.example.lab_mobile.todo.data.remote

import com.example.lab_mobile.todo.data.Item
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ItemService {
    @GET("/api/boardgames")
    suspend fun find(@Header("Authorization") authorization: String): List<Item>

    @GET("/api/boardgames/{id}")
    suspend fun read(
        @Header("Authorization") authorization: String,
        @Path("id") itemId: String?
    ): Item;

    @Headers("Content-Type: application/json")
    @POST("/api/boardgames")
    suspend fun create(@Header("Authorization") authorization: String, @Body item: Item): Item

    @Headers("Content-Type: application/json")
    @PUT("/api/boardgames/{id}")
    suspend fun update(
        @Header("Authorization") authorization: String,
        @Path("id") itemId: String?,
        @Body item: Item
    ): Item
}
