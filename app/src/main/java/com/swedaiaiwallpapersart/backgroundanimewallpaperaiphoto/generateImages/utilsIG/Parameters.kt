package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.utilsIG

import com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils.FirebaseUtils

class Parameters {
    companion object {
        val canvasPos = 0
        val varendpoint = "v3/text2img"
        val enhance_prompt = "no"
        val exact_Nprompt = ""
        val guess_mode = "no"
        val guidance_scale = 20.0
        val height = "1024"
        val instance_prompt = ""
        val key = "cBAKGvs2IWAC4XztfETeUPMbKiZvHIBrzmul6VhSpAyzxxtz1oJWxni9vqBG"
        var model_id = "sdxl"
        var model_name = "SDXL"
        val multi_lingual = "yes"
        val negative_prompt = "low quality, watermark,painting, extra fingers, mutated hands, poorly drawn hands, poorly drawn face, deformed, ugly, blurry, bad anatomy, bad proportions, extra limbs, cloned face, skinny, glitchy, double torso, extra arms, extra hands, mangled fingers, missing lips, ugly face, distorted face, extra legs, anime, double face, extra face,(((nudity))),(((nsfw))),(((nude))),(((upskirt))),(((bra))),(((nude))),(((latex))),(((boobs))),(((panty))),(((ass))),painting, extra fingers, mutated hands, poorly drawn hands, poorly drawn face, deformed, ugly, blurry, bad anatomy, bad proportions, extra limbs, cloned face, skinny, glitchy, double torso, extra arms, extra hands, mangled fingers, missing lips, ugly face, distorted face, extra legs, anime, dual, multiple,"
        val num_inference_steps = "51"
        val panorama = "no"
        val prompt = "one girl, Brown hair, Brown eyes, slim weight scarlett person holding a positive pregnancy"
        val promptsBuilder = ""
        val safety_checker = "yes"
        val samples = "4"
        val scheduler = "UniPCMultistepScheduler"
        val self_attention = "yes"
        val steps = "21"
        val strength = 1.0
        val token = FirebaseUtils.FIREBASE_TOKEN
        val type = "a"
        val upscale = "no"
        val webhook = "https://edecator.com/wallpaperApp/api/webhook.php"
        val width = "720"
    }

}