package com.example.myfiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_file.*
import kotlinx.android.synthetic.main.fragment_file.view.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class FragmentFile : Fragment(), FileAdapter.FileItemEventListener {
    private lateinit var path: String
    private var back = false
    private lateinit var fileAdapter: FileAdapter
    private lateinit var viewType: ViewTypeFiles
    private lateinit var gridLayoutManager: GridLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        path = arguments!!.getString("path", "")
        back = arguments!!.getBoolean("back", false)
        viewType =
            if (arguments!!.getBoolean("viewType", true)) ViewTypeFiles.ROW else ViewTypeFiles.GRID
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_file, container, false)
        val recyclerviewFile = view.rv_main
        val currentFile = File(path)
        val pathName = view.txt_file_path
        gridLayoutManager = GridLayoutManager(context, 1, RecyclerView.VERTICAL, false)
        recyclerviewFile.layoutManager = gridLayoutManager
        if (StorageHelper.isExternalStorageReadable()) {
            val files = currentFile.listFiles()
            fileAdapter = FileAdapter(files!!.toCollection(ArrayList()), this)
            recyclerviewFile.adapter = fileAdapter
            setViewType(viewType)
        }
        pathName.text = if (currentFile.name == "files") {
            "External Storage"
        } else currentFile.name

        if (back) {
            view.img_file_back.setOnClickListener {
//                val lastIndex = path.lastIndexOf("/")
//                val subPatch = path.substring(0, lastIndex)
//                (activity as MainActivity).listFile(subPatch, viewTypeFiles)
                (activity as MainActivity).onBackPressed()
            }
        }
        return view
    }

    override fun onFileItemClick(file: File, viewTypeFiles: ViewTypeFiles) {
        if (file.isDirectory) {
            (activity as MainActivity).listFile(file.path, viewTypeFiles)
        }
    }

    override fun onFileItemDelete(file: File) {
        if (StorageHelper.isExternalStorageWritable()) {
            if (file.delete())
                fileAdapter.deleteFolder(file)
        }
    }

    override fun onFileItemCopy(file: File) {
        if (StorageHelper.isExternalStorageWritable()) {
            try {
                if (copy(file.path, getDestinationPath(file))) {
                    Toast.makeText(context, "با موفقیت کپی شد!", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(context, "مشکلی پیش آمد !", Toast.LENGTH_SHORT).show()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    override fun onFileItemMove(file: File) {
        if (StorageHelper.isExternalStorageWritable()) {
            try {
                if (copy(file.path, getDestinationPath(file))) {
                    onFileItemDelete(file)
                    Toast.makeText(context, "با موفقیت انتقال یافت !", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(context, "مشکلی پیش آمد !", Toast.LENGTH_SHORT).show()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    fun crateNewFolder(folder: String) {
        if (StorageHelper.isExternalStorageWritable()) {
            val newFolder = File(path + File.separator + folder)
            if (!newFolder.exists()) {
                if (newFolder.mkdir()) {
                    fileAdapter.addFolder(newFolder)
                }
                rv_main.smoothScrollToPosition(0)
            } else Toast.makeText(
                context,
                "مشکلی در ایجاد پوشه ایجاد شده است!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun copy(source: String, destination: String): Boolean {
        if (source != destination) {
            val input = FileInputStream(source)
            val output = FileOutputStream(destination)
            val bytes = ByteArray(1024)
            var length: Int
            while (input.read(bytes).also { length = it } > 0) {
                output.write(bytes, 0, length)
            }

            input.close()
            output.close()
            return true
        }
        return false
    }

    private fun getDestinationPath(file: File): String =
        context?.getExternalFilesDir(null)!!.path + (File.separator) + "destination" + File.separator + file.name

    fun search(query: String) {
        fileAdapter.search(query)
    }

    fun setViewType(viewType: ViewTypeFiles) {
        fileAdapter.setViewType(viewType)
        gridLayoutManager.spanCount = if (viewType == ViewTypeFiles.ROW) 1 else 2
    }
}