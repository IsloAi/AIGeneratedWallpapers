package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentFeedbackBinding
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.data.remote.EndPointsInterface
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.models.FeedbackModel
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.MySharePreference
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.PostDataOnServer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@AndroidEntryPoint
class FeedbackFragment : Fragment() {
   private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!
    private val postDataOnServer = PostDataOnServer()

    @Inject
    lateinit var endPointsInterface: EndPointsInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =FragmentFeedbackBinding.inflate(inflater,container,false)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mail = view.findViewById<EditText>(R.id.enterMail)
        val name = view.findViewById<EditText>(R.id.enterName)
        val subject = view.findViewById<EditText>(R.id.enterSubject)
        val message = view.findViewById<EditText>(R.id.enterMessage)
        message.post {
            message.setSelection(message.text.length)
            message.requestFocus()
        }
        view.findViewById<RelativeLayout>(R.id.backButton).setOnClickListener { findNavController().navigateUp() }
        view.findViewById<Button>(R.id.sendButton)?.setOnClickListener {
            val getMail = mail?.text.toString()
            val getName = name?.text.toString()
            val getSubject = subject?.text.toString()
            val getMessage = message?.text.toString()
            if(MySharePreference.getFeedbackValue(requireContext())){
                Toast.makeText(requireContext(),
                    getString(R.string.you_already_send_feedback_now_you_can_send_again_after_24_hours), Toast.LENGTH_SHORT).show()
            }else{
                if(getMail.isEmpty()){
                    mail?.error = getString(R.string.must_required_your_mail)
                    mail?.requestFocus()
                }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(getMail).matches()){
                    mail.error = getString(R.string.enter_a_valid_email_address)
                    mail.requestFocus()
                }
                else if(getName.isEmpty()){
                    name?.error = getString(R.string.must_required_your_name)
                    name?.requestFocus()
                }else if(getSubject.isEmpty()){
                    subject?.error = getString(R.string.must_required_your_subject)
                    subject?.requestFocus()
                }else{
                    try {
                        lifecycleScope.launch(Dispatchers.IO) {
                            endPointsInterface.postData(FeedbackModel(getMail,getName,getSubject,getMessage,
                                MySharePreference.getDeviceID(requireContext())!!
                            ))
                        }
                    }catch (e:Exception){

                    }catch (e:UnknownHostException){
                        e.printStackTrace()
                    }




//                    postDataOnServer.sendFeedback(requireContext(),getMail,getName,getSubject,getMessage,
//                        MySharePreference.getDeviceID(requireContext())!!)
                    mail?.setText("")
                    name?.setText("")
                    subject?.setText("")
                    message?.setText("")
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding =null
    }



}