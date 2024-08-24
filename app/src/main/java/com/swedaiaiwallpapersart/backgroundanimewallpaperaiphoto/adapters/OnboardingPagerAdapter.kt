package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.welcome.WelcomeFragment3
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.interfaces.ViewPagerCallback

class OnboardingPagerAdapter(fragmentActivity: FragmentActivity, private val viewPagerCallback: ViewPagerCallback) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val fragmentTitleList: MutableList<String> = ArrayList()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
        if (fragment is WelcomeFragment3) {
            fragment.viewPagerCallback = viewPagerCallback
        }
    }

    fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }
}