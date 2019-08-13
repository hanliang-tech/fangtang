package com.fangtang.tv.sdk.ui.tips

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.fangtang.tv.sdk.base.logging.logE
import com.fangtang.tv.sdk.ui.R
import com.fangtang.tv.sdk.ui.net.bean.VoiceBarEntity
import com.fangtang.tv.sdk.ui.tips.drawable.ColorFilteredStateDrawable
import com.fangtang.tv.sdk.ui.tips.util.Utils
import java.util.*

class TipGroupLayout : LinearLayout, View.OnFocusChangeListener {

    private lateinit var mTipGroup: MutableList<ArrayList<VoiceBarEntity.Tip>>
    private lateinit var mViewPool: ViewPool

    private var mWidth = 0
    private var mMargin = 0
    private var mFixWidth = 0
    private var mFirstItemMargin = 0

//    private lateinit var mTextColor: ColorStateList
    private var onClickListener: OnClickListener? = null
    private var onFocusListener: OnItemFocusChangeListener? = null

    private var mCacheTips = mutableListOf<VoiceBarEntity.Tip>()

    private val supportFocus = true

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        initView(context)
//    }

    private fun initView(context: Context) {
        mTipGroup = mutableListOf()
        mViewPool = ViewPool(this)
        orientation = LinearLayout.HORIZONTAL
        setClipChildren(false)

        // 两个tip之间的间距
        mMargin = Utils.dp2px(context, -24)
        // 没有设置Text时候的显示宽度 .9图片更改后需要重新测量
        mFixWidth = Utils.dp2px(context, 43)
        mFirstItemMargin = Utils.dp2px(context, 16)

//        mTextColor = if (!sIsInVoiceHelp) context.resources.getColorStateList(R.color.tv_selector_white) else context.resources.getColorStateList(R.color.tv_selector_blue)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        logE("onMeasure")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mWidth != measuredWidth) {
            mWidth = measuredWidth
            if (mCacheTips.isNotEmpty()) {
                showTips(mCacheTips)
            }
        }
//        logE("onMeasure width:$width measuredWidth:$measuredWidth")
    }

    //region 外部接口

    /**
     * 显示tips
     * @param tips tips集合
     */
    fun showTips(tips: List<VoiceBarEntity.Tip>) {
        logE { "tips size: ${tips.size}" }
        if (mWidth == 0) {
            //logE("width == 0")
            measure(0, 0)
            mWidth = measuredWidth
            mCacheTips.clear()
            mCacheTips.addAll(tips)
            return
        }

        // 随机
//        tips.shuffle()

        // 分组
        generateGroup(tips)

        // 显示
        renderGroup()
    }

    fun release(){
        mTipGroup.clear()
        mViewPool.release()
    }
    //endregion

    private fun generateGroup(tips: List<VoiceBarEntity.Tip>) {
        //logE("generateGroup:${tips.size}")

//        logW("分组前：" + tips)

        mTipGroup.clear()
        if(tips.isEmpty()){
            return
        }

        val tmpTextView: TextView = mViewPool.getView(0)

        var width = mWidth
        var grouping = false
        var groupIndex = -1

        run {
            var i = 0
            while (i < tips.size) {
                if(tips.size == 1 && tips[i] == null){
                    break
                }
                val margin = if (!grouping) 0 else Math.abs(mMargin)
                val tipWidth = tmpTextView.paint.measureText(tips[i].key).toInt() + margin + mFixWidth - mFirstItemMargin
                width -= tipWidth

                // 可以放下
                if (width >= 0) {
                    if (!grouping) {
                        grouping = true
                        mTipGroup.add(ArrayList(0))
                        groupIndex++
                    }
                    mTipGroup[groupIndex].add(tips[i])
                } else {
                    grouping = false
                    width = mWidth
                    i--
                }
                i++
            }
        }
        mViewPool.release(0, tmpTextView)
    }

    private fun renderGroup() {

        // 清空显示
        if (mTipGroup.isEmpty()) {
            for (index in 0 until childCount) {
                val view = getChildAt(index)
                if (view != null) {
                    mViewPool.release(0, view)
                }
            }
            removeAllViews()
            return
        }

        // 显示Tips
        val displayGroup = mTipGroup[0]
        //logE("renderGroup:${displayGroup.size}")
//        logW("分组后：" + displayGroup)
        val difCount = childCount - displayGroup.size
        // 已经显示的个数 小于 新的个数 需要增加View
        if (difCount < 0) {
            val needSize = Math.abs(difCount)
            for (index in 0 until needSize) {
                val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                params.leftMargin = if (childCount == 0) 0 else mMargin
                addView(mViewPool.getView(0), params)
            }
        }
        // 已经显示的个数 大于 新的个数 需要删除View
        else if (difCount > 0) {
            for (index in 0 until difCount) {
                val lastChild = getChildAt(childCount - 1)
                removeView(lastChild)
                mViewPool.release(0, lastChild)
            }
        }

        // 渲染Tips
        for (index in 0 until displayGroup.size) {
            val bean = displayGroup[index]
            val tv = getChildAt(index) as TextView
            tv.setTag(R.id.id_tip_index, index)
            tv.setTag(R.id.id_tip_bean, bean)
            tv.isFocusable = supportFocus
            if(supportFocus){
                touchListener(tv)
            }

            val textColor = if(bean.type == 1)  context.resources.getColorStateList(R.color.tv_selector_yellow) else  context.resources.getColorStateList(R.color.tv_selector_white)
            val bgColor = if(bean.type == 1)  R.drawable.bg_selector_yellow else R.drawable.bg_selector_white

            tv.text = bean.key
            tv.setTextColor(textColor)
//            tv.backgroundDrawable = generateColorFilterStateDrawable()
            tv.setBackgroundResource(bgColor)
        }
    }

    private fun generateColorFilterStateDrawable(): StateListDrawable {

        val drawables = arrayOf(
                resources.getDrawable(R.drawable.pic_tip_border_white),
                resources.getDrawable(R.drawable.pic_tip_focus_white)
        )

        val states = arrayOf(
                intArrayOf(
                        -android.R.attr.state_focused
                ),
                intArrayOf(
                        android.R.attr.state_focused
                )
        )
        val colors = intArrayOf(
                Color.parseColor("#ffeb6a"),
                Color.parseColor("#ffeb6a")
        )

        return ColorFilteredStateDrawable(drawables, states, colors)
    }

    private fun touchListener(view: View) {
        view.setOnClickListener(onClickListener)
        if (onFocusListener == null) {
            view.setOnFocusChangeListener(null)
        } else {
            view.setOnFocusChangeListener(this@TipGroupLayout)
        }
    }

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        onFocusListener?.let {
            val index = v.getTag(R.id.id_tip_index) as Int
            val bean = v.getTag(R.id.id_tip_bean) as VoiceBarEntity.Tip
            it.onFocusChanged(v, hasFocus, index, bean)
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        onClickListener = listener
    }

    fun setItemFocusChangeListener(listener: OnItemFocusChangeListener?) {
        onFocusListener = listener
    }

}

interface OnItemFocusChangeListener {
    fun onFocusChanged(view: View, hasFocus: Boolean, index: Int, tip: VoiceBarEntity.Tip)
}

//继承TextView 作为焦点拦截的判断依据
class TipTextView(context: Context, attrs: AttributeSet?) : TextView(context, attrs)

/**
 * ViewGroup中TextView的缓存
 */
class ViewPool {

    private var mParent: ViewGroup
    private var cache: SparseArray<Stack<View>>

    constructor(parent: ViewGroup) {
        mParent = parent
        cache = SparseArray(0)
    }

    fun <T : View> getView(type: Int): T {
        var viewStack: Stack<View>? = cache.get(type)
        if (viewStack == null) {
            viewStack = Stack()
            cache.put(type, viewStack)
        }
        return if (viewStack.isEmpty()) {
            LayoutInflater.from(mParent.context)
                    .inflate(R.layout.item_common_bordertext, mParent, false) as T
        } else {
            viewStack.pop() as T
        }
    }

    fun release() {
        cache.clear()
    }

    fun release(type: Int, view: View) {
        val viewStack = cache.get(type)
        viewStack.push(view)
    }
}