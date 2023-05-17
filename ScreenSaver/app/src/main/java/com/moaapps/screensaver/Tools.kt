package com.moaapps.screensaver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import androidx.annotation.RequiresApi
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import java.io.File

class Tools {

    companion object{


        @RequiresApi(Build.VERSION_CODES.R)
        fun initKeyEvents(context: Context, showingVideo: Boolean, event: KeyEvent, exoPlayer: ExoPlayer, activity: Activity): Boolean{
            if( showingVideo ) {
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
                } else if (event.keyCode == KeyEvent.KEYCODE_BACK || event.keyCode == KeyEvent.KEYCODE_DPAD_DOWN || event.keyCode == KeyEvent.KEYCODE_DPAD_UP || event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER || event.keyCode == KeyEvent.KEYCODE_ESCAPE) {
                    if (event.action == KeyEvent.ACTION_UP) {
                        stopPlayer(context, exoPlayer)
                        return true
                    }
                }
            }

            return false
        }


        @RequiresApi(Build.VERSION_CODES.R)
        fun stopPlayer(context: Context, exoPlayer: ExoPlayer){
            exoPlayer.stop()
            exoPlayer.release()
            context.startActivity(Intent(context, MainActivity::class.java))
        }


        fun setFilesAndPlay(exoPlayer: ExoPlayer, filesList: ArrayList<File>) {
            filesList.shuffle()
            for (file in filesList) {
                val mediaItem = MediaItem.fromUri(Uri.fromFile(file))
                exoPlayer.addMediaItem(mediaItem)
            }

            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }










    }

}