/*
 Created by Ilya Reznik
 reznikid@altarix.ru
 skype be3bapuahta
 on 15.11.18 20:39
 */

package com.meetingsprod.meetings.main

import android.content.Context
import android.graphics.Rect
import android.support.annotation.DimenRes
import android.support.annotation.NonNull
import android.support.v7.widget.RecyclerView
import android.view.View


class ItemOffsetDecoration(private val mItemOffset: Int = 10) : RecyclerView.ItemDecoration() {

    constructor(@NonNull context: Context, @DimenRes itemOffsetId: Int) : this(
        context.getResources().getDimensionPixelSize(
            itemOffsetId
        )
    ) {
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset)
    }
}