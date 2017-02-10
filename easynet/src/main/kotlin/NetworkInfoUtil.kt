package com.canaanai.net

import android.content.Context
import android.net.ConnectivityManager

/**
 * @author chenp
 * @version 2017-02-08 15:16
 */
class NetworkInfoUtil {
    companion object{
        @JvmStatic
        fun isNetworkAvailable(context: Context): Boolean{
            val networkInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager).activeNetworkInfo

            return networkInfo.isAvailable
        }
    }
}