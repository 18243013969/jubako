package com.justeat.jubako.recyclerviews.util

import android.graphics.Rect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.justeat.jubako.Jubako
import com.justeat.jubako.recyclerviews.widgets.JubakoRecyclerView

open class JubakoScreenFiller(
    val orientation: Orientation,
    val logger: Jubako.Logger,
    val log: Boolean = true,
    val hasMore: () -> Boolean,
    val loadMore: () -> Unit,
    val onFilled: () -> Unit = {}
) : IJubakoScreenFiller {

    enum class Orientation {
        HORIZONTAL,
        VERTICAL
    }

    override fun attach(recyclerView: RecyclerView) {
        (recyclerView as JubakoRecyclerView).onDrawComplete = {
            fill(recyclerView)
        }
    }

    private fun fill(recyclerView: JubakoRecyclerView) {
        if (hasMore()) {
            val lm = (recyclerView.layoutManager as LinearLayoutManager)
            val lastVisibleItemPos = lm.findLastVisibleItemPosition()
            if (lastVisibleItemPos > RecyclerView.NO_POSITION) {
                val view = lm.findViewByPosition(lastVisibleItemPos)
                if (view == null && log) logger.log(TAG, "$lastVisibleItemPos view was null")
                if (view != null) {
                    val itemRect = Rect()
                    val areaRect = Rect()
                    recyclerView.getGlobalVisibleRect(areaRect)
                    itemRect.left = lm.getDecoratedLeft(view)
                    itemRect.right = lm.getDecoratedRight(view)
                    itemRect.top = lm.getDecoratedTop(view)
                    itemRect.bottom = lm.getDecoratedBottom(view)

                    val areaWidth = areaRect.width()
                    val areaHeight = areaRect.height()

                    val filled = when (orientation) {
                        Orientation.HORIZONTAL -> {
                            itemRect.right > areaWidth
                        }
                        else -> {
                            itemRect.bottom > areaHeight
                        }
                    }

                    val extent = when (orientation) {
                        Orientation.HORIZONTAL -> {
                            areaWidth
                        }
                        else -> {
                            areaHeight
                        }
                    }

                    if (filled) {
                        if (log) logger.log(
                            TAG,
                            "Fill $orientation Complete",
                            "pos: $lastVisibleItemPos, extent: $extent, rect: $itemRect"
                        )
                        onFilled()
                        // TODO once we are done we should detatch from
                        //  draw complete callbacks however given
                        //  the size of the loading indicator can cause
                        //  grid fill example to not fill entirely to the end
                        //  of the screen and prevents more items loading
                        // We are done no need to remain attached
                        // recyclerView.onDrawComplete = {}
                    } else if (hasMore()) {
                        if (log) logger.log(
                            TAG,
                            "Fill $orientation  Filling",
                            "pos: $lastVisibleItemPos, extent: $extent, rect: $itemRect"
                        )
                        loadMore()
                    }
                }
            }
        } else {
            if (log) logger.log(
                TAG,
                "Initial Fill $orientation",
                "Nothing more to load"
            )
        }
    }
}

private val TAG = JubakoScreenFiller::class.java.simpleName
