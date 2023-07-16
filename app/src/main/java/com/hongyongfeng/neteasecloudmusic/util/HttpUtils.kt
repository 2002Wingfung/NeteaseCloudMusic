package com.hongyongfeng.neteasecloudmusic.util

import org.json.JSONArray

class HttpUtils {
    companion object{
        fun transferToListMap(string: String,keyWords:String?):List<Map<String,Any>>{
            val stringListMap: MutableList<Map<String, Any>> = ArrayList()
            //截取json字符串,因为jsonArray的格式是[]，只能解析这种格式的数据,而返回的数据的格式是{}
            //截取json字符串,因为jsonArray的格式是[]，只能解析这种格式的数据,而返回的数据的格式是{}
            try {
                val indexStart = string.indexOf('[')
                val indexEnd = string.lastIndexOf(']')
                val jsonArray = JSONArray(string.substring(indexStart, indexEnd + 1))
                var jsonObject = jsonArray.getJSONObject(0)
                if (keyWords==null){
                    val keys = jsonObject.keys()
                    val jsonFieldList: MutableList<String> = ArrayList()
                    //读取其中一个JsonObject的字段
                    while (keys.hasNext()) {
                        val jsonField = keys.next()
                        jsonFieldList.add(jsonField)
                    }
                    for (i in 0 until jsonArray.length()) {
                        jsonObject = jsonArray.getJSONObject(i)
                        val stringMap: MutableMap<String, Any> = HashMap(1)
                        //将jsonObject中所有的字段的值都存进map中
                        for (field in jsonFieldList) {
                            stringMap[field] = jsonObject[field]
                        }
                        stringListMap.add(stringMap)
                    }
                }else{
                    for (i in 0 until jsonArray.length()) {
                        jsonObject = jsonArray.getJSONObject(i)
                        val stringMap: MutableMap<String, Any> = HashMap(1)
                        stringMap[keyWords]=jsonObject[keyWords]
                        stringListMap.add(stringMap)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return stringListMap
        }
    }
}