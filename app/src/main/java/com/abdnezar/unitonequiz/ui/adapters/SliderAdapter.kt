package com.abdnezar.unitonequiz.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.abdnezar.unitonequiz.R
import com.abdnezar.unitonequiz.data.models.Slider
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(private var context: Context?) : SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {
    private var mSliderItems: ArrayList<Slider> = ArrayList()

    class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        var rootView: View
        var imageViewBackground: ImageView

        init {
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider)
            rootView = itemView.rootView
        }
    }

    fun setData(sliderItems: ArrayList<Slider>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem = mSliderItems[position]

        Glide.with(viewHolder.itemView)
            .load(sliderItem.imageURL)
            .fitCenter()
            .into(viewHolder.imageViewBackground)

        viewHolder.itemView.setOnClickListener { v ->
            Toast.makeText(context, "This is item in position $position ${sliderItem.imageURL}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getCount(): Int {
        return mSliderItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(context).inflate(R.layout.slider_item, parent, false)
        return SliderAdapterVH(inflate)
    }
}