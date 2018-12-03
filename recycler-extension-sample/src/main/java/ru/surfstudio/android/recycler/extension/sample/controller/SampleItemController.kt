package ru.surfstudio.android.recycler.extension.sample.controller

import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_list_sample.*
import ru.surfstudio.android.easyadapter.controller.BindableItemController
import ru.surfstudio.android.easyadapter.holder.BindableViewHolder
import ru.surfstudio.android.recycler.extension.sample.Data
import ru.surfstudio.android.recycler.extension.sample.R

class SampleItemController : BindableItemController<Data, SampleItemController.Holder>() {
    override fun getItemId(data: Data): String = data.hashCode().toString()

    override fun createViewHolder(parent: ViewGroup?): Holder = Holder(parent)

    inner class Holder(parent: ViewGroup?) : BindableViewHolder<Data>(parent, R.layout.item_list_sample) {

        override fun bind(data: Data?) {
            item_list_sample_tv.text = data?.name
        }
    }
}