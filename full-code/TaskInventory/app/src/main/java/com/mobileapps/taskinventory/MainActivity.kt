package com.mobileapps.taskinventory

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.mobileapps.taskinventory.model.TaskModel
import com.mobileapps.taskinventory.presenter.TaskPresenter
import com.mobileapps.taskinventory.view.TaskView
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.text.InputType
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.mobileapps.taskinventory.model.AppDatabase


class MainActivity : AppCompatActivity(), TaskView {
    private lateinit var progressBar: ProgressBar
    private lateinit var mainContent: View
    private lateinit var cardNew: CardView
    private lateinit var cardNewName: TextView
    private lateinit var cardNewCount: TextView
    private lateinit var cardInProgress: CardView
    private lateinit var cardInProgressName: TextView
    private lateinit var cardInProgressCount: TextView
    private lateinit var cardDone: CardView
    private lateinit var cardDoneName: TextView
    private lateinit var cardDoneCount: TextView

    private lateinit var addButton: Button
    private lateinit var presenter: TaskPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setViews()
        setPresenter()
        setListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun showData(data: Map<String, Any>) {
        hideLoading()

        cardNewName.text = "New"
        cardNewCount.text = data["new_count"].toString()

        cardInProgressName.text = "In Progress"
        cardInProgressCount.text = data["in_progress_count"].toString()

        cardDoneName.text = "Done"
        cardDoneCount.text = data["done_count"].toString()
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        mainContent.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            presenter.setTasksCount()
        }
    }

    private fun setViews() {
        progressBar = findViewById(R.id.progressBar)
        mainContent = findViewById<View>(R.id.mainContent)

        // inisialisasi CardView
        cardNew = findViewById<CardView>(R.id.newStatusCard)
        cardNewName = findViewById<TextView>(R.id.newStatusName)
        cardNewCount = findViewById<TextView>(R.id.newStatusCount)

        cardInProgress = findViewById<CardView>(R.id.inProgressStatusCard)
        cardInProgressName = findViewById<TextView>(R.id.inProgressStatusName)
        cardInProgressCount = findViewById<TextView>(R.id.inProgressStatusCount)

        cardDone = findViewById<CardView>(R.id.doneStatusCard)
        cardDoneName = findViewById<TextView>(R.id.doneStatusName)
        cardDoneCount = findViewById<TextView>(R.id.doneStatusCount)

        addButton = findViewById<Button>(R.id.addButton)
    }
    private fun setListeners() {
        val cardsWithActions = listOf(
            cardNew to { moveToTaskListScreen("New") },
            cardInProgress to { moveToTaskListScreen("In Progress") },
            cardDone to { moveToTaskListScreen("Done") }
        )

        val buttonsWithActions = listOf(
            addButton to { showInputDialog() }
        )

        cardsWithActions.forEach { (card, action) ->
            card.setOnClickListener { action() }
        }

        buttonsWithActions.forEach { (button, action) ->
            button.setOnClickListener { action() }
        }
    }

    private fun setPresenter() {
        val db = AppDatabase.getInstance(this)
        val model = TaskModel(db.taskDao())
        presenter = TaskPresenter(this, model, lifecycleScope)

        lifecycleScope.launch {
            presenter.setTasksCount()
        }
    }

    private fun moveToTaskListScreen(status: String) {
        val intent = Intent(this, TaskListActivity::class.java)
        intent.putExtra("status", status)
        startActivity(intent)
    }

    private fun showInputDialog() {
        // ===== Title =====
        val titleInput = EditText(this).apply {
            hint = "Title"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        // ===== Description =====
        val descriptionInput = EditText(this).apply {
            hint = "Description"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 3
        }

        // ===== RadioGroup Category =====
        val radioGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
        }

        val normalRadio = RadioButton(this).apply {
            text = "Normal"
        }

        val urgentRadio = RadioButton(this).apply {
            text = "Urgent"
        }

        val importantRadio = RadioButton(this).apply {
            text = "Important"
        }

        radioGroup.addView(normalRadio)
        radioGroup.addView(urgentRadio)
        radioGroup.addView(importantRadio)

        // ===== Main Layout =====
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
            addView(titleInput)
            addView(descriptionInput)
            addView(radioGroup)
        }

        // ===== Dialog =====
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add New Task")
            .setView(layout)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()

        // ===== Validation & Action =====
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val title = titleInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()

            if (title.isEmpty()) {
                titleInput.error = "Title cannot be empty"
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                descriptionInput.error = "Description cannot be empty"
                return@setOnClickListener
            }

            val selectedCategory = when (radioGroup.checkedRadioButtonId) {
                normalRadio.id -> "Normal"
                urgentRadio.id -> "Urgent"
                importantRadio.id -> "Important"
                else -> ""
            }

            // TODO: kirim ke Presenter / Firebase
            presenter.onAddTaskClicked(
                title, description, selectedCategory
            )

            dialog.dismiss()
            lifecycleScope.launch {
                presenter.setTasksCount()
            }
            hideLoading()
        }
    }

    override fun showMessage(message: String) {
        Toast.makeText(
                this,
                "Message: $message",
                Toast.LENGTH_LONG
            ).show()
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
        mainContent.visibility = View.VISIBLE
    }

}