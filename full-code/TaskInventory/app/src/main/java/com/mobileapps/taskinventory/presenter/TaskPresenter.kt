package com.mobileapps.taskinventory.presenter

import com.mobileapps.taskinventory.model.TaskModel
import com.mobileapps.taskinventory.view.TaskView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.mobileapps.taskinventory.model.Status
import com.mobileapps.taskinventory.model.Task
import kotlin.String

class TaskPresenter(
    private val view: TaskView,
    private val model: TaskModel,
    private val scope: CoroutineScope
) {

    suspend fun setTasksCount() {
        view.showLoading()
        val tasks = model.getTasks()

        val newCount = tasks.count { it.status == Status.NEW.label }
        val inProgressCount = tasks.count { it.status == Status.IN_PROGRESS.label }
        val doneCount = tasks.count { it.status == Status.DONE.label }

        val result = mapOf(
            "new_count" to newCount,
            "in_progress_count" to inProgressCount,
            "done_count" to doneCount
        )

        view.showData(result)
    }

    fun onAddTaskClicked(
        title: String,
        description: String,
        category: String
    ) {
        if (title.isBlank()) {
            view.showMessage("Title cannot be empty")
            return
        }

        if (description.isBlank()) {
            view.showMessage("Description cannot be empty")
            return
        }

        view.showLoading()

        scope.launch {
            try {
                model.addTask(title, description, category)
            } catch (e: Exception) {
                view.showMessage(e.message ?: "Failed to add task")
            }
        }
    }

    suspend fun loadTasks(status: String?, category: String?) {
        val tasks = model.getTasks()

        val filteredByStatus = status?.let {
            tasks.filter { it.status == status }
        } ?: tasks

        val filteredTasks = category?.let {
            filteredByStatus.filter { it.category == category }
        } ?: filteredByStatus

        view.showData(mapOf("filtered_tasks" to filteredTasks))
    }

    suspend fun onUpdateStatus(task: Task) {

        val updatedTask = when (task.status) {

            Status.NEW.label -> task.copy(
                status = Status.IN_PROGRESS.label
            )

            Status.IN_PROGRESS.label -> task.copy(
                status = Status.DONE.label,
                finishedTime = System.currentTimeMillis(),
                duration = System.currentTimeMillis() - task.createdTime
            )

            else -> task
        }

        model.editTask(updatedTask)
    }

    suspend fun onDeleteTask(task: Task) {
        model.deleteTask(task)
    }
}