package com.julienluccioni.todo

import android.os.Parcelable
import com.squareup.moshi.Json

data class Todo(
    @Json(name = "id")
    val id: Long,
    @Json(name = "content")
    val content: String,
    @Json(name = "project_id")
    val project_id: Long,
    @Json(name = "completed")
    var completed: Boolean
)