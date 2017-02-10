package com.canaanai.net

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * @author chenp
 * @version 2017-01-17 15:16
 *
 * 网络状况处理
 * 待修改进度显示
 */
class NetTask<T>(val tag: String, val observer: Observable<T>, val easyNet: EasyNet, val baseApi: BaseApiInterface, var progressHandler: IProgress? = null) {

    //private var subscription: Subscription? = null

    fun showProgress(): NetTask<T>{
        return NetTask(tag, observer.doOnNext { progressHandler?.start() }, easyNet, baseApi, progressHandler)
    }

    fun hideProgress(): NetTask<T>{
        return NetTask(tag, observer.doOnNext { progressHandler?.end() }, easyNet, baseApi, progressHandler)
    }

    fun setProgressValue(value: Int): NetTask<T>{
        return NetTask(tag, observer.doOnNext { progressHandler?.setProgress(value) }, easyNet, baseApi, progressHandler)
    }

    /*fun checkNetworkAvailable(context: Context): NetTask<T>{

        return NetTask(tag, observer.takeWhile { NetworkInfoUtil.isNetworkAvailable(context) }, easyNet, baseApi, progressHandler)
    }*/

    fun workOnMainThread(): NetTask<T>{
        return NetTask(tag, observer.observeOn(AndroidSchedulers.mainThread()), easyNet, baseApi, progressHandler)
    }

    fun workOnSubThread(): NetTask<T>{
        return NetTask(tag, observer.observeOn(Schedulers.io()), easyNet, baseApi, progressHandler)
    }

    fun doNext(callback: NetAction<T>): NetTask<T>{
        return NetTask(tag, observer.doOnNext { callback.call(it) }, easyNet, baseApi, progressHandler)
    }

    fun doNext(callback: (T) -> Unit): NetTask<T>{
        return NetTask(tag, observer.doOnNext { callback(it) }, easyNet, baseApi, progressHandler)
    }

    /*fun <E> doNextAsync(callback: (param: T) -> E) : NetTask<E>{
        return NetTask(observer
                .observeOn(Schedulers.io())
                .map { callback(it) }, baseApi)
    }*/

    fun <E> map(callback: (T) -> E) : NetTask<E>{
        return NetTask(tag, observer.map { callback(it) }, easyNet, baseApi, progressHandler)
    }

    /*fun <R> doGet(rClass: Class<R>) : NetTask<R>{
        return NetTask(observer
                .flatMap { params -> baseApi.doGet(request = beanToMap(params))
                        .map { jsonToBean(it, rClass) } }, baseApi)
    }*/

    @JvmOverloads
    fun <R> doGet(url: String = "", rClass: Class<R>): NetTask<R>{
        return NetTask(tag, observer
                .flatMap {
                    val obj = beanToMap(it)

                    easyNet.netInterceptor?.onRequest(tag, obj)
                    baseApi.doGet(url, obj)
                }
                .map {
                    val obj = jsonToBean(it, rClass)

                    obj?.let {
                        easyNet.netInterceptor?.onResponse(tag, obj)
                    }

                    obj
                }, easyNet, baseApi, progressHandler)
    }

    @JvmOverloads
    fun <R> doPost(path: String = "", rClass: Class<R>): NetTask<R>{
        return NetTask(tag, observer
                .flatMap {
                    val obj = beanToMap(it)

                    easyNet.netInterceptor?.onRequest(tag, obj)
                    baseApi.doPost(path, obj)
                }
                .map {
                    val obj = jsonToBean(it, rClass)

                    obj?.let {
                        easyNet.netInterceptor?.onResponse(tag, obj)
                    }

                    obj
                }, easyNet, baseApi, progressHandler)
    }

    fun start(){
        easyNet.addSubscription(tag, observer.subscribe(
                {  },
                { throwable ->
                    run {
                        //Log.e("NetTask", throwable.message)
                        throwable.printStackTrace()
                        easyNet.removeSubscription(tag)
                    }
                },
                {
                    Log.d("NetTask", "Complete")
                    easyNet.removeSubscription(tag)
                }))

    }

    fun start(complete: (T) -> Unit, error: (Throwable) -> Unit){
        easyNet.addSubscription(tag, observer.subscribe(
                { complete(it) },
                { throwable ->
                    run {
                        Log.e("NetTask", throwable.message)
                        error(throwable)
                        easyNet.removeSubscription(tag)
                    }
                },
                {
                    Log.d("NetTask", "Complete")
                    easyNet.removeSubscription(tag)
                }))
    }

    companion object{
        @JvmStatic
        fun <G> jsonToBean(json: String, clazz: Class<G>) :G{
            @Suppress("UNCHECKED_CAST")
            if ( clazz == String::class.java)
                return json as G

            return Gson().fromJson(json, clazz)
        }

        @JvmStatic
        fun beanToMap(bean: Any?): Map<String, String>{
            val map = HashMap<String, String>()

            bean?.let {
                val json = Gson().toJson(bean)
                val jsonObject = JSONObject(json)

                for ( key in jsonObject.keys()){
                    map[key] = jsonObject.getString(key)
                }
            }

            return map
        }
    }
}