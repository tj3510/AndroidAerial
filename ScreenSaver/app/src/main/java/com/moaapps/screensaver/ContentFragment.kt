package com.moaapps.screensaver

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi

class ContentFragment : DisplayFragment() {

    //Tools
    lateinit var prefs: SharedPreferences

    //TextViews
    lateinit var screensaverDirTextView: TextView


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //Tools
        prefs = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        //Holders
        baseView = layoutInflater.inflate(R.layout.content_fragment, container, false)
        fileDisplayHolder = baseView.findViewById(R.id.folder_content_holder)
        folderContentCurrentDir = baseView.findViewById(R.id.current_dir_folder_content)
        contentScrollView =  baseView.findViewById(R.id.content_scroll_view_folder_content)


        //TextViews
        noFilesTextView =  baseView.findViewById(R.id.no_files_folder_content)
        screensaverDirTextView =  baseView.findViewById(R.id.screensaver_dir)


        resetContent()
        createFilePanels(true)
        updateDirTextView(true)
        updateScreensaverDir()

        return baseView
        
    }

    fun updateScreensaverDir(){
        val text = prefs.getString("dir", defaultStorageDir)
        screensaverDirTextView.text = "Screensaver Dir: " + text
    }

}