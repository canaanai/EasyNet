package com.canaanai.net

/**
 * @author chenp
 * @version 2017-02-03 11:35
 */
interface IProgress {
    fun start()
    fun end()
    fun setProgress(progressValue: Int)
}