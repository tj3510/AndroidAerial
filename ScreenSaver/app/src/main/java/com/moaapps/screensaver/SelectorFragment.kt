package com.moaapps.screensaver

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class SelectorFragment : DisplayFragment() {

    //Buttons
    lateinit var backButton: Button
    lateinit var testButton: Button
    lateinit var settingsButton: Button


    //Tools
    lateinit var prefs: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        //Holder Views
        baseView = layoutInflater.inflate(R.layout.selector_fragment, container, false)
        fileDisplayHolder = baseView.findViewById(R.id.file_selecter_holder)
        fileSelectorCurrentDir = baseView.findViewById(R.id.current_dir_file_selector)
        contentScrollView =  baseView.findViewById(R.id.content_scroll_view_file_selector)

        //TextViews
        noFilesTextView =  baseView.findViewById(R.id.no_files_file_selector)

        //Buttons
        backButton =  baseView.findViewById(R.id.back_button)
        confirmButton =  baseView.findViewById(R.id.confirm_button)
        openButton =  baseView.findViewById(R.id.open_button)
        testButton =  baseView.findViewById(R.id.test_button)
        settingsButton =  baseView.findViewById(R.id.settings_button)

        prefs = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        val cDirString = prefs.getString("dir", defaultStorageDir)!!

        contentDir = File(cDirString)
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
            updateSelectorDir()
            (requireActivity() as MainActivity).updateSelectorFragment(true)
            (requireActivity() as MainActivity).updateContentFragment(true)
        }

        confirmButton.setOnClickListener {

            with (prefs.edit()) {
                putString("dir", contentDir.absolutePath)
                apply()
            }
            
            Toast.makeText(requireContext(), "Screensaver Dir Set To: " + contentDir.absolutePath, Toast.LENGTH_LONG).show()

            (requireActivity() as MainActivity).updateScreensaverDir()
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