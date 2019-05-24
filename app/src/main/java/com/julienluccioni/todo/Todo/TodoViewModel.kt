package com.julienluccioni.todo

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {

    var myTasks: MutableList<Todo> = mutableListOf()
    var isLoadingFirstTime = true
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob)
    val projectId = 2210926529

    fun init(
        initList: (lastIndex: Int) -> Unit,
        isRefresh: Boolean = false
    ) {
        if (!isLoadingFirstTime && !isRefresh) {
            return
        }
        isLoadingFirstTime = false
        coroutineScope.launch {
            try {
                val allTasks = TodoApi.TodoService.getTasks(projectId).await()
                myTasks.clear()
                myTasks.addAll(allTasks)
                initList(myTasks.size)
            } catch (e: Exception) {
                Log.i("MyDebug", e.message)
            }
        }
        Log.i("MyDebug", "InitViewModel")
    }

    fun onCreate(content: String, sucess: (Int) -> Unit, fail: (String) -> Unit) {
        coroutineScope.launch {
            try {
                val newTodo = Todo(id = 0, content = content, project_id = projectId, completed = false)
                val todo: Todo = TodoApi.TodoService.addTask(newTodo).await()
                myTasks.add(todo)
                sucess(myTasks.size - 1)
            } catch (e: Exception) {
                fail("Create Fail")
                Log.i("MyDebug", e.message)
            }
        }
    }

    fun onDelete(pos: Int, success: (Int, Int) -> Unit, fail: (String) -> Unit) {
        coroutineScope.launch {
            try {
                TodoApi.TodoService.deleteTask(myTasks[pos].id.toString()).await()
                myTasks.removeAt(pos)
                success(pos, myTasks.size)
            } catch (e: Exception) {
                fail("Delete fail")
                Log.i("MyDebug", e.message)
            }
        }
    }

    fun onDeleteAll(success: (Int) -> Unit, fail: (String) -> Unit) {
        coroutineScope.launch {
            val count = myTasks.size
            myTasks.forEach {
                try {
                    TodoApi.TodoService.deleteTask(it.id.toString()).await()
                } catch (e: Exception) {
                    fail("""Task ${it.id} was not deleted""")
                    Log.i("MyDebug", e.message)
                }
            }
            myTasks.clear()
            success(count)
        }

    }

    fun onUpdate(pos: Int, isChecked: Boolean, fail: (String) -> Unit) {
        coroutineScope.launch {
            try {
                if (isChecked) {
                    TodoApi.TodoService.closeTask(myTasks[pos].id.toString()).await()
                } else {
                    TodoApi.TodoService.reopenTask(myTasks[pos].id.toString()).await()
                }
            } catch (e: Exception) {
                fail("Update fail")
                Log.i("MyDebug", e.message)
            }

        }
    }

}