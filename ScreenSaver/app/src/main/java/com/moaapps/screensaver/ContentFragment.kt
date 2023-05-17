package com.moaapps.screensaver

import android.Manifest
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
import java.io.File

class ContentFragment : DisplayFragment() {


    private lateinit var playerView: PlayerView
    private lateinit var exoPlayer: SimpleExoPlayer
    private val filesList = ArrayList<File>(200)




    lateinit var dirEditText: EditText

    lateinit var directoryText: TextView

    lateinit var clock: LinearLayout

    lateinit var showClockCheckBox: Button

    lateinit var dirSubmitButton: Button
    lateinit var testVideoButton: Button

    lateinit var dirString: String

    var showClock = true

    var showingVideo = false

    lateinit var prefs: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        baseView = layoutInflater.inflate(R.layout.content_fragment, container, false)
        fileDisplayHolder = baseView.findViewById(R.id.folder_content_holder)
        noFilesTextView =  baseView.findViewById(R.id.no_files_folder_content)
        folderContentCurrentDir = baseView.findViewById(R.id.current_dir_folder_content)
        contentScrollView =  baseView.findViewById(R.id.content_scroll_view_folder_content)


        resetContent()
        createFilePanels(true)
        updateDirTextView(true)

        return baseView
        
    }




    private fun loadPrefs(){
        dirString = prefs.getString("dir", "storage")!!
        showClock = prefs.getBoolean("showClock", true)
    }

    private fun testVideo(){
        initExoPlayer()

        Log.d("TAG", "onDreamingStarted: ")



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