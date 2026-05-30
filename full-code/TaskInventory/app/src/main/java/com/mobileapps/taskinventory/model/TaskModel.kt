package com.mobileapps.taskinventory.model

import com.mobileapps.taskinventory.utils.TaskDao

class TaskModel(
    private val taskDao: TaskDao
) {

    suspend fun getTasks(): List<Task> {
        return taskDao.getAllTasks()
    }

    suspend fun addTask(
        title: String,
        description: String,
        category: String
    ) {
        taskDao.insert(
            Task(
                title = title,
                description = description,
                category = category,
                status = Status.NEW.label,
                createdTime = System.currentTimeMillis(),
                finishedTime = null,
                duration = null
            )
        )
    }

    suspend fun getTaskById(id: Int): Task? {
        return taskDao.getTaskById(id)
    }

    suspend fun editTask(task: Task) {
        taskDao.update(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.delete(task)
    }
}