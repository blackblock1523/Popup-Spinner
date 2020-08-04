package com.blackblock.popupSpinner.util

import java.util.*

object DistinctUtil {

    // 去重 传列表即可 ps：需要在对应的实体类JavaBean重写equal和hashCode方法
    fun <T> Distinct(list: List<T>): MutableList<T> {
        val tempList: MutableList<T> = ArrayList()
        for (obj in list) {
            if (!tempList.contains(obj)) {
                tempList.add(obj)
            }
        }
        return tempList
    }
}