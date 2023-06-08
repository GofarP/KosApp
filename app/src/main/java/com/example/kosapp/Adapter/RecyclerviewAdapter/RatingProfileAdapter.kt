package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.RatingProfile
import com.example.kosapp.databinding.LayoutRatingProfileBinding

class RatingProfileAdapter(private val ratingProfileArrayList:ArrayList<RatingProfile>):
    RecyclerView.Adapter<RatingProfileAdapter.ViewHolder>() {

    class ViewHolder(layoutRatingProfileBinding: LayoutRatingProfileBinding)
        :RecyclerView.ViewHolder(layoutRatingProfileBinding.root) {
            private val binding=layoutRatingProfileBinding

            fun bind(ratingProfile: RatingProfile)
            {
                itemView.apply {
                    binding.lblnamakos.text=ratingProfile.namaKos
                    binding.lblbrating.text=ratingProfile.ratingPengguna
                    binding.lbltanggal.text=ratingProfile.tanggal
                }


            }

        }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RatingProfileAdapter.ViewHolder {
       val binding=LayoutRatingProfileBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingProfileAdapter.ViewHolder, position: Int) {
        holder.bind(ratingProfileArrayList[position])
    }

    override fun getItemCount(): Int {
        return ratingProfileArrayList.size
    }


}