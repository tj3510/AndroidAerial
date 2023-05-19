package com.moaapps.screensaver.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.flexbox.FlexboxLayout
import com.moaapps.screensaver.Activities.MainActivity
import com.moaapps.screensaver.R
import java.io.File

abstract class DisplayFragment: Fragment() {


    companion object{
        lateinit var selectorDir: File
        lateinit var contentDir: File
        var selectorDirList: MutableList<File>? = null
        var contentDirList: MutableList<File>? = null
        val defaultStorageDir = "/"
        var homeDir =  mutableListOf<File>()
        val dirLinkedList = ArrayDeque<File> ()
        var homeDirInited = false
    }

    //Holders
    lateinit var baseView: View
    lateinit var fileDisplayHolder: FlexboxLayout
    lateinit var contentScrollView: ScrollView

    //Text Views
    lateinit var fileSelectorCurrentDir: TextView
    lateinit var folderContentCurrentDir: TextView
    lateinit var noFilesTextView: TextView
    lateinit var noPermissionTextView: TextView

    //Buttons
    lateinit var confirmButton: Button
    lateinit var openButton: Button

    //Tools
    lateinit var prefs: SharedPreferences



    @RequiresApi(Build.VERSION_CODES.R)
    fun createFilePanels(isContentDir: Boolean){

        updateDirList(isContentDir)
        val dirList = if(isContentDir) contentDirList else selectorDirList
        fileDisplayHolder.removeAllViews()



        if(dirList!!.isNotEmpty()){

            dirList.forEach { file ->
                val nView = layoutInflater.inflate(R.layout.file_name_panel, fileDisplayHolder, false)

                val fileIcon = nView.findViewById<ImageView>(R.id.file_icon)
                val fileName = nView.findViewById<TextView>(R.id.file_name)

                setFileIcon(file, fileIcon)
                fileName.text = file.name
                nView.isClickable = true
                nView.isFocusable = true

                fileDisplayHolder.addView(nView)

                if(!isContentDir) {

                    nView.setOnFocusChangeListener { view, b ->
                        if(b) {
                            contentDir = file
                            view.setBackgroundResource(R.drawable.file_name_background_focused)
                            updateDirTextView(false)
                        } else {
                            view.setBackgroundResource(R.drawable.file_name_background_unfocused)
                        }
                    }
                    nView.setOnClickListener {
                        openButton.requestFocus()
                    }
                }
            }
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    fun setFileIcon(file: File, fileIcon: ImageView){
        val drawable: Drawable?
        val extension = file.extension

        val videoExtensions = arrayOf("mov", "mp4")

        when{
            videoExtensions.contains(extension) -> {
                drawable = requireActivity().getDrawable(R.drawable.video_icon_2)
            } file.isDirectory-> {
                drawable = requireActivity().getDrawable(R.drawable.folder_icon_e)
            } else -> {
                drawable = requireActivity().getDrawable(R.drawable.file_icon_e)
            }
        }

        fileIcon.setImageDrawable(drawable)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun backButtonFunctionality(){


        if(!dirLinkedList.last().absolutePath.equals("/")){
            dirLinkedList.removeLast()
            contentDir = dirLinkedList.last()

            (requireActivity() as MainActivity).updateSelectorFragment(true)

            Log.d("DevStuff", contentDir.absolutePath + " Is now the last dir!")
        } else {
            Toast.makeText(requireContext(), "This is the home directory!", Toast.LENGTH_SHORT).show()
        }



    }


    fun resetContent(){

        if(selectorDirList!!.size != 0){
            contentDir = selectorDirList!!.get(0)
        }

    }

    fun updateSelectorDir(){
        selectorDir =  File(contentDir.absolutePath)
        Log.d("DevStuff", "Dir Stack: " + selectorDir.absolutePath)
    }

    @SuppressLint("SetTextI18n")
    fun updateDirTextView(isFolderContent: Boolean){

        if(isFolderContent){
            folderContentCurrentDir.text = contentDir.name + "'s Folder"
        } else {
            fileSelectorCurrentDir.text = contentDir.absolutePath
        }

    }

    fun updateDirList(isContent: Boolean) {

        if(isContent) {
            contentDirList = contentDir.listFiles()?.toMutableList()

            if (contentDirList == null || contentDirList!!.size == 0) {
                Log.d("DevStuff", "sDir is empty!")
                contentDirList =  mutableListOf()
                showNoFiles()
            } else {
                Log.d("DevStuff", "Dir List: " + contentDirList.toString())
                showScrollView()
            }
        } else {
            if(selectorDir.absolutePath.equals("/")) {
                selectorDirList = homeDir
                Log.d("DevStuff", "Selector Dir Set To Home!")
            } else {
                selectorDirList = selectorDir.listFiles()?.toMutableList()
                Log.d("DevStuff", "Selector Dir Set Not To Home! Name IS: " + selectorDir.name)
            }

            if (selectorDirList == null) {
                Log.d("DevStuff", "No Permission For cDir!")
                selectorDirList = mutableListOf()
                showNoPermission()
            } else if(selectorDirList!!.size == 0){
                Log.d("DevStuff", "cDir is empty!")
                selectorDirList = mutableListOf()
                showNoFiles()
            }  else {
                Log.d("DevStuff", "Dir List: " + contentDirList.toString())
                showScrollView()
            }
        }

    }

    fun showNoFiles(){
        noFilesTextView.visibility = LinearLayout.VISIBLE
        contentScrollView.visibility = LinearLayout.GONE
        noPermissionTextView.visibility = LinearLayout.GONE
    }

    fun showNoPermission(){
        noFilesTextView.visibility = LinearLayout.GONE
        contentScrollView.visibility = LinearLayout.GONE
        noPermissionTextView.visibility = LinearLayout.VISIBLE
    }

    fun showScrollView(){
        noFilesTextView.visibility = LinearLayout.GONE
        contentScrollView.visibility = LinearLayout.VISIBLE
        noPermissionTextView.visibility = LinearLayout.GONE
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun initHomeDir(){
        prefs = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)


        val pStrings = arrayOf("/", "/storage", "/storage/emulated")

        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        requireContext().contentResolver.query(
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), projection, null, null, null, null)?.use { cursor ->      //cache column indices
            val absPath = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)

            //iterating over all of the found images
            while (cursor.moveToNext()) {

                val filePath = cursor.getString(absPath)
                val nFile = File(filePath)
                val parentString = nFile.parent!!

                if(nFile.isDirectory && pStrings.contains(parentString)) {
                    homeDir.add(nFile)
                }

            }
        }
    }

}