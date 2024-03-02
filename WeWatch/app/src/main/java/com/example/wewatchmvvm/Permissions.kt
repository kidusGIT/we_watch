package com.example.wewatchmvvm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class Permissions(private val context: Context) {
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var imageLauncher: ActivityResultLauncher<Intent>? = null
    private var callBack: (() -> Unit?)? = null
    private var imageCallBack: ((uri: Uri) -> Unit?)? = null

    fun initializeLauncher() {
        permissionLauncher = (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it) {
                if (callBack != null) {
                    callBack?.let { it1 -> it1() }
                }
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun initializeImageLauncher() {
        imageLauncher = (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = it.data?.data
                if (selectedImageUri != null) {
                    imageCallBack?.let { it1 -> it1(selectedImageUri) }
                }
            }
        }
    }

    fun launchImageLauncher(intent: Intent, callBack: (imageUri: Uri) -> Unit) {
        this.imageCallBack = callBack
        this.imageLauncher?.launch(intent)
    }

//    request permission
    fun requestPermission(permission: String, callBack: () -> Unit) {
        this.callBack = callBack
        if (!checkPermission(permission)) {
            permissionLauncher?.launch(permission)
        }
    }

//    check if permission is granted
    fun checkPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

}