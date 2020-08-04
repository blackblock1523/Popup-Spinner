package com.blackblock.popupSpinner.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.blackblock.popupSpinner.R
import com.blackblock.popupSpinner.adapter.PopupWindowAdapter
import com.blackblock.popupSpinner.util.DistinctUtil
import com.blackblock.popupSpinner.util.ISimpleFormat
import java.util.*

class PopupWindowSpinner : AppCompatTextView {
    private val MAX_LEVEL = 10000

    private var popupWindow: PopupWindow? = null //下拉框
    private var mRecyclerView: RecyclerView? = null
    private val itemList: MutableList<String> = mutableListOf()
    private val valueList: MutableList<String> = mutableListOf()

    private lateinit var selectItem: String
    private lateinit var selectValue: String
    private var tempItemList: MutableList<String> = mutableListOf()
    private var tempValueList: MutableList<String> = mutableListOf()

    private var position = -1//位置
    private var mAdapter: PopupWindowAdapter? = null

    private var isHideSelectItem: Boolean = false// 列表中是否隐藏所选项
    private var textColorResId = 0 // 文字颜色
    private var backgroundSelector = 0 // 列表selector
    private var isArrowHidden = false // 是否右侧箭头
    private var arrowDrawableTint = 0 // 填充右侧箭头颜色
    private var arrowDrawableResId = 0 // 右侧箭头资源Id
    private var arrowDrawable: Drawable? = null // 右侧箭头图片
    private var arrowAnimator: ObjectAnimator? = null // 箭头动画效果

