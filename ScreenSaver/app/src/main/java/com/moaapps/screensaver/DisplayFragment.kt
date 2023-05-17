package com.moaapps.screensaver

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.format.Formatter
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
import java.io.File

abstract class DisplayFragment: Fragment() {


    companion object{
        lateinit var selectorDir: File
        lateinit var contentDir: File
        var selectorDirList: Array<File>? = null
        var contentDirList: Array<File>? = null
        val defaultStorageDir = "storage/emulated/0/"
    }

    //Holders
    lateinit var baseView: View
    lateinit var fileDisplayHolder: LinearLayout
    lateinit var contentScrollView: ScrollView

    //Text Views
    lateinit var fileSelectorCurrentDir: TextView
    lateinit var folderContentCurrentDir: TextView
    lateinit var noFilesTextView: TextView

    //Buttons
    lateinit var confirmButton: Button
    lateinit var openButton: Button



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
                val fileSize = nView.findViewById<TextView>(R.id.file_size)

                setFileIcon(file, fileIcon)
                fileName.text = file.name
                fileSize.text = Formatter.formatFileSize(context, file.length())
                nView.isClickable = true
                nView.isFocusable = true

                fileDisplayHolder.addView(nView)

                if(!isContentDir) {

                    nView.setOnFocusChangeListener { view, b ->
                        if(b) {
                            contentDir = file
                            (requireActivity() as MainActivity).updateContentFragment(false)
                            view.setBackgroundResource(R.drawable.file_name_background_focused)
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
                drawable = requireActivity().getDrawable(R.drawable.video_icon)
            } file.isDirectory-> {
                drawable = requireActivity().getDrawable(R.drawable.folder_icon)
            } else -> {
                drawable = requireActivity().getDrawable(R.drawable.file_icon)
            }
        }

        fileIcon.setImageDrawable(drawable)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun backButtonFunctionality(){

        val parentFile = selectorDir.parentFile
        val parentListFiles = parentFile?.listFiles()


        if(parentFile != null && parentListFiles != null && parentListFiles.isNotEmpty()){
            contentDir = parentFile

            (requireActivity() as MainActivity).updateSelectorFragment(true)
            (requireActivity() as MainActivity).updateContentFragment(true)

            Log.d("DevStuff", parentFile.absolutePath)
        } else {
            Toast.makeText(requireContext(), "This is the root directory!", Toast.LENGTH_SHORT).show()
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
            fileSelectorCurrentDir.text = selectorDir.absolutePath
        }

    }

    fun updateDirList(isContent: Boolean) {

        if(isContent) {
            contentDirList = contentDir.listFiles()

            if (contentDirList == null || contentDirList!!.size == 0) {
                Log.d("DevStuff", "sDir is empty!")
                contentDirList = arrayOf()
                hideScrollView()
            } else {
                Log.d("DevStuff", "Dir List: " + contentDirList.contentToString())
                showScrollView()
            }
        } else {
            selectorDirList = selectorDir.listFiles()

            if (selectorDirList == null || selectorDirList!!.size == 0) {
                Log.d("DevStuff", "cDir is empty!")
                selectorDirList = arrayOf()
                hideScrollView()
            } else {
                Log.d("DevStuff", "Dir List: " + contentDirList.contentToString())
                showScrollView()
            }
        }

    }

    fun hideScrollView(){
        noFilesTextView.visibility = LinearLayout.VISIBLE
        contentScrollView.visibility = LinearLayout.GONE
    }

    fun showScrollView(){
        noFilesTextView.visibility = LinearLayout.GONE
        contentScrollView.visibility = LinearLayout.VISIBLE
    }

}