package com.moaapps.screensaver

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<SelectorFragment>(R.id.file_selecter_fragment)
                add<ContentFragment>(R.id.folder_content_fragment)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {

        try {
//            val rEvent = Tools.initKeyEvents(this, showingVideo, event, exoPlayer, this)
            val rEvent = false

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
        val frag = supportFragmentManager.findFragmentById(R.id.folder_content_fragment) as ContentFragment

        if(canResetContent) {
            frag.resetContent()
        }

        frag.updateDirTextView(true)
        frag.createFilePanels(true)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun updateSelectorFragment(canUpdateSelector: Boolean){
        val frag = supportFragmentManager.findFragmentById(R.id.file_selecter_fragment) as SelectorFragment

        if(canUpdateSelector){
            frag.updateSelectorDir()
        }

        frag.updateDirTextView(false)
        frag.createFilePanels(false)
    }





}