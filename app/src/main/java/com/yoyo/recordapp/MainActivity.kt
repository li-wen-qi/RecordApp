package com.yoyo.recordapp

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import com.yoyo.recordapp.utils.SystemOperatorGlobalUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SystemOperatorGlobalUtils.setStatusBarColor(this, R.color.white)
        SystemOperatorGlobalUtils.setStatusBarDarkMode(true, this)
    }

}