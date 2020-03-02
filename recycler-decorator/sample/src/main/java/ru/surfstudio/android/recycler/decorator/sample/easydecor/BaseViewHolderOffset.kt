package ru.surfstudio.android.recycler.decorator.sample.easydecor

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.surfstudio.android.easyadapter.item.BaseItem

interface BaseViewHolderOffset<I : BaseItem<out RecyclerView.ViewHolder>> {
    fun getItemOffsets(outRect: Rect,
                       view: View,
                       recyclerView: RecyclerView,
                       state: RecyclerView.State,
                       baseItem: I?)
}