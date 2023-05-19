package com.moaapps.screensaver.Services

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.service.dreams.DreamService
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.moaapps.screensaver.Fragments.DisplayFragment
import com.moaapps.screensaver.R
import java.io.File
import java.lang.Exception
import kotlin.collections.ArrayList


class ScreenSaverService : DreamService() {

    private lateinit var playerView: PlayerView
    private lateinit var exoPlayer:SimpleExoPlayer
    private val filesList = ArrayList<File>(200)

    lateinit var prefs: SharedPreferences

    lateinit var dir: String

    lateinit var clock: LinearLayout

    var showClock: Boolean = false

    override fun onDreamingStarted() {
        try {
        super.onDreamingStarted()

            Log.d("TAG", "onDreamingStarted: ")

            prefs = getSharedPreferences("user", Context.MODE_PRIVATE)

            dir = prefs.getString("dir", DisplayFragment.defaultStorageDir)!!

            val file = File(dir)

            if (file.exists() && file.isDirectory) {
                for (file in file.listFiles()) {

                    if (file != null && file.absolutePath.endsWith("mov")) {
                        filesList.add(file)
                    } else if (file != null && file.absolutePath.endsWith("mp4")) {
                        filesList.add(file)
                    }

                }
            }

            exoPlayer = SimpleExoPlayer.Builder(this).build()

            //Repeat mode set to true
            exoPlayer.repeatMode = SimpleExoPlayer.REPEAT_MODE_ALL


            playerView.player = exoPlayer

            if (filesList.isEmpty()) {
                findViewById<TextView>(R.id.no_files).visibility = View.VISIBLE
                findViewById<PlayerView>(R.id.player_view).visibility = View.GONE
            } else {
                setFilesAndPlay()

                exoPlayer.addListener(object : Player.DefaultEventListener() {
                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        super.onPlayerStateChanged(playWhenReady, playbackState)
                        if (playbackState == Player.STATE_ENDED) {
                            exoPlayer.release()
                            exoPlayer = SimpleExoPlayer.Builder(this@ScreenSaverService).build()
                            setFilesAndPlay()
                        }
                    }
                })

            }
        } catch (e: Exception){
            findViewById<TextView>(R.id.no_files).visibility = View.VISIBLE
            findViewById<PlayerView>(R.id.player_view).visibility = View.GONE
            findViewById<TextView>(R.id.no_files).text = e.toString()
        }


    }

    private fun setFilesAndPlay() {
        filesList.shuffle()
        for (file in filesList) {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(file))
            exoPlayer.addMediaItem(mediaItem)
        }

        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    override fun onDreamingStopped() {
        super.onDreamingStopped()
        Log.d("TAG", "onDreamingStopped: ")
        exoPlayer.stop()
        exoPlayer.release()
    }

    override fun onAttachedToWindow() {
        try {
        super.onAttachedToWindow()
        Log.d("TAG", "onAttachedToWindow: ")
        isInteractive = true
        isFullscreen = true

            prefs = getSharedPreferences("user", Context.MODE_PRIVATE)
            setContentView(R.layout.screen_saver_layout)
            playerView = findViewById(R.id.player_view)
            clock = findViewById(R.id.textClock)

            showClock = prefs.getBoolean("showClock", true)

            if (showClock) {
                clock.visibility = LinearLayout.VISIBLE
            } else {
                clock.visibility = LinearLayout.GONE
            }
        } catch (e: Exception){
            findViewById<TextView>(R.id.no_files).visibility = View.VISIBLE
            findViewById<PlayerView>(R.id.player_view).visibility = View.GONE
            findViewById<TextView>(R.id.no_files).text = e.toString()
        }

    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (event.action == KeyEvent.ACTION_UP) {
                exoPlayer.previous()
                return true
            }
        } else if (event.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (event.action == KeyEvent.ACTION_UP) {
                exoPlayer.next()
                return true
            }
        } else {
            finish()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

}