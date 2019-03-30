package com.centennial.donateblood.extensions

fun String?.IsNullOrEmpty(): Boolean {
    if (this == null)
        return true
    if (this == "")
        return true
    return false
}