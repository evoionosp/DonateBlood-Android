package com.centennial.donateblood.extensions

import androidx.recyclerview.widget.RecyclerView
import com.centennial.donateblood.utils.RecyclerItemClickListener


fun String?.IsNullOrEmpty(): Boolean {
    if (this == null)
        return true
    if (this == "")
        return true
    return false
}




fun RecyclerView.affectOnItemClick(listener: RecyclerItemClickListener.OnClickListener) {
    this.addOnChildAttachStateChangeListener(RecyclerItemClickListener(this, listener, null))
}

fun RecyclerView.affectOnLongItemClick(listener: RecyclerItemClickListener.OnLongClickListener) {
    this.addOnChildAttachStateChangeListener(RecyclerItemClickListener(this, null, listener))
}

fun RecyclerView.affectOnItemClicks(onClick: RecyclerItemClickListener.OnClickListener, onLongClick: RecyclerItemClickListener.OnLongClickListener) {
    this.addOnChildAttachStateChangeListener(RecyclerItemClickListener(this, onClick, onLongClick))
}
