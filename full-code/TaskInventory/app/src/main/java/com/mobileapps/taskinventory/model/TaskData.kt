package com.mobileapps.taskinventory.model

import androidx.room.Entity
import androidx.room.PrimaryKey


enum class Category(val code: String, val label: String) {
    NORMAL("N", "Normal"),
    URGENT("U", "Urgent"),
    IMPORTANT("I", "Important")
}

enum class Status(val label: String) {
    NEW("New"),
    IN_PROGRESS("In Progress"),
    DONE("Done")
}


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val description: String,
    val category: String,
    val status: String,
    val createdTime: Long,
    val finishedTime: Long?,
    val duration: Long?
)
