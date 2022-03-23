package com.example.myfiles

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class FileAdapter(private val files: ArrayList<File>, val callbackItem: FileItemEventListener) :
    RecyclerView.Adapter<FileAdapter.FileViewHolder>() {
    private var filteredFile: ArrayList<File> = files
    private var viewTypeFiles = ViewTypeFiles.ROW

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileName = itemView.findViewById<TextView>(R.id.txt_nameFile)
        private val fileIcon = itemView.findViewById<ImageView>(R.id.img_itemFiles_main)
        private val iconMenu = itemView.findViewById<ImageView>(R.id.img_itemFiles_more)
        fun onBind(file: File) {
            fileName.text = file.name
            if (file.isDirectory)
                fileIcon.setImageResource(R.drawable.ic_folder_black_32dp)
            else fileIcon.setImageResource(R.drawable.ic_file_black_32dp)
            itemView.setOnClickListener {
                callbackItem.onFileItemClick(file, viewTypeFiles)
            }
            iconMenu.setOnClickListener { v ->
                val popupMenu = PopupMenu(v.context, v)
                popupMenu.menuInflater.inflate(R.menu.menu_item_popup, popupMenu.menu)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menuItem_delete -> callbackItem.onFileItemDelete(file)
                        R.id.menuItem_copy -> callbackItem.onFileItemCopy(file)
                        R.id.menuItem_move -> callbackItem.onFileItemMove(file)
                    }
                    return@setOnMenuItemClickListener false
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            if (viewType == ViewTypeFiles.ROW.value) R.layout.item_files else R.layout.item_files_grid,
            parent, false
        )
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.onBind(filteredFile[position])
    }

    override fun getItemCount(): Int = filteredFile.size
    fun addFolder(file: File) {
        files.add(0, file)
        notifyItemInserted(0)
    }

    fun deleteFolder(file: File) {
        val index = files.indexOf(file)
        if (index > -1) {
            files.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun search(query: String) {
        if (query.isNotEmpty()) {
            val result = ArrayList<File>()
            this.files.forEach {
                if (it.name.contains(query, true)) {
                    result.add(it)
                }
            }
            this.filteredFile = result
            notifyDataSetChanged()
        } else this.filteredFile = this.files; notifyDataSetChanged()
    }

    interface FileItemEventListener {
        fun onFileItemClick(file: File, viewTypeFiles: ViewTypeFiles)
        fun onFileItemDelete(file: File)
        fun onFileItemCopy(file: File)
        fun onFileItemMove(file: File)

    }

    override fun getItemViewType(position: Int): Int {
        return this.viewTypeFiles.value
    }

    fun setViewType(viewType: ViewTypeFiles) {
        this.viewTypeFiles = viewType
        notifyDataSetChanged()
    }
}