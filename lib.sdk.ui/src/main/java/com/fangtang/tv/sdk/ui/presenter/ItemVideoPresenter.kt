package com.fangtang.tv.sdk.ui.presenter

import android.os.Build
import android.text.TextUtils
import com.fangtang.tv.support.item2.host.ItemHostView
import com.fangtang.tv.support.item2.presenter.CommonItemPresenter
import com.fangtang.tv.support.item2.presenter.SimpleItemPresenter
import com.fangtang.tv.support.item2.utils.DimensUtil
import com.fangtang.tv.support.item2.widget.*
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.model.WaterfallModel
import com.fangtang.tv.sdk.ui.widget.CustomTagWidget
import com.fangtang.tv.sdk.ui.widget.IOriginWidget
import com.fangtang.tv.sdk.ui.widget.OriginWidget

class ItemVideoPresenter : CommonItemPresenter() {

    private var haveOriginIcon = false



    override fun onCreateShadowBuilder(holder: LazyWidgetsHolder?): BuilderWidget.Builder<out BuilderWidget<*>> {
        return if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            ShadowWidget.builder(context).setDefaultActive(false).setFocusActive(true)
        }else{
            super.onCreateShadowBuilder(holder)
        }
    }

    override fun onRegisterWidgetBuilder(holder: LazyWidgetsHolder) {
        super.onRegisterWidgetBuilder(holder)
        holder.registerLazyWidget(IOriginWidget.NAME, OriginWidget.Builder(context)
                .setWidth(DimensUtil.dp2Px(143F)).setHeight(DimensUtil.dp2Px(209F))
                .setZOrder(Z_ORDER_MISC))

        val customTagWidget = CustomTagWidget.Builder(context)
        customTagWidget.setZOrder(SimpleItemPresenter.Z_ORDER_MISC + 999)
        holder.registerLazyWidget(ITagWidget.NAME, customTagWidget)
    }

    override fun onWidgetInitialized(widget: BuilderWidget<out BuilderWidget.Builder<*>>?, lwh: LazyWidgetsHolder?) {
        super.onWidgetInitialized(widget, lwh)
        when (widget) {
            is BottomTitleWidget -> {
                val textColorNormal = context.resources.getColor(R.color.bottom_title)
                widget.setTextColor(textColorNormal)
                widget.builder.setTextColorDefault(textColorNormal)
            }
            is OriginWidget -> {
                widget.setVisible(false, false)
            }

            is FocusBorderWidget ->{
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        widget.setBorderVisible(false)
                    }
            }
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        viewHolder as LazyWidgetsHolder
        item as WaterfallModel

        viewHolder.lazyWorker.cancelAllWork()
        super.onBindViewHolder(viewHolder, item)

        val widget = viewHolder.getWidget<OriginWidget>(IOriginWidget.NAME)
        haveOriginIcon = if (!TextUtils.isEmpty(item.originName) && !TextUtils.isEmpty(item.originIconUrl)) {
            widget.setApkName(item.originName)
            widget.setApkUrl(item.originIconUrl)
            true
        } else {
            widget.setVisible(false, false)
            widget.clearBitmap()
            false
        }


    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        super.onUnbindViewHolder(viewHolder)
    }

    override fun onExecuteTask(holder: LazyWidgetsHolder?, item: Any?, taskID: Int) {
        super.onExecuteTask(holder, item, taskID)
        if(taskID == SimpleItemPresenter.TASK_MISC) {
            item as WaterfallModel
            val tag = holder?.getWidget<TagWidget>(ITagWidget.NAME)
            tag?.let {
                if (item.iconType == TagWidget.CORNER_NONE) {
                    it.setTagImgVisible(false)
                    it.isTextBGVisible = true
                } else {
                    it.setTagImgVisible(true)
                    it.isTextBGVisible = false
                }
            }
        }
    }

    override fun onHostViewFocusChanged(hostView: ItemHostView?, hasFocus: Boolean, widgetsHolder: LazyWidgetsHolder) {
        super.onHostViewFocusChanged(hostView, hasFocus, widgetsHolder)
        widgetsHolder.getWidget<OriginWidget>(IOriginWidget.NAME)?.let {
            if (haveOriginIcon) {
                it.setVisible(hasFocus, false)
            }
        }
    }

}