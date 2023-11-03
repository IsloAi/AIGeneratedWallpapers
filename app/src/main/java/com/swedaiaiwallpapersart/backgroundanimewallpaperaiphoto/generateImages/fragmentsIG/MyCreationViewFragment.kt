package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.fragmentsIG

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentMyCreationViewBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.MainActivity
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.AppDatabase
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.RoomViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB.ViewModelFactory
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.ImageListViewModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class MyCreationViewFragment : Fragment() {
    private var _binding:FragmentMyCreationViewBinding ?= null
    private val binding get() = _binding!!

    private var bindingRef: WeakReference<FragmentMyCreationViewBinding>? = null

    private var timer:CountDownTimer ?= null

    private var myContext: Context?= null
    private lateinit var listViewModel: ImageListViewModel
    private var timeDisplay = 0
    private lateinit var  viewModel:RoomViewModel
    private var myId = 0
    private var myActivity : MainActivity? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomDatabase = AppDatabase.getInstance(requireContext())
        listViewModel = ViewModelProvider(requireActivity())[ImageListViewModel::class.java]
        myId = arguments?.getInt("listId")!!
        val  getTime= arguments?.getInt("timeDisplay")!!
        viewModel = ViewModelProvider(this,ViewModelFactory(roomDatabase,myId))[RoomViewModel::class.java]
        if(getTime>0){
            timeDisplay = getTime+5
        }else{
            timeDisplay = 0
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View{
        // Inflate the layout for this fragment
        _binding = FragmentMyCreationViewBinding.inflate(inflater,container,false)
        bindingRef = WeakReference(_binding)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(myContext!= null){
            onCreateCalling()
        }


        Glide.with(requireContext())
            .asGif()
            .load(R.raw.gems_animaion)
            .into(binding.animationDdd)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        myContext = context
    }
    private fun onCreateCalling(){
        myActivity = activity as MainActivity
        swipeRefreshLayout = binding.swipeLayout
        binding.gemsText.text = MySharePreference.getGemsValue(requireContext()).toString()
        val clipboardManager = myContext?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        binding.copyButton.visibility = INVISIBLE
        if(timeDisplay==0){
            binding.notificationLayout.visibility = GONE
            binding.copyButton.visibility = VISIBLE
        }
        val time = (timeDisplay*1000)
        binding.notificationMessage.text = "Estimated time to load..."
        startCountdown(timeDisplay,binding.textCounter)

        lifecycleScope.launch(Dispatchers.Main) {

            delay(time.toLong())
            binding.loading1.visibility = GONE
            binding.loading2.visibility = GONE
            binding.loading3.visibility = GONE
            binding.loading4.visibility = GONE
            loadDate()
            binding.copyButton.visibility = VISIBLE
        }
        binding.backButton.setOnClickListener { findNavController().navigateUp()}
        swipeRefreshLayout.setOnRefreshListener {
            loadDate()
        }
        binding.copyButton.setOnClickListener {
            val textToCopy = binding.prompt.text.toString()
            val clip = ClipData.newPlainText("Copied Text", textToCopy)
            clipboardManager.setPrimaryClip(clip)
            Toast.makeText(myContext, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
    private fun loadDate(){
            viewModel.getResponseIGById.observe(viewLifecycleOwner){
                binding.prompt.text = it?.prompt
                if(it?.future_links.isNullOrEmpty()){
                    Log.d("imageLists", "future_links is Empty")
                    setImage(it?.output!!,it.prompt,myId)
                }else if(it?.output.isNullOrEmpty()){
                    Log.d("imageLists", "future_links is not empty   elssssssss")
                    setImage(it?.future_links!!,it.prompt,myId)
                }
            }

        swipeRefreshLayout.isRefreshing = false
    }
    private fun setImage(list: ArrayList<String>, prompt: String?, id: Int){
        Glide.with(myContext!!).load(list[0]).into(binding.imageView1)
        Glide.with(myContext!!).load(list[1]).into(binding.imageView2)
        Glide.with(myContext!!).load(list[2]).into(binding.imageView3)
        Glide.with(myContext!!).load(list[3]).into(binding.imageView4)
        binding.imageView1.setOnClickListener {navigate(0, list, prompt, id)}
        binding.imageView2.setOnClickListener {navigate(1, list, prompt, id)}
        binding.imageView3.setOnClickListener {navigate(2, list, prompt, id)}
        binding.imageView4.setOnClickListener {navigate(3, list, prompt, id)}
    }
    private fun navigate(click: Int, list: ArrayList<String>, prompt: String?, id: Int){
        timeDisplay = 0
        val gson = Gson()
        val arrayListJson = gson.toJson(list)
       val bundle =  Bundle().apply {
            putString("arrayListJson",arrayListJson)
            putInt("position",click)
           putString("prompt",prompt)
           putInt("id",id)
        }
        findNavController().navigate(R.id.action_myViewCreationFragment_to_creationSliderViewFragment,bundle)
    }
    private fun startCountdown(initialSeconds: Int,textView:TextView) {
      timer = object : CountDownTimer((initialSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update the UI with the remaining seconds
                val binding = bindingRef?.get()
                val remainingSeconds = (millisUntilFinished / 1000).toInt()
                if (binding != null){
                    textView.text = "$remainingSeconds sec"

                }
            }
            override fun onFinish() {
                val binding = bindingRef?.get()
                if (binding!=null){
                    textView.text = ""
                    binding.notificationMessage.text = "if still not loaded then swipe down\nto refresh after few second"
                }

            }
        }
        timer?.start()


    }


    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }


}