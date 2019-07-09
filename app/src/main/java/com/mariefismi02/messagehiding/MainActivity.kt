package com.mariefismi02.messagehiding

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.mariefismi02.messagehiding.main.MainFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        if(supportActionBar!=null)
            supportActionBar!!.title = "Encrypt Data"

        if(savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_main, MainFragment())
                .commit()
        }

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onPause() {
        super.onPause()
        drawer_layout.removeDrawerListener(toggle)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> {
                supportActionBar!!.title = "Encrypt Data"
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, MainFragment())
                    .commit()
            }
            R.id.nav_log -> {
                supportActionBar!!.title = "Log Data"
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, LogFragment())
                    .commit()
            }
            R.id.nav_about -> {
                supportActionBar!!.title = "About Me"
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, AboutFragment())
                    .commit()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
