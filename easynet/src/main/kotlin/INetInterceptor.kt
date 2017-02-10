package com.canaanai.net

/**
 * @author chenp
 * @version 2017-02-04 16:26
 */
interface INetInterceptor {
    fun onRequest(tag: String, data: Any?)
    fun onResponse(tag: String, data: Any?)
}