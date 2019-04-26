package com.loro.klinechart

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.loro.klinechart.bean.KLineEntity
import java.nio.charset.Charset

/**
 * 模拟网络请求
 */

object DataRequest {
    private var datas: MutableList<KLineEntity>? = null

    private fun getStringFromAssert(context: Context, fileName: String): String {
        try {
            val `in` = context.resources.assets.open(fileName)
            val length = `in`.available()
            val buffer = ByteArray(length)
            `in`.read(buffer)
            return String(buffer, 0, buffer.size, Charset.defaultCharset())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun getALL(context: Context): MutableList<KLineEntity> {
        if (datas == null) {
            datas = try {
                val data = Gson().fromJson<MutableList<KLineEntity>>(
                    getStringFromAssert(context, "ibm.json"),
                    object : TypeToken<MutableList<KLineEntity>>() {
                    }.type
                )
                DataHelper.calculate(data)
                data
            } catch (e: Exception) {
                e.printStackTrace()
                mutableListOf()
            }
        }
        return datas!!.take(200) as MutableList<KLineEntity>
    }

}


