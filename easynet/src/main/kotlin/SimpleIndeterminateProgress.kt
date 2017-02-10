package com.canaanai.net

import android.app.Activity
import android.app.ProgressDialog

/**
 * @author chenp
 * @version 2017-02-03 16:21
 */
class SimpleIndeterminateProgress(val activity: Activity): IProgress {

    private var progressDialog: ProgressDialog? = null

    override fun setProgress(progressValue: Int) {

    }

    override fun end() {
        progressDialog?.hide()
    }

    override fun start() {
        progressDialog = ProgressDialog.show(activity, "", "")
    }
}