package com.blackblock.popupSpinner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blackblock.popupSpinner.R

class PopupWindowAdapter(
        private var mDatas: List<String>?,
        private val layoutId: Int
) : RecyclerView.Adapter<PopupWindowAdapter.BaseViewHolder>() {

    private var mListener: View.OnClickListener? = null

    fun setOnClickListener(listener: View.OnClickListener?) {
        mListener = listener
    }

    fun updateDataSource(datas: List<String>?) {
        mDatas = datas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(layoutId, parent, false)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

        holder.tv_item?.text = mDatas!![position]

        holder.itemView.setOnClickListener {
            if (mListener != null) {
                it.tag = position
                mListener!!.onClick(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return mDatas?.size ?: 0
    }

    open class BaseViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        var tv_item: TextView? = itemView.findViewById(R.id.tv_item)
    }

}