package com.example.myfiles

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AddFolderDialog.AddNewFolderCallback {
    private lateinit var viewTypeFiles: ViewTypeFiles
    private var back = false
    private lateinit var path: String
    private lateinit var pathBack: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (StorageHelper.isExternalStorageReadable()) {
            val externalFilesDir = getExternalFilesDir(null)
            listFile(externalFilesDir!!.path, false, ViewTypeFiles.ROW)
        }
        img_addNewFolder.setOnClickListener {
            AddFolderDialog().show(supportFragmentManager, null)
        }
        et_main_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) search_files_cancel.visibility = View.VISIBLE
                else search_files_cancel.visibility = View.GONE
                val fragment =
                    supportFragmentManager.findFragmentById(R.id.frame_main_fragmentContainer)
                if (fragment is FragmentFile) {
                    fragment.search(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        search_files_cancel.setOnClickListener {
            et_main_search.setText("")
        }
        toggleGroup_main.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (checkedId == R.id.btn_main_list && isChecked) {
                val fragment =
                    supportFragmentManager.findFragmentById(R.id.frame_main_fragmentContainer)
                if (fragment is FragmentFile) {
                    fragment.setViewType(ViewTypeFiles.ROW)
                }
            } else if (checkedId == R.id.btn_main_grid && isChecked) {
                val fragment =
                    supportFragmentManager.findFragmentById(R.id.frame_main_fragmentContainer)
                if (fragment is FragmentFile) {
                    fragment.setViewType(ViewTypeFiles.GRID)
                }
            }

        }
    }

    private fun listFile(path: String, addToBackStack: Boolean, viewTypeFiles: ViewTypeFiles) {
        this.path = path
        subPath()
        this.back = false
        this.viewTypeFiles = viewTypeFiles
        val fragmentFile = FragmentFile()
        val bundle = Bundle()
        bundle.putString("path", path)
        bundle.putBoolean("back", addToBackStack)
        bundle.putBoolean("viewType", viewTypeFiles.value == ViewTypeFiles.ROW.value)
        fragmentFile.arguments = bundle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main_fragmentContainer, fragmentFile)
//        if (addToBackStack)
//            transaction.addToBackStack(null)
        transaction.commit()
    }

    fun listFile(path: String, viewTypeFiles: ViewTypeFiles) {
        this.path = path
        subPath()
        this.back = true
        this.viewTypeFiles = viewTypeFiles
        this.listFile(path, true, viewTypeFiles)
    }

    override fun onCreateFolderButtonClick(folder: String) {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame_main_fragmentContainer)
        if (fragment is FragmentFile) {
            fragment.crateNewFolder(folder)
        }
    }

    override fun onBackPressed() {
        if (!pathBack.endsWith("myfiles")) {
            listFile(pathBack, viewTypeFiles)
        } else
            super.onBackPressed()
    }
    private fun subPath(){
        pathBack = path.substring(0, path.lastIndexOf("/"))
    }
}