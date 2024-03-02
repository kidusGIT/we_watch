package com.example.wewatchmvvm

import android.net.Uri
import androidx.fragment.app.Fragment

interface MainActivityFunc {

    fun addFragment(fragment: Fragment, tag: String)

    fun requestAPermission(permission: String, callback: () -> Unit)

    fun selectImage(callback: (uri: Uri) -> Unit)

}