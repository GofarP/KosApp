package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kosapp.Model.Comment
import com.example.kosapp.databinding.LayoutCommentBinding

class CommentAdapter(private val commentArrayList: ArrayList<Comment>)
    :RecyclerView.Adapter<CommentAdapter.ViewHolder>() {


        class ViewHolder(layoutCommentBinding: LayoutCommentBinding)
            :RecyclerView.ViewHolder(layoutCommentBinding.root)
        {

                val bind=layoutCommentBinding

                fun bind(comment: Comment)
                {
                    bind.lblnamapengguna.text=comment.namaPengguna
                    bind.lblcomment.text=comment.comment
                    bind.lbltanggal.text=comment.tanggal.toString()
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