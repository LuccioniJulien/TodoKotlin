package com.julienluccioni.todo

import android.os.Bundle
import android.text.TextUtils
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.view.*
import org.jetbrains.anko.alert
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.cancelButton
import org.jetbrains.anko.okButton

class MainActivity : AppCompatActivity() {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var recycle: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel::class.java)

        recycle = layout_content.recycle_todo

        fab.setOnClickListener { showAddItemDialog() }

        recycle.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recycle.adapter = CustomAdapter(
            todoViewModel.myTasks,
            update = { pos: Int, isCheked: Boolean -> update(pos, isCheked) },
            delete = { delete(it) })
        todoViewModel.init({
            runOnUiThread {
                recycle.adapter?.notifyItemRangeInserted(0, it)
            }
        })
    }

    private fun create(task: String?) {
        if (task == "" || task == null) return
        todoViewModel.onCreate(task) {
            runOnUiThread {
                recycle.adapter?.notifyItemInserted(it)
                Snackbar.make(
                    layout_content,
                    "Task created",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun update(pos: Int, isChecked: Boolean) {
        todoViewModel.onUpdate(pos, isChecked)
    }

    private fun delete(pos: Int) {
        todoViewModel.onDelete(pos) { position: Int, size: Int ->
            runOnUiThread {
                recycle.adapter?.notifyItemRemoved(position)
                Snackbar.make(
                    layout_content,
                    "Task removed",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showAddItemDialog() {
        val editText = EditText(this)
        alert("What do you want to do next?", "Add a new task") {
            customView = editText
            okButton { create(editText.text.toString()) }
            cancelButton { }
            onCancelled { }
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                true
            }
            R.id.action_delete_all -> {
                todoViewModel.onDeleteAll {
                    runOnUiThread {
                        recycle.adapter?.notifyItemRangeRemoved(0, it)
                        Snackbar.make(
                            layout_content,
                            "Task removed",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
                true
            }
            R.id.action_refresh -> {
                todoViewModel.init({
                    runOnUiThread {
                        recycle.adapter?.notifyDataSetChanged()
                    }
                }, true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
