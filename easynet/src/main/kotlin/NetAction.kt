package com.canaanai.net

import rx.functions.Action
import rx.functions.Action1

/**
 * @author chenp
 * @version 2017-01-20 10:38
 */
interface NetAction<T> : Action1<T> {
    override fun call(t: T)
}