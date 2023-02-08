package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Model.Comment
import com.example.kosapp.databinding.LayoutCommentBinding
import com.google.firebase.storage.FirebaseStorage

class CommentAdapter(private val commentArrayList: ArrayList<Comment>)
    :RecyclerView.Adapter<CommentAdapter.ViewHolder>() {


        class ViewHolder(layoutCommentBinding: LayoutCommentBinding)
            :RecyclerView.ViewHolder(layoutCommentBinding.root)
        {

                val bind=layoutCommentBinding
                val storage=FirebaseStorage.getInstance()

            fun bind(comment: Comment)
                {

                    itemView.apply {

                        bind.lblnamapengguna.text=comment.username
                        bind.lblcomment.text=comment.isiComment
                        bind.lbltanggal.text=comment.tanggal

                        storage.reference.child(comment.foto).downloadUrl
                            .addOnSuccessListener { uri->
                                Glide.with(this)
                                    .load(uri)
                                    .into(bind.ivprofile)
                            }

                    }

                }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind=LayoutCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(commentArrayList[position])
    }

    override fun getItemCount(): Int {
        return commentArrayList.size
    }
}