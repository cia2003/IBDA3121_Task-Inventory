package com.mobileapps.taskinventory

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobileapps.taskinventory.model.Category
import com.mobileapps.taskinventory.view.TaskView
import com.mobileapps.taskinventory.model.Status

class TaskListActivity : AppCompatActivity(), TaskView {
    private lateinit var normalButton: Button
    private lateinit var UrgentButton: Button
    private lateinit var importantButton: Button
    private lateinit var backButton: Button
    private lateinit var statusPageTitle: TextView

    private var status: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task_list)

        status = intent.getStringExtra("status")

        val fragment = TaskList_Fragment.newInstance(status)

        setViews()
        setListeners(fragment)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment).commit()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setViews() {
        normalButton = findViewById<Button>(R.id.btnNormal)
        UrgentButton = findViewById<Button>(R.id.btnUrgent)
        importantButton = findViewById<Button>(R.id.btnImportant)
        backButton = findViewById<Button>(R.id.backButton)
        statusPageTitle = findViewById<TextView>(R.id.statusPageTitle)

        if(status == Status.DONE.label) {
            statusPageTitle.text = "Task $status"
        } else {
            statusPageTitle.text = "$status Task"
        }

    }

    private fun setListeners(fragment: TaskList_Fragment) {
        val buttonsWithActions = listOf(
            normalButton to { fragment.filterByCategory(Category.NORMAL.label) },
            UrgentButton to { fragment.filterByCategory(Category.URGENT.label) },
            importantButton to { fragment.filterByCategory(Category.IMPORTANT.label) },
            backButton to { finish() }
        )

        buttonsWithActions.forEach { (button, action) ->
            button.setOnClickListener { action() }
        }
    }

    override fun showData(data: Map<String, Any>) {
        println("Hello")
    }

    override fun showLoading() {
//        progressBar.visibility = View.VISIBLE
    }

    override fun showMessage(message: String) {
        Toast.makeText(
            this,
            "Message: $message",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun hideLoading() {
//        progressBar.visibility = View.GONE
    }


}