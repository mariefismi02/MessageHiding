package com.mariefismi02.messagehiding.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mariefismi02.messagehiding.R
import com.mariefismi02.messagehiding.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.populateFragment(EncryptFragment(), "ENKRIPSI")
        adapter.populateFragment(DecryptFragment(), "DEKRIPSI")
        viewpager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
        tabs.bringToFront()
    }
}