package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.interfaces

import androidx.constraintlayout.widget.ConstraintLayout
import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.models.Prompts

interface GetPromptDetails {
    //fun getPrompt(arrayList: ArrayList<Prompts>, layout: ConstraintLayout, position: Int)
    fun getPrompt(prompt:String)
}