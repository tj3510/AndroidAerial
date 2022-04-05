package com.moaapps.screensaver

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView

import android.content.ActivityNotFoundException
import android.os.Build
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import java.io.File
import androidx.core.app.ActivityCompat.startActivityForResult
import pub.devrel.easypermissions.EasyPermissions
import androidx.core.content.ContextCompat





class MainActivity : Activity() {

    private val TAG: String = MainActivity::class.java.getName()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if(EasyPermissions.hasPermissions(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)){
            val file = File(Environment.getExternalStorageDirectory(),"Screensaver")
            Log.d("TAG", "onCreate: ${file.absolutePath}")
            findViewById<TextView>(R.id.directory).text = "Selected Folder: ${file.absolutePath}"
            if (file.exists() && file.isDirectory){
                val filesList = ArrayList<File>()
                for (file in file.listFiles()){
                    if (file != null && file.absolutePath.endsWith("mov")){
                        filesList.add(file)
                    }
                }
                findViewById<TextView>(R.id.video_count).text = "${filesList.size} files found in the directory"
            }else{
                findViewById<TextView>(R.id.directory).text = "Folder not found"
            }
        }else{
            EasyPermissions.requestPermissions(this, "We need to access storage", 123, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 9999 && resultCode == RESULT_OK){
            val folderLocation = data?.extras!!.getString("data")
            Log.d("TAG", "onActivityResult: $folderLocation")
            findViewById<TextView>(R.id.directory).text = folderLocation
        }
    }


}