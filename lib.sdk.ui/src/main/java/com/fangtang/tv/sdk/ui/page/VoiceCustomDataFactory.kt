package com.fangtang.tv.sdk.ui.page

import com.fangtang.tv.waterfall.ComponentModel
import com.fangtang.tv.waterfall.GridDataFactory
import com.fangtang.tv.waterfall.SectionModel
import com.fangtang.tv.waterfall.Waterfall

class VoiceCustomDataFactory(waterfall: Waterfall.IPageInterface, spanCount: Int, rowSpace: Int, private val flag:String? = null) : GridDataFactory(waterfall, spanCount, rowSpace) {

    // 第一次加载需要特殊处理
    // 显示登录二维码
    // 显示分组信息
    private var isFirstLoad = true

    override fun buildOrderedSections(sourceList: MutableList<out Item>?, trim: Boolean, sectionIndex: Int): MutableList<SectionModel<ComponentModel<*>>> {
        val buildOrderedSections = super.buildOrderedSections(sourceList, trim, sectionIndex)

//        logE("buildOrderedSections, flag:$flag")
//        if (isFirstLoad && flag == Constant.TabInfo.SPECIAL_FLAG_RECOMMEND) {
//            isFirstLoad = false
//
//            // 添加分组显示
//            buildOrderedSections[0].marginTop = 60
//            buildOrderedSections[0].title = "猜你喜欢"
//            buildOrderedSections[0].titleVerticalSpacing = 32
//        }

//        if (DebugStatus.isDebug()) {
//            buildOrderedSections.forEachIndexed { iS, sectionModel ->
//                sectionModel.components.forEachIndexed { _, componentModel ->
//                    componentModel.items?.forEachIndexed { iI, itemModel ->
//                        logI("组：$iS \t 坐标：$iI ： ${(itemModel as WaterfallModel).name}")
//                    }
//                }
//            }
//        }

        return buildOrderedSections
    }

}