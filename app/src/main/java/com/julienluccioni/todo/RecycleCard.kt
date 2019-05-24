package com.julienluccioni.todo

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(
    var taskList: MutableList<Todo>,
    val update: (pos: Int, isChecked: Boolean) -> Unit,
    val delete: (pos: Int) -> Unit
) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, index: Int) = holder.bind(taskList[index], update, delete)

    override fun getItemCount(): Int = taskList.size

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.material_card, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var chkTask: CheckBox = itemView.findViewById(R.id.checkBox_task)
        private var btnDelete: ImageView = itemView.findViewById(R.id.imageView_delete)

        fun bind(task: Todo, update: (pos: Int, isChecked: Boolean) -> Unit, delete: (pos: Int) -> Unit) {
            btnDelete.setOnClickListener { delete(adapterPosition) }
            chkTask.apply {
                text = task.content
                setOnClickListener {
                    paintFlags = if (isChecked) Paint.STRIKE_THRU_TEXT_FLAG else 0
                    update(adapterPosition, isChecked)
                }
            }
        }
    }

}