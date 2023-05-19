package com.moaapps.screensaver.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.moaapps.screensaver.Activities.MainActivity
import com.moaapps.screensaver.R

class LoadingFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val baseView = layoutInflater.inflate(R.layout.loading_fragment, container, false)

        return baseView

    }
}