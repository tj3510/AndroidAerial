package com.moaapps.screensaver.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.moaapps.screensaver.Activities.MainActivity
import com.moaapps.screensaver.R

class SettingsFragment : Fragment() {


    //Buttons
    lateinit var showClockButton: Button
    lateinit var backButton: Button

    //Values
    var showClock = false

    //Misc
    lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val baseView = layoutInflater.inflate(R.layout.settings_fragment, container, false)


        showClockButton = baseView.findViewById(R.id.show_clock)
        backButton = baseView.findViewById(R.id.back_button)

        prefs = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        initShowClock()

        showClockButton.setOnClickListener {

            if(showClock){
                showClockButton.text = "Show Clock Off"
                showClock = false
            } else {
                showClockButton.text = "Show Clock On"
                showClock = true
            }

            with(prefs.edit()){
                putBoolean("showClock", showClock)
                apply()
            }

            Log.d("DevStuff", "Show Clock is " + showClock.toString())

        }


        backButton.setOnClickListener {
            (requireActivity() as MainActivity).showDisplayFragments()
        }



        return baseView
    }

    private fun initShowClock(){
        showClock = prefs.getBoolean("showClock", false)

        if(showClock){
            showClockButton.text = "Show Clock On"
        } else {
            showClockButton.text = "Show Clock Off"
        }
    }
}