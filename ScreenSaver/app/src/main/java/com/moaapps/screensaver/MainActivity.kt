package com.moaapps.screensaver

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File


class MainActivity : AppCompatActivity(R.layout.activity_main) {


    //Holders
    lateinit var selectorFragmentContainer: FrameLayout
    lateinit var contentFragmentContainer: FrameLayout
    lateinit var settingsFragmentContainer: FrameLayout
    lateinit var clock: LinearLayout

    //Video Stuff
    private lateinit var playerView: PlayerView
    private lateinit var exoPlayer: SimpleExoPlayer


    //Values
    var showClock = true
    var showingVideo = false

    //Tools
    lateinit var prefs: SharedPreferences


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE)

        selectorFragmentContainer = findViewById(R.id.selector_fragment_container)
        contentFragmentContainer = findViewById(R.id.content_fragment_container)
        settingsFragmentContainer = findViewById(R.id.settings_fragment_container)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<SelectorFragment>(R.id.selector_fragment_container)
                add<ContentFragment>(R.id.content_fragment_container)
                add<SettingsFragment>(R.id.settings_fragment_container)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {

        try {
            val rEvent = Tools.initKeyEvents(this, showingVideo, event, exoPlayer, this)

            if(rEvent){
                return rEvent
            } else {
                return super.dispatchKeyEvent(event)
            }
        }catch (e: java.lang.Exception){
            return super.dispatchKeyEvent(event)
        }

    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun updateContentFragment(canResetContent: Boolean){
        val frag = supportFragmentManager.findFragmentById(R.id.content_fragment_container) as ContentFragment

        if(canResetContent) {
            frag.resetContent()
        }

        frag.updateDirTextView(true)
        frag.createFilePanels(true)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun updateSelectorFragment(canUpdateSelector: Boolean){
        val frag = supportFragmentManager.findFragmentById(R.id.selector_fragment_container) as SelectorFragment

        if(canUpdateSelector){
            frag.updateSelectorDir()
        }

        frag.updateDirTextView(false)
        frag.createFilePanels(false)
    }

    fun prepareTestVideo(){
        setContentView(R.layout.screen_saver_layout)
    }


    fun showSettingsFragment(){
        settingsFragmentContainer.visibility = LinearLayout.VISIBLE
        contentFragmentContainer.visibility = LinearLayout.GONE
        selectorFragmentContainer.visibility = LinearLayout.GONE
    }

    fun showDisplayFragments(){
        settingsFragmentContainer.visibility = LinearLayout.GONE
        contentFragmentContainer.visibility = LinearLayout.VISIBLE
        selectorFragmentContainer.visibility = LinearLayout.VISIBLE
    }

    fun updateScreensaverDir() {
        val frag = supportFragmentManager.findFragmentById(R.id.content_fragment_container) as ContentFragment
        frag.updateScreensaverDir()
    }




    fun testVideo(){
        initExoPlayer()

        val filesList = ArrayList<File>()

        Log.d("DevStuff", "Dream Started")

        val dirString = prefs.getString("dir", DisplayFragment.defaultStorageDir)!!

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

        Log.d("DevStuff", "File List For Screensaver: " + filesList.toString())

        exoPlayer = SimpleExoPlayer.Builder(this).build()

        //Repeat mode set to true
        exoPlayer.repeatMode = SimpleExoPlayer.REPEAT_MODE_ALL

        clock = findViewById(R.id.textClock)

        showClock = prefs.getBoolean("showClock", true)

        if (showClock) {
            clock.visibility = LinearLayout.VISIBLE
        } else {
            clock.visibility = LinearLayout.GONE
        }


        playerView.player = exoPlayer

        if (filesList.isEmpty()){
            findViewById<TextView>(R.id.no_files).visibility = View.VISIBLE
            findViewById<PlayerView>(R.id.player_view).visibility = View.GONE
        }else{
            Tools.setFilesAndPlay(exoPlayer, filesList)

            exoPlayer.addListener(object : Player.DefaultEventListener() {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    super.onPlayerStateChanged(playWhenReady, playbackState)
                    if (playbackState == Player.STATE_ENDED){
                        exoPlayer.release()
                        exoPlayer = SimpleExoPlayer.Builder(this@MainActivity).build()
                        Tools.setFilesAndPlay(exoPlayer, filesList)
                    }
                }
            })

        }
    }

    private fun initExoPlayer(){

        prepareTestVideo()
        showingVideo = true
        playerView = findViewById(R.id.player_view)
        clock = findViewById(R.id.textClock)

        showClock = prefs.getBoolean("showClock", true)

        if(showClock){
            clock.visibility = LinearLayout.VISIBLE
        } else {
            clock.visibility = LinearLayout.GONE
        }

        Log.d("DevStuff", "Exo Player Initialized")

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