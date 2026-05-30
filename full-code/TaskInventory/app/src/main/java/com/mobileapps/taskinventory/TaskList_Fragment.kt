package com.mobileapps.taskinventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobileapps.taskinventory.model.Task
import com.mobileapps.taskinventory.model.TaskModel
import com.mobileapps.taskinventory.presenter.TaskPresenter
import com.mobileapps.taskinventory.utils.TaskAdapter
import com.mobileapps.taskinventory.utils.onTaskClickListener
import com.mobileapps.taskinventory.view.TaskView
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import com.mobileapps.taskinventory.model.AppDatabase


class TaskList_Fragment : Fragment(), TaskView {
    private lateinit var adapter: TaskAdapter
    private lateinit var presenter: TaskPresenter
    private lateinit var model: TaskModel

    private lateinit var tasks: List<Task>

    companion object {
        private const val ARG_STATUS = "ARG_STATUS"

        fun newInstance(status: String?): TaskList_Fragment {
            val fragment = TaskList_Fragment()
            val bundle = Bundle()

            bundle.putString(ARG_STATUS, status)
            fragment.arguments = bundle

            return fragment
        }
    }

    private var status: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val db = AppDatabase.getInstance(requireContext())
        model = TaskModel(db.taskDao())
        presenter = TaskPresenter(this, model, lifecycleScope)
        adapter = TaskAdapter(object : onTaskClickListener {
            override fun onDetailClick(task: Task) {
                // Buat Dialog untuk Next Action
                taskWithAction(task)
            }
        })

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_task_list_, container, false)
        val listView = view.findViewById<RecyclerView>(R.id.rvTask)

        listView.layoutManager = LinearLayoutManager(requireContext())
        listView.adapter = adapter

        lifecycleScope.launch {
            status = arguments?.getString(ARG_STATUS)
            presenter.loadTasks(status, null)
        }

        return view
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showData(data: Map<String, Any>) {
        tasks = data["filtered_tasks"] as? List<Task> ?: emptyList()
        adapter.submitList(tasks)
    }

    override fun showMessage(message: String) {
        Toast.makeText(
            requireContext(),
            "Message: $message",
            Toast.LENGTH_LONG
        ).show()
    }

    fun filterByCategory(category: String) {
        lifecycleScope.launch {
            status = arguments?.getString(ARG_STATUS)
            presenter.loadTasks(status, category)
        }
    }

    fun taskWithAction(task: Task) {
        // TODO("Buat dialog yang berisi tombol action (Take atau Done tergantung dari status")
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(task.title)
        builder.setMessage(task.description ?: "Tidak ada deskripsi")

        when (task.status) {
            "New" -> {
                builder.setPositiveButton("Take") { dialog, _ ->
                    // TODO: logika ketika task diambil
                    showMessage("Task '${task.title}' diambil")

                    lifecycleScope.launch {
                        presenter.onUpdateStatus(task)
                        presenter.loadTasks(status, null)
                    }
                    dialog.dismiss()
                }
            }

            "In Progress" -> {
                builder.setPositiveButton("Done") { dialog, _ ->
                    // TODO: logika ketika task selesai
                    showMessage("Task '${task.title}' selesai")
                    lifecycleScope.launch {
                        presenter.onUpdateStatus(task)
                        presenter.loadTasks(status, null)
                    }
                    dialog.dismiss()
                }

            }

            "Done" -> {
                builder.setPositiveButton("Delete") { dialog, _ ->
                    // TODO: logika ketika task dihapus
                    showMessage("Task '${task.title}' dihapus")
                    lifecycleScope.launch {
                        presenter.onDeleteTask(task)
                        presenter.loadTasks(status, null)
                    }
                    dialog.dismiss()
                }
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
}