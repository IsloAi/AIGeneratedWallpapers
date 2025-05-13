package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.generateImages.roomDB

import androidx.room.TypeConverter

class ArrayListStringConverter {
    @TypeConverter
    fun fromString(value: String?): ArrayList<String>? {
        if (value == null) {
            return null
        }
        val array = value.split(",").toTypedArray()
        val list = ArrayList<String>()
        for (item in array) {
            list.add(item)
        }
        return list
    }

    @TypeConverter
    fun toString(list: ArrayList<String>?): String? {
        if (list == null) {
            return null
        }
        val value = StringBuilder()
        for (item in list) {
            value.append(item)
            value.append(",")
        }
        return value.toString()
    }
}

