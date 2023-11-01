package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentViewAllCreationsBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG.HistoryAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetbackOfID
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.RoomViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.ViewModelFactory

class ViewAllCreations : Fragment() {
    private var _binding:FragmentViewAllCreationsBinding ?=  null
    private val binding get() = _binding!!
    var roomDatabase:AppDatabase ?= null
    var viewModel:RoomViewModel ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentViewAllCreationsBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomDatabase = AppDatabase.getInstance(requireContext())
        viewModel = ViewModelProvider(this,ViewModelFactory(roomDatabase!!,0))[RoomViewModel::class.java]
        initHistory()
        setEvents()
    }


    fun setEvents(){
        binding.deleteAllHistory.setOnClickListener {
            viewModel?.deleteAll()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    fun initHistory(){

        viewModel?.allGetResponseIG?.observe(viewLifecycleOwner){myList->
            if(myList.isNotEmpty()){
//                binding.errorTitle.visibility = View.GONE
                binding.historyRecyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                val adapter  = CreationsAdapter(myList,object: GetbackOfID {
                    override fun getId(id:Int){
//                        navigate(id,0)
                    }
                })
                binding.historyRecyclerView.adapter = adapter
            }else{
                binding.historyRecyclerView.visibility = View.INVISIBLE
//                binding.errorTitle.visibility = View.VISIBLE
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}