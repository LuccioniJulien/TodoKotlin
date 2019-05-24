package com.julienluccioni.todo

import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.http.*
import java.lang.reflect.Type

private const val TOKEN = "a2f4c735ae2197a652c930e3782bf578db619fc0"
private const val BASE_URL = "https://beta.todoist.com/API/v8/"

val okHttpClient: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
    val newRequest = chain.request().newBuilder()
        .addHeader("Authorization", "Bearer $TOKEN")
        .build()
    chain.proceed(newRequest)
}.build()

var retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(BASE_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

interface TodoApiService {
    @GET("tasks")
    fun getTasks(@Query("project_id") project_id: Long): Deferred<MutableList<Todo>>

    @Headers("Content-Type: application/json")
    @POST("tasks")
    fun addTask(@Body newTodo: Todo): Deferred<Todo>

    @DELETE("tasks/{id}")
    fun deleteTask(@Path("id") id: String): Deferred<Response<Unit>>

    @POST("tasks/{id}/close")
    fun closeTask(@Path("id") id: String): Deferred<Response<Unit>>

    @POST("tasks/{id}/reopen")
    fun reopenTask(@Path("id") id: String): Deferred<Response<Unit>>
}

object TodoApi {
    val TodoService: TodoApiService by lazy { retrofit.create(TodoApiService::class.java) }
}