    private var onItemSelectedListener: OnItemSelectedListener? = null

    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener?) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(
            context,
            attrs
    ) {
        init(context, attrs)
    }

    constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {

        val typedArray: TypedArray =
                context!!.obtainStyledAttributes(attrs, R.styleable.PopupWindowSpinner)

        gravity = Gravity.CENTER_VERTICAL or Gravity.START

        val defaultPadding = 8
        setPadding(
                12,
                defaultPadding,
                defaultPadding,
                defaultPadding
        )

        isHideSelectItem = typedArray.getBoolean(
                R.styleable.PopupWindowSpinner_isHideSelectItem,
                false
        )

        backgroundSelector = typedArray.getResourceId(
                R.styleable.PopupWindowSpinner_backgroundSelector,
                R.drawable.popup_window_selector
        )

        setBackgroundResource(backgroundSelector)

        textColorResId = typedArray.getColor(
                R.styleable.PopupWindowSpinner_textTint,
                getDefaultTextColor(context)
        )
        setTextColor(textColorResId)

        isArrowHidden = typedArray.getBoolean(
                R.styleable.PopupWindowSpinner_hideArrow,
                false
        )

        arrowDrawableTint = typedArray.getColor(
                R.styleable.PopupWindowSpinner_arrowTint,
                resources.getColor(android.R.color.black)
        )
        arrowDrawableResId =
                typedArray.getResourceId(
                        R.styleable.PopupWindowSpinner_arrowDrawable,
                        R.drawable.arrow_down
                )

        initArrowDrawable()

        mAdapter = PopupWindowAdapter(itemList, R.layout.item_menu_popup_window)

        setOnClickListener {
            if (popupWindow != null && popupWindow!!.isShowing) {
                popupWindow?.dismiss()
            } else {
                if (mRecyclerView == null) {
                    val viewList = LayoutInflater.from(context)
                            .inflate(R.layout.popup_window_menulist, null)
                    mRecyclerView = viewList.findViewById(R.id.popup_window_recycler)

                    mAdapter?.setOnClickListener(OnClickListener(fun(v: View?) {
                        val pos = v?.tag as Int
                        setSelection(pos)
                        if (popupWindow != null && popupWindow!!.isShowing) {
                            popupWindow?.dismiss()
                        }
                    }))

                    mRecyclerView?.adapter = mAdapter


                }

                if (popupWindow == null) {
                    popupWindow = PopupWindow(
                            mRecyclerView,
                            width,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    popupWindow?.animationStyle = R.style.Popup_Animation // 设置动画效果
                    popupWindow?.update()
                    popupWindow?.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
                    popupWindow?.isTouchable = true
                    popupWindow?.isOutsideTouchable = true // 点击popupWindow外面消失
                    popupWindow?.isFocusable = true // 物理键是否响应，为true时，点返回键触发dismiss
                    popupWindow?.setTouchInterceptor(OnTouchListener { v1, event ->
                        if (event.action == MotionEvent.ACTION_OUTSIDE) {
                            popupWindow?.dismiss()
                            return@OnTouchListener true
                        }
                        false
                    })

                    popupWindow?.setOnDismissListener {
                        if (!isArrowHidden && arrowDrawable != null) {
                            animateArrow(false)
                        }
                    }
                }

                showDropDown()
            }
        }

        val entries = typedArray.getTextArray(R.styleable.PopupWindowSpinner_entries)
        if (entries != null) {
            setData(entries)
        }

        typedArray.recycle()
    }

    private fun showDropDown() {
        if (!isArrowHidden && arrowDrawable != null) {
            animateArrow(true)
        }

        popupWindow?.showAsDropDown(this, 0, 0)
    }

    /**
     * 初始化数据，使用数组
     */
    fun setData(
            items: Array<*>
    ) {
        setData(items, items)
    }

    /**
     * 初始化数据，使用数组
     */
    fun setData(
            items: Array<*>,
            values: Array<*>
    ) {
        itemList.clear()
        itemList += arrayListOf(*items) as MutableList<String>

        valueList.clear()
        valueList += arrayListOf(*values) as MutableList<String>
    }

    /**
     * 初始化数据，使用集合
     */
    fun setData(
            items: List<String?>?
    ) {
        setData(items, items)
    }

    /**
     * 初始化数据，使用集合
     */
    fun setData(
            items: List<String?>?,
            values: List<String?>?
    ) {
        itemList.clear()
        itemList.addAll(items)
        valueList.clear()
        valueList.addAll(values)
    }

    fun setData(pList: MutableList<Any>, simpleFormatItem: ISimpleFormat, simpleFormatValue: ISimpleFormat) {
        itemList.addAll(simpleFormatItem.format(pList))
        valueList.addAll(simpleFormatValue.format(pList))
    }

    /**
     * 设置选中第几项
     *
     *  @param position 下标
     *  @param needCallBack 是否需要触发回调 需要先实现回调接口
     */
    fun setSelection(position: Int, needCallBack: Boolean = true) {

        if (isHideSelectItem) {
            if (tempItemList.size == 0) {
                tempItemList = DistinctUtil.Distinct(itemList)
                tempValueList = DistinctUtil.Distinct(valueList)

                selectItem = tempItemList[position]
                selectValue = tempValueList[position]
            } else {
                selectItem = tempItemList[position]
                selectValue = tempValueList[position]
                tempItemList = DistinctUtil.Distinct(itemList)
                tempValueList = DistinctUtil.Distinct(valueList)
            }
            tempItemList.remove(selectItem)
            tempValueList.remove(selectValue)

            mAdapter?.updateDataSource(tempItemList)
        } else {
            selectItem = itemList[position]
            selectValue = valueList[position]
        }

        if (needCallBack) {
            if (onItemSelectedListener != null) {
                onItemSelectedListener?.onItemSelected(this, position)
            }
        }

        if (position == -1) {
            text = selectItem
            this.position = 0
        } else {
            text = selectItem
            this.position = position
        }
    }

    fun getSelectItem(): String {
        return this.selectItem
    }

    fun getSelectValue(): String {
        return this.selectValue
    }

    private fun getDefaultTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme
                .resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        val typedArray = context.obtainStyledAttributes(
                typedValue.data,
                intArrayOf(android.R.attr.textColorPrimary)
        )
        val defaultTextColor = typedArray.getColor(0, Color.BLACK)
        typedArray.recycle()
        return defaultTextColor
    }

    fun setArrowTintColor(resolvedColor: Int) {
        if (arrowDrawable != null && !isArrowHidden) {
            arrowDrawableTint = resolvedColor
            DrawableCompat.setTint(
                    arrowDrawable!!,
                    ContextCompat.getColor(context, arrowDrawableTint)
            )
        }
    }

    fun setArrowDrawable(@DrawableRes @ColorRes drawableId: Int) {
        arrowDrawableResId = drawableId
        arrowDrawable = initArrowDrawable()
        setArrowDrawableOrHide(arrowDrawable)
    }

    fun setArrowDrawable(drawable: Drawable) {
        arrowDrawable = drawable
        setArrowDrawableOrHide(arrowDrawable)
    }

    private fun initArrowDrawable(): Drawable? {
        if (isArrowHidden || arrowDrawableResId == 0) return null
        val arrow = RotateDrawable()
        arrow.pivotX = 0.5F
        arrow.pivotY = 0.5F
        arrow.fromDegrees = 0F
        arrow.toDegrees = 180F
        arrow.drawable = ContextCompat.getDrawable(context, arrowDrawableResId)
        if (arrow.drawable != null) {
            // Gets a copy of this drawable as this is going to be mutated by the animator
            arrow.drawable = DrawableCompat.wrap(arrow.drawable!!).mutate()
            if (arrowDrawableResId != Int.MAX_VALUE && arrowDrawableResId != 0) {
                DrawableCompat.setTint(arrow.drawable!!, arrowDrawableResId)
            }
        }
        return arrow
    }

    private fun setArrowDrawableOrHide(drawable: Drawable?) {
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun animateArrow(shouldRotateUp: Boolean) {
        val start = if (shouldRotateUp) 0 else MAX_LEVEL
        val end = if (shouldRotateUp) MAX_LEVEL else 0
        arrowAnimator = ObjectAnimator.ofInt(arrowDrawable, "level", start, end)
        arrowAnimator?.interpolator = LinearOutSlowInInterpolator()
        arrowAnimator?.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            onVisibilityChanged(this, visibility)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (popupWindow != null && popupWindow!!.isShowing) popupWindow?.dismiss()
        arrowAnimator?.cancel()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        arrowDrawable = initArrowDrawable()
        setArrowDrawableOrHide(arrowDrawable)
    }

    interface OnItemSelectedListener {
        fun onItemSelected(sp: PopupWindowSpinner?, position: Int)
    }

}

private fun <E> MutableList<E>.addAll(sources: List<E?>?): Boolean {
    if (sources == null) return false

    sources.map { e: E? -> e?.let { this.add(it) } }
    return this.size == sources.size
}
