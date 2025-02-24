package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.R
import com.swedai.ai.wallpapers.art.background.anime_wallpaper.aiphoto.databinding.FragmentConsentBinding


class ConsentFragment : Fragment() {

    private lateinit var binding: FragmentConsentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConsentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setGradientText(binding.toolBarHeading, binding.Heading, binding.subTitle)
        binding.agree.setOnClickListener {
            findNavController().navigate(R.id.permissionFragment)
        }
        binding.disagree.setOnClickListener {
            disagreeDialog()
        }
    }

    private fun disagreeDialog() {
        val disagreeDialog = Dialog(requireActivity()).apply {
            setContentView(R.layout.disagree_consent_dialog)
            setCancelable(false)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        disagreeDialog.show()

        val confirmText: TextView = disagreeDialog.findViewById(R.id.confirmationText)
        val quitText: TextView = disagreeDialog.findViewById(R.id.quit)
        val continueBtn: TextView = disagreeDialog.findViewById(R.id.continue_btn)

        val text =
            "By continuing to use this app you accept our Privacy Policy & Terms of conditions"
        val spannableString = SpannableString(text)
        // Find the start and end index of "Privacy Policy"
        val startIndex = text.indexOf("Privacy Policy & Terms of conditions")
        val endIndex = startIndex + "Privacy Policy & Terms of conditions".length
        // Make it clickable
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent =
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://isloai.com/privacy/")
                    )
                widget.context.startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true // Underline the text
                ds.color = Color.BLUE // Change color if needed
            }
        }
        // Apply ClickableSpan to "Privacy Policy"
        spannableString.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Set text and enable click handling
        confirmText.text = spannableString
        confirmText.movementMethod =
            LinkMovementMethod.getInstance() //Enable clicking

        disagreeDialog.apply {
            quitText.setOnClickListener {
                requireActivity().finishAffinity()
            }
            continueBtn.setOnClickListener {
                findNavController().navigate(R.id.permissionFragment)
                dismiss()
            }
        }
    }

    private fun setGradientText(vararg textViews: TextView) {
        val customColors = intArrayOf(
            Color.parseColor("#FC9502"),
            Color.parseColor("#FF6726")
        )

        for (textView in textViews) {
            val paint: TextPaint = textView.paint
            val width: Float = paint.measureText(textView.text.toString())

            val shader = LinearGradient(
                0f, 0f, width, textView.textSize,
                customColors, null, Shader.TileMode.CLAMP
            )
            textView.paint.shader = shader
            textView.invalidate()  // Ensure UI updates
        }
    }

}