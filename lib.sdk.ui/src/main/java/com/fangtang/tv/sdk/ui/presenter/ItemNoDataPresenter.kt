package com.fangtang.tv.sdk.ui.presenter

import android.view.ViewGroup
import android.widget.TextView
import com.fangtang.tv.support.item2.host.ItemHostView
import com.fangtang.tv.support.item2.presenter.BaseItemPresenter
import com.fangtang.tv.support.item2.widget.LazyWidgetsHolder
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.extensions.setIntrinsicBoundsDrawable
import com.fangtang.tv.sdk.ui.view.CommonLayoutHostView


class ItemNoDataPresenter : BaseItemPresenter() {

    override fun onCreateHostView(parent: ViewGroup?): ItemHostView {
        return CommonLayoutHostView(context, R.layout.item_view_no_data, false)
    }

    override fun onRegisterWidgetBuilder(widgetsHolder: LazyWidgetsHolder) {
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val tv = viewHolder.view.findViewById<TextView>(R.id.tv_info)
        tv.setIntrinsicBoundsDrawable(top = R.drawable.pic_no_data)

    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
    }

}