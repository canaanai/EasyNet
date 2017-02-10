package com.canaanai.net

import retrofit2.http.*
import rx.Observable
import java.util.*

/**
 * @author chenp
 * @version 2017-01-17 14:59
 */
interface BaseApiInterface {

    /*@GET()
    fun doGet(@QueryMap param: Map<String, String>): Observable<String>*/

    /*@GET("{url}")
    fun doPathGet(@Path("url") urlPath: String, @QueryMap param: Map<String, String> = HashMap<String, String>()): Observable<String>*/

    @GET()
    fun doGet(@Url url: String = "", @QueryMap request: Map<String, String> = HashMap<String, String>()): Observable<String>

    /*@FormUrlEncoded
    @POST()
    fun doPost(@FieldMap param: Map<String, String>): Observable<String>*/

    @FormUrlEncoded
    @POST()
    fun doPost(@Url url: String = "", @FieldMap request: Map<String, String> = HashMap<String, String>(), @QueryMap queryParams: Map<String, String> = HashMap<String, String>()): Observable<String>

    /*@FormUrlEncoded
    @POST("{url}")
    fun doPathPost(@Path("url") urlPath: String, @FieldMap param: Map<String, String> = HashMap<String, String>()): Observable<String>*/


}