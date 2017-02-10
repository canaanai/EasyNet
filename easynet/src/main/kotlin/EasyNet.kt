@file:JvmName("EasyNetHelper")
package com.canaanai.net

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers
import java.util.*

/**
 * @author chenp
 * @version 2017-01-16 16:27
 *
 * 添加生命周期管理
 */
class EasyNet(val retrofit: Retrofit, var progress: IProgress? = null, val netInterceptor: INetInterceptor? = null) {

    /*init {
        progress = progressHandler
    }*/

    val baseApi: BaseApiInterface = retrofit.create(BaseApiInterface::class.java)
    //var progress: IProgress? = null
    val taskMap: HashMap<String, Subscription> = HashMap()

    fun progress(progressHandler: IProgress?): EasyNet{
        progress = progressHandler

        return this
    }

    fun <T> request(tag: String, request: T): NetTask<T>{
        return NetTask(tag,
                Observable.just(request).subscribeOn(Schedulers.io()), this,
                baseApi, progress)
    }

    fun cancleTask(tag: String){
        val subscription = taskMap.remove(tag)

        subscription?.unsubscribe()
    }

    fun addSubscription(tag: String, subscription: Subscription){
        taskMap[tag] = subscription
    }

    fun removeSubscription(tag: String){
        taskMap.remove(tag)
    }

    class Builder{
        private var url: String? = null
        private var iProgress: IProgress? = null
        private var netInterceptor: INetInterceptor? = null

        fun url(url: String): Builder{
            this.url = url

            return this
        }

        fun progress(iProgress: IProgress): Builder{
            this.iProgress = iProgress

            return this
        }

        fun netInterceptor(netInterceptor: INetInterceptor): Builder{
            this.netInterceptor = netInterceptor

            return this
        }

        fun builder(): EasyNet{
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addNetworkInterceptor(interceptor).build()
            val bRetrofit = Retrofit.Builder()
                    .client(client)
                    .baseUrl(url)
                    //.addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()

            return EasyNet(bRetrofit, iProgress, netInterceptor)
        }

        /*fun <T> request(requestBody: T): NetTask<T>{
            val observe = Observable.just(requestBody).subscribeOn(Schedulers.io())

            return NetTask(observe, ServerBuilder.NipponColorInstance.buildServer(BaseApiInterface::class.java))
        }*/
    }
}

fun <T> Observable<T>.easyNet(easyNet: EasyNet, tag: String): NetTask<T>{
    return NetTask(tag, this, easyNet, easyNet.baseApi, easyNet.progress)
}