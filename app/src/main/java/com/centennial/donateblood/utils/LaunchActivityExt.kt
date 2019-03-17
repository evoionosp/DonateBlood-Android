package com.centennial.donateblood.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

inline fun <reified T : Any> Activity.ExtLaunchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}) {

    val intent = newIntent<T>(this)
    intent.init()
    startActivityForResult(intent, requestCode, options)
}

inline fun <reified T : Any> Context.ExtLaunchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}) {

    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)

inline fun Activity.ExtLaunchActivity(
    intent: Intent,
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}) {
    startActivityForResult(intent, requestCode, options)
}

inline fun Context.ExtLaunchActivity(
    intent: Intent,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}) {
    startActivity(intent, options)
}