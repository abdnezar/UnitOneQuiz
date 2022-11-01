package com.abdnezar.unitonequiz.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abdnezar.unitonequiz.data.models.City
import com.abdnezar.unitonequiz.databinding.CityItemBinding
import com.bumptech.glide.Glide

class CitiesAdapter(val context: Context, val click: OnClick) : RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {
    private var list = ArrayList<City>()

    // For View Binding
    class ViewHolder(val binding: CityItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CityItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city = list[position]

        Glide.with(context).load(city.imageURL).into(holder.binding.ivCity)
        holder.binding.tvCityName.text = city.name

        holder.binding.root.setOnClickListener {
            click.onClickCity(city)
        }
    }

    fun setData(data: ArrayList<City>) {
        list = data
        notifyDataSetChanged()
    }

    interface OnClick {
        fun onClickCity(city: City)
    }
}