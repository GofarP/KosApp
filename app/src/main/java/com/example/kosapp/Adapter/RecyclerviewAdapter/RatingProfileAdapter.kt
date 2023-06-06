package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.Kos
import com.example.kosapp.Model.Rating
import com.example.kosapp.databinding.LayoutRatingProfileBinding

class RatingProfileAdapter(private val ratingArrayList:ArrayList<Rating>):
    RecyclerView.Adapter<RatingProfileAdapter.ViewHolder>() {

    class ViewHolder(layoutRatingProfileBinding: LayoutRatingProfileBinding)
        :RecyclerView.ViewHolder(layoutRatingProfileBinding.root) {
            private val binding=layoutRatingProfileBinding

            fun bind(rating: Rating)
            {
                itemView.apply {
                    binding.lblnamakos.text=rating.namaKos
                    binding.lblbrating.text=rating.rating
                    binding.lbltanggal.text=rating.tanggal
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
        holder.bind(ratingArrayList[position])
    }

    override fun getItemCount(): Int {
        return ratingArrayList.size
    }


}