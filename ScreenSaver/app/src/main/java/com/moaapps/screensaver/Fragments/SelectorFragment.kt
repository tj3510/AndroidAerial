package com.moaapps.screensaver.Fragments

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import com.moaapps.screensaver.Activities.MainActivity
import com.moaapps.screensaver.R
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class SelectorFragment : DisplayFragment() {

    //Buttons
    lateinit var backButton: Button
    lateinit var testButton: Button
    lateinit var settingsButton: Button



    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //Holder Views
        baseView = layoutInflater.inflate(R.layout.selector_fragment, container, false)
        fileDisplayHolder = baseView.findViewById(R.id.file_selecter_holder)
        fileSelectorCurrentDir = baseView.findViewById(R.id.current_dir_file_selector)
        contentScrollView =  baseView.findViewById(R.id.content_scroll_view_file_selector)

        //TextViews
        noFilesTextView =  baseView.findViewById(R.id.no_files_file_selector)
        noPermissionTextView =  baseView.findViewById(R.id.no_permision)

        //Buttons
        backButton =  baseView.findViewById(R.id.back_button)
        confirmButton =  baseView.findViewById(R.id.confirm_button)
        openButton =  baseView.findViewById(R.id.open_button)
        testButton =  baseView.findViewById(R.id.test_button)
        settingsButton =  baseView.findViewById(R.id.settings_button)

        if(!homeDirInited) {
            initHomeDir()
            homeDirInited = true
        }

        contentDir = File("/")
        dirLinkedList.addLast(contentDir)
        updateSelectorDir()
        createFilePanels(false)
        updateDirTextView(false)


        if(Environment.isExternalStorageManager()){
            Log.d("DevStuff", "Has permission")
        }else{
            Log.d("DevStuff", "Does not have permission")
            EasyPermissions.requestPermissions(requireActivity(), "We need to access storage", 123, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }

        backButton.setOnClickListener {
            backButtonFunctionality()
        }

        openButton.setOnClickListener {
            dirLinkedList.addLast(contentDir)
            updateSelectorDir()
            (requireActivity() as MainActivity).updateSelectorFragment(true)
        }

        confirmButton.setOnClickListener {

            with (prefs.edit()) {
                putString("dir", contentDir.absolutePath)
                apply()
            }
            
            Toast.makeText(requireContext(), "Screensaver Dir Set To: " + contentDir.absolutePath, Toast.LENGTH_LONG).show()
        }
        
        testButton.setOnClickListener {
            (requireActivity() as MainActivity).testVideo()
        }

        settingsButton.setOnClickListener {

            (requireActivity() as MainActivity).showSettingsFragment()
        }

        return baseView
        
    }
}