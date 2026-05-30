package com.mobileapps.taskinventory.utils
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.mobileapps.taskinventory.model.Task
import android.view.View
import android.view.ViewGroup
import com.mobileapps.taskinventory.R
import android.view.LayoutInflater
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Button


interface onTaskClickListener {
    fun onDetailClick(task: Task)
}
class TaskAdapter(
    private val listener: onTaskClickListener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private val tasks = mutableListOf<Task>()

    fun submitList(data: List<Task>) {
        tasks.clear()
        tasks.addAll(data)
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val time: TextView = view.findViewById(R.id.tvCreatedTime)
        val category: TextView = view.findViewById(R.id.tvCategory)
        val detailButton: Button = view.findViewById(R.id.detailButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val formattedTime = formatter.format(Date(task.createdTime))

        holder.title.text = task.title
        holder.time.text = formattedTime
        holder.category.text = task.category
        holder.detailButton.setOnClickListener {
            listener.onDetailClick(task)
        }
    }

    override fun getItemCount() = tasks.size

}
