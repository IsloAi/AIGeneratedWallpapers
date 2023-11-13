package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.debug.databinding
.FragmentViewAllCreationsBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.adapters.ApiCategoriesListAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.adaptersIG.HistoryAdapter
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces.GetbackOfID
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.GetResponseIGEntity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.RoomViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.ViewModelFactory
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.ratrofit.RetrofitInstance
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.RvItemDecore

class ViewAllCreations : Fragment() {
    private var _binding:FragmentViewAllCreationsBinding ?=  null
    private val binding get() = _binding!!
    var roomDatabase:AppDatabase ?= null
    var viewModel:RoomViewModel ?= null

    private var existGems:Int? = null
    private val postDataOnServer = PostDataOnServer()


    private var isSelectionMode = false
    var adapter:CreationsAdapter ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentViewAllCreationsBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        existGems = MySharePreference.getGemsValue(requireContext())
        roomDatabase = AppDatabase.getInstance(requireContext())
        viewModel = ViewModelProvider(this,ViewModelFactory(roomDatabase!!,0))[RoomViewModel::class.java]
        initHistory()
        setEvents()
    }


    fun setEvents(){
        binding.deleteAllHistory.setOnClickListener {

            binding.deleteAllHistory.visibility = View.GONE
            binding.selectAll.visibility = View.VISIBLE
            isSelectionMode = true
            adapter?.updateSelectionMode(isSelectionMode)
        }



        binding.backButton.setOnClickListener {
            if (isSelectionMode){

                isSelectionMode = false
                adapter?.updateSelectionMode(isSelectionMode)
                binding.deleteAllHistory.visibility = View.VISIBLE
                binding.selectAll.visibility = View.GONE
                binding.deleteCreations.visibility = View.GONE
            }else{
                findNavController().navigateUp()
            }
        }


        binding.deleteCreations.setOnClickListener {
            viewModel?.deleteAll(adapter?.getSelectedlist()!!)

            binding.deleteAllHistory.visibility = View.VISIBLE
            binding.selectAll.visibility = View.GONE
            isSelectionMode = false
            adapter?.updateSelectionMode(isSelectionMode)
        }


        binding.selectAll.setOnClickListener {

            if (binding.selectAll.text == getString(R.string.unselect_all)){
                binding.selectAll.text =  "Select All"
                adapter?.unselectAll()
                binding.deleteCreations.visibility = View.GONE
            }else{
                binding.selectAll.text =  "Unselect All"
                adapter?.selectAll()
                binding.deleteCreations.visibility = View.VISIBLE
            }

        }
    }


    fun initHistory(){

        binding.historyRecyclerView.addItemDecoration(
            RvItemDecore(
                3,
                20,
                true
            )
        )

        viewModel?.allGetResponseIG?.observe(viewLifecycleOwner){myList->
            if(myList.isNotEmpty()){
                binding.emptySupport.visibility = View.GONE
                binding.historyRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
                adapter  = CreationsAdapter(myList,isSelectionMode,object: CreationsAdapter.CreationSelectionInterface {

                    override fun setOnClick(id: Int, getResponseIGEntity: GetResponseIGEntity) {
//                        viewModel!!.deleteSingleImage(getResponseIGEntity)
                    }

                    override fun viewMyCreations(id: Int, list: ArrayList<GetResponseIGEntity>) {
                        if (isSelectionMode){
                            if (list.size > 0){
                                binding.deleteCreations.visibility = View.VISIBLE
                            }else{
                                binding.deleteCreations.visibility = View.GONE
                            }
                        }else{
                            navigate(id,0)

                        }
                    }
                })


                binding.historyRecyclerView.adapter = adapter
            }else{
                binding.historyRecyclerView.visibility = View.GONE
                binding.emptySupport.visibility = View.VISIBLE
//                binding.errorTitle.visibility = View.VISIBLE
            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    private fun postGems(){
        val totalGems = existGems?.minus(10)
        postDataOnServer.gemsPostData(requireContext(), MySharePreference.getDeviceID(requireContext())!!,
            RetrofitInstance.getInstance(),totalGems!!, PostDataOnServer.isPlan)
        MySharePreference.setGemsValue(requireContext(),totalGems)
    }
    private fun navigate(listId: Int, timeDisplay: Int?){
        if(timeDisplay != null){
            if(timeDisplay>0){
                postGems()
            }
            val bundle = Bundle().apply {
                putInt("listId",listId)
                putInt("timeDisplay", timeDisplay)
            }
            requireParentFragment().findNavController().navigate(R.id.myViewCreationFragment,bundle)
        }else{
            Toast.makeText(requireContext(),
                getString(R.string.error_please_try_again), Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}