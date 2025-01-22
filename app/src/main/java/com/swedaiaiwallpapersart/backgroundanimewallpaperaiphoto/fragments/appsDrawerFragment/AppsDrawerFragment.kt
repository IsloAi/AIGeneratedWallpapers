package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments.appsDrawerFragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentAppsDrawerBinding


class AppsDrawerFragment : Fragment() {
    private lateinit var appList: List<AppInfo>
    lateinit var adapter: AppAdapter
    lateinit var binding: FragmentAppsDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAppsDrawerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            adapter = AppAdapter(requireContext(), getAllApps())
            binding.Apps.adapter = adapter
            binding.Apps.layoutManager = GridLayoutManager(requireContext(), 4)
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getAllApps(): List<AppInfo> {
        val pManager = context?.packageManager
        appList = ArrayList()

        val i = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val allApps = pManager?.queryIntentActivities(i, 0)
        if (allApps != null) {
            for (ri in allApps) {
                val app = AppInfo(
                    ri.loadLabel(pManager),
                    ri.activityInfo.packageName,
                    ri.activityInfo.loadIcon(pManager)
                )
                Log.i("Log", app.packageName.toString())
                (appList as ArrayList<AppInfo>).add(app)
            }
        }
        return appList
    }

}