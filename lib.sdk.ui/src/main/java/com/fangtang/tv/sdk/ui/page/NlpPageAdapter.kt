package com.fangtang.tv.sdk.ui.page

import android.text.TextUtils
import com.fangtang.tv.waterfall.GridDataHelper
import com.fangtang.tv.sdk.FangTang
import com.fangtang.tv.sdk.FangTangSDK
import com.fangtang.tv.sdk.base.logging.logE
import com.fangtang.tv.sdk.base.logging.logW
import com.fangtang.tv.sdk.base.nlp.INLPManager
import com.fangtang.tv.sdk.base.nlp.bean.NLPPageBean
import com.fangtang.tv.sdk.ui.base.BaseAbstractPageAdapter
import com.fangtang.tv.sdk.ui.model.WaterfallModel
import com.fangtang.tv.sdk.ui.utils.CommonUtils
import com.fangtang.tv.sdk.ui.utils.VoiceStatus


class NlpPageAdapter : BaseAbstractPageAdapter() {

    override fun loadNextPage(pageNumber: Int, helper: GridDataHelper?) {

        logW { "影视 loadNextPage -> pageNumber:$pageNumber" }

        if (TextUtils.isEmpty(VoiceStatus.domain)) {
            logE { "没有domain" }
            mNeedLoadMore = false
            return
        }

        // 请求数据
        FangTang.getInstance().nlpManager.queryPage(pageNumber, VoiceStatus.queryId, object : INLPManager.NLPPageListener{
            override fun onSuccess(pageBean: NLPPageBean?) {
                onCallBack(pageBean, helper, pageNumber)
            }

            override fun onError(e: Exception?) {
                onCallBack(null, helper, pageNumber)
            }

        })
    }

    private fun onCallBack(entity: NLPPageBean?, helper: GridDataHelper?, pageNumber: Int) {
        if (!FangTangSDK.get().getSdkInterceptor().handlePageResult(entity)) {
            // Cover to CommonItemModel type
            val list = CommonUtils.covertData(entity?.items)
            // 接口失败 || 没有数据
            if (list.isEmpty()) {
                logW { "#### 没有数据了：list:$list" }
                mNeedLoadMore = false
//            helper?.get
                helper?.callDataLoadedInBackGroundTask(pageNumber, ArrayList<WaterfallModel>(0), false)
            } else {
                // Add to mDataList list && call render method
                helper?.callDataLoadedInBackGroundTask(pageNumber, list, true)
            }
        }
    }


    override fun hasPageSplit(): Boolean {
        return true
    }

}