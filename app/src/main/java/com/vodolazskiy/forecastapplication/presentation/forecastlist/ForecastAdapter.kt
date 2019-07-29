package com.vodolazskiy.forecastapplication.presentation.forecastlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vodolazskiy.forecastapplication.R
import com.vodolazskiy.forecastapplication.domain.entity.ForecastItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_forecast.view.*
import java.text.SimpleDateFormat

class ForecastAdapter : ListAdapter<ForecastItem, ForecastViewHolder>(
    ForecastDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder =
        ForecastViewHolder.create(parent)

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

    override val containerView: View? = itemView

    fun bind(item: ForecastItem) {
        itemView.txtDateValue.text = SimpleDateFormat.getDateTimeInstance().format(item.date)
        itemView.txtTemperatureValue.text = item.mainForecast.temp.toInt().toString()
    }

    companion object {

        fun create(parent: ViewGroup): ForecastViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ForecastViewHolder(
                inflater.inflate(
                    R.layout.item_forecast,
                    parent,
                    false
                )
            )
        }
    }
}

private class ForecastDiffCallback : DiffUtil.ItemCallback<ForecastItem>() {
    override fun areItemsTheSame(
        oldItem: ForecastItem,
        newItem: ForecastItem
    ): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(
        oldItem: ForecastItem,
        newItem: ForecastItem
    ): Boolean {
        return oldItem == newItem
    }

}
