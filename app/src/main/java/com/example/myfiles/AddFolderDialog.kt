package com.example.myfiles

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_add_new_folder.*
import kotlinx.android.synthetic.main.dialog_add_new_folder.view.*

class AddFolderDialog : DialogFragment() {
    private lateinit var callbackAddFolder: AddNewFolderCallback
    override fun onAttach(context: Context) {
        callbackAddFolder = activity as AddNewFolderCallback
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context)
        val view =
            LayoutInflater.from(activity).inflate(R.layout.dialog_add_new_folder, null, false)
        val folderName = view.et_addNewFolder
        val etl = view.etl_addNewFolder
        view.btn_addNewFolder_create.setOnClickListener {
            if (folderName.text?.isNotEmpty()!!) {
                callbackAddFolder.onCreateFolderButtonClick(folderName.text.toString())
                dismiss()
            } else etl.error = "Folder name cannot be empty!!"
        }
        dialog.setView(view)

        return dialog.create()
    }



    interface AddNewFolderCallback {
        fun onCreateFolderButtonClick(folder: String)
    }
}