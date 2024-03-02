package com.example.wewatchmvvm.viewModel

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.example.wewatchmvvm.R
import java.util.Stack

class MainActivityViewModel(
   activity: AppCompatActivity,
   private val mainFragments: HashMap<String, Fragment>
    ): ViewModel() {

    private val TAG = "MainActivityViewModel"

    private val frameLayout = R.id.main_frame_layer
    var fragmentManager: FragmentManager = (activity).supportFragmentManager
    var hasRotated = false

    private var currentFragmentTag: String? = null

    private val stackFragment = HashMap<String, Stack<Fragment>>()

    fun navigate(
        newFragment: Fragment,
        tag: String,
        rootTag: String? = null,
        isBackStacked: Boolean= false
    ) {

        if (currentFragmentTag == tag) return

        if (isBackStacked && rootTag != null) {
            var list = Stack<Fragment>()
            list= if (stackFragment.containsKey(rootTag)) stackFragment[rootTag]!! else list
            list.push(newFragment)
            stackFragment[rootTag] = list

            displayTopFragmentStack()
            return
        }

        val count = fragmentManager.backStackEntryCount
        if (count > 0) clearAllBackStack()

        val trans = fragmentManager.beginTransaction()
        mainFragments.forEach { (key, value) ->
            if (tag == key) {
                if (!value.isAdded) {
                    trans.add(frameLayout, value, key)
                }
                trans.show(value)
                currentFragmentTag = key
            } else {
                trans.hide(value)
            }
        }
        trans.commit()
        if (stackFragment.containsKey(tag)) displayTopFragmentStack()
    }

    private fun displayTopFragmentStack() {
        val stack = stackFragment[currentFragmentTag]
        if (stack != null) {
            val stack = stackFragment[currentFragmentTag]
            val trans = fragmentManager.beginTransaction()
            if (stack?.empty() == true) return

            stack?.peek()?.let { trans.add(frameLayout, it) }
            trans.addToBackStack(null)
            trans.commit()
        }
    }

    fun popBackFragment(): Boolean {
        if (currentFragmentTag != null) {
            if (!stackFragment.containsKey(currentFragmentTag)) return true
            val stack = stackFragment[currentFragmentTag]
            if (stack?.empty() == true) return true

            fragmentManager.popBackStack()
            stack?.pop()
            displayTopFragmentStack()
        } else return true

        return false
    }

    private fun clearAllBackStack() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}