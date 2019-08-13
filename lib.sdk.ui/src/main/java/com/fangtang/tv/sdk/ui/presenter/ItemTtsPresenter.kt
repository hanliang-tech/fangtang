package com.fangtang.tv.sdk.ui.presenter

import android.graphics.Bitmap
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.TextView
import com.fangtang.tv.support.item2.host.ItemHostView
import com.fangtang.tv.support.item2.presenter.BaseItemPresenter
import com.fangtang.tv.support.item2.widget.LazyWidgetsHolder
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.utils.VoiceStatus
import com.fangtang.tv.sdk.ui.view.CommonLayoutHostView

class ItemTtsPresenter : BaseItemPresenter() {

    private var qrBitmap: Bitmap? = null

    override fun onCreateHostView(parent: ViewGroup?): ItemHostView {
        return CommonLayoutHostView(context, R.layout.item_view_tts, false)
    }

    override fun onRegisterWidgetBuilder(widgetsHolder: LazyWidgetsHolder) {
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        if(TextUtils.isEmpty(VoiceStatus.query) || TextUtils.isEmpty(VoiceStatus.display)) return
        viewHolder.view.findViewById<TextView>(R.id.tv_query).text = "“${VoiceStatus.query}”"
        viewHolder.view.findViewById<TextView>(R.id.tv_tts).text = "“${VoiceStatus.display}”"
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        qrBitmap?.recycle()
    }

}