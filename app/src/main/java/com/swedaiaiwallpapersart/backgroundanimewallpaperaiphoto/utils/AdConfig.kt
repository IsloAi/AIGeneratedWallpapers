package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

object AdConfig {
    var firstAdLineTrending = 0
    var lineCountTrending = 0
    var adStatusTrending = 1

    var firstAdLineCategoryArt = 0
    var lineCountCategoryArt = 0
    var adStatusCategoryArt = 1

    var firstAdLineViewListWallSRC = 0
    var lineCountViewListWallSRC = 0
    var adStatusViewListWallSRC = 1


    var firstAdLineMostUsed = 0
    var lineCountMostUsed = 0
    var adStatusMostUsed = 1

    var showOnboarding = true

    var inAppConfig = true

    var tabPositions = arrayOf("Live", "Popular", "Double", "Category", "Anime", "Car", "Charging")

    var categoryOrder:List<String> = listOf( "Sadness", "Car", "Motor Bike", "Fantasy", "Animal","New Year", "Christmas", "Travel", "Ocean", "Nature", "Mountains", "Music", "Love", "Art", "Space", "Adstract", "Tech", "Black And White", "Architecture", "Artistic", "Pattern", "City", "Minimal", "Vintage","4K", "Anime", "Super Heros", "IOS", "Dark")



    var BASE_URL_DATA:String = ""

    val BASE_URL = "https://vps.edecator.com/wallpaper_App/V3/"
    val HD_ImageUrl = "${BASE_URL_DATA}/images/"

    val Compressed_Image_url = "${BASE_URL_DATA}/compress/"

    val LIVE_WALL_URL = "http://edecator.com/wallpaperApp/Live_Wallpaper/20.mp4"

    var iapScreenType = 2

    var ISPAIDUSER = false

    var regularWallpaperFlow = 0
}