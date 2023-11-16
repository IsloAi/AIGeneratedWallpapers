package com.swedaiaiwallpapersart.backgroundanimewallpaperaiphoto.utils

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class CustomGridLayoutManager(
    context: Context,
    spanCount: Int,
    private val adapter: RecyclerView.Adapter<*>
) : GridLayoutManager(context, spanCount) {

    override fun getSpanSizeLookup(): GridLayoutManager.SpanSizeLookup {
        return object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.getItemViewType(position) == 1) {
                    spanCount // Full width for the ad
                } else {
                    1 // Regular item occupies 1 span
                }
            }
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        applyItemDecoration()
    }

    private fun applyItemDecoration() {
        val spacing = 8 // Adjust spacing as needed
        val spanCount = spanCount
        val itemCount = adapter.itemCount
        val adPositions = mutableListOf<Int>()

        // Collect ad positions
        for (i in 0 until itemCount) {
            if (adapter.getItemViewType(i) == 1) {
                adPositions.add(i)
            }
        }

        // Apply decoration to regular items
        for (position in 0 until itemCount) {
            if (!adPositions.contains(position)) {
                val column = position % spanCount
                val top = if (position < spanCount) 0 else spacing
                val left = column * spacing / spanCount
                val right = spacing - (column + 1) * spacing / spanCount
                val bottom = spacing
                val params = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(left, top, right, bottom)
            }
        }
    }
}
