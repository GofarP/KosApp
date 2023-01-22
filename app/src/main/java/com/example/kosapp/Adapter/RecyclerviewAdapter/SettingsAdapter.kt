package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.databinding.LayoutSettingsBinding

class SettingsAdapter(private val listSettings:ArrayList<String>, private val itemOnClick: ItemOnClick )
    :RecyclerView.Adapter<SettingsAdapter.ViewHolder>()
{

    class ViewHolder(layoutSettingsBinding: LayoutSettingsBinding)
        :RecyclerView.ViewHolder(layoutSettingsBinding.root)
    {
        private val binding=layoutSettingsBinding

        fun bind(settingsName:String, itemOnClick: ItemOnClick)
        {
            itemView.apply {

                binding.lblsettings.text=settingsName

                itemView.setOnClickListener { view->
                    itemOnClick.onClick(view,settingsName)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=LayoutSettingsBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listSettings[position], itemOnClick)
    }

    override fun getItemCount(): Int {
        return listSettings.size
    }

    interface ItemOnClick
    {
        fun onClick(view:View, settingsName: String)
    }

}