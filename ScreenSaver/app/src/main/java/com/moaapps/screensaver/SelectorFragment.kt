package com.moaapps.screensaver

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
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


    private lateinit var playerView: PlayerView
    private lateinit var exoPlayer: SimpleExoPlayer
    private val filesList = ArrayList<File>(200)


    //Buttons
    lateinit var backButton: Button


    lateinit var clock: LinearLayout

    lateinit var dirString: String

    var showClock = true

    var showingVideo = false

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

        prefs = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        val cDirString = prefs.getString("dir", "storage/emulated/0/")!!

        fileDirStack.addLast(File(cDirString))

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
            fileDirStack.addLast(contentDir)
            (requireActivity() as MainActivity).updateSelectorFragment(true)
            (requireActivity() as MainActivity).updateContentFragment(true)
        }

        confirmButton.setOnClickListener {

        }

        return baseView
        
    }






    private fun loadPrefs(){
        dirString = prefs.getString("dir", "storage")!!
        showClock = prefs.getBoolean("showClock", true)
    }

    private fun testVideo(){
        initExoPlayer()

        Log.d("TAG", "onDreamingStarted: ")

        prefs = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        dirString = prefs.getString("dir", "storage")!!

        val dir = File(dirString)

        val testDirFileList = dir.listFiles()



        if (dir.exists() && dir.isDirectory && testDirFileList != null){
            for (foundFile in testDirFileList){

                if (foundFile != null && foundFile.absolutePath.endsWith("mov")){
                    filesList.add(foundFile)
                }

                else if (foundFile != null && foundFile.absolutePath.endsWith("mp4")){
                    filesList.add(foundFile)
                }

            }
        }

        exoPlayer = SimpleExoPlayer.Builder(requireContext()).build()

        //Repeat mode set to true
        exoPlayer.repeatMode = SimpleExoPlayer.REPEAT_MODE_ALL


        playerView.player = exoPlayer

        if (filesList.isEmpty()){
            baseView.findViewById<TextView>(R.id.no_files).visibility = View.VISIBLE
            baseView.findViewById<PlayerView>(R.id.player_view).visibility = View.GONE
        }else{
            Tools.setFilesAndPlay(exoPlayer, filesList)

            exoPlayer.addListener(object : Player.DefaultEventListener() {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)
                    if (playbackState == Player.STATE_ENDED){
                        exoPlayer.release()
                        exoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
                        Tools.setFilesAndPlay(exoPlayer, filesList)
                    }
                }
            })

        }
    }



    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            val result1 =
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }
    
    
    private fun initExoPlayer(){

        showingVideo = true

//        setContentView(R.layout.screen_saver_layout)
        playerView = baseView.findViewById(R.id.player_view)
        clock = baseView.findViewById(R.id.textClock)

        showClock = prefs.getBoolean("showClock", true)

        if(showClock){
            clock.visibility = LinearLayout.VISIBLE
        } else {
            clock.visibility = LinearLayout.GONE
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.stop()
        exoPlayer.release()
    }

    override fun onStop() {
        super.onStop()
        exoPlayer.stop()
        exoPlayer.release()
    }

}