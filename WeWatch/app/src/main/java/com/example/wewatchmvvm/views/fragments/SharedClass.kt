package com.example.wewatchmvvm.views.fragments

class SharedClass {
    interface OnDataChangedListener {
        fun onDataChangedListener()
    }

    var hasListener = HashMap<String, OnDataChangedListener>()

    fun setOnDataChangedListener(key: String, dataChanged: OnDataChangedListener) {
        this.hasListener[key] = dataChanged
    }

}