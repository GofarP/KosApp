package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Helper.Constant
import com.example.kosapp.Model.Chat
import com.example.kosapp.databinding.LayoutChatReceiverImageBinding
import com.example.kosapp.databinding.LayoutChatReceiverTextBinding
import com.example.kosapp.databinding.LayoutChatSenderImageBinding
import com.example.kosapp.databinding.LayoutChatSenderTextBinding
import com.google.firebase.storage.FirebaseStorage

class ChatAdapter(private val arrayListChat:ArrayList<Chat>, private val emailSaatIni:String)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
        private val VIEW_TYPE_SENT=1
        private val VIEW_TYPE_RECEIVE=2

        private val VIEW_TYPE_SENT_IMAGE=3
        private val VIEW_TYPE_RECEIVE_IMAGE=4


        class SendMessageViewHolder(layoutChatSenderTextBinding: LayoutChatSenderTextBinding)
            : RecyclerView.ViewHolder(layoutChatSenderTextBinding.root){

                private val binding=layoutChatSenderTextBinding


                fun bind(chat: Chat)
                {
                    itemView.apply {
                        binding.textMessage.text=chat.pesan
                        binding.textDateTime.text=chat.tanggal
                    }
                }

            }

    class ReceiveMessageViewHolder(layoutChatReceiverTextBinding: LayoutChatReceiverTextBinding)
        :RecyclerView.ViewHolder(layoutChatReceiverTextBinding.root)
    {
            private val binding=layoutChatReceiverTextBinding

            fun bind(chat:Chat)
            {
                itemView.apply {
                    binding.textMessage.text=chat.pesan
                    binding.textDateTime.text=chat.tanggal
                }
            }
    }


    class SendImageViewHolder(layoutChatSenderImageBinding: LayoutChatSenderImageBinding)
        :RecyclerView.ViewHolder(layoutChatSenderImageBinding.root)
    {
            private val binding=layoutChatSenderImageBinding

            private val storage=FirebaseStorage.getInstance().reference

        fun bind(chat: Chat)
            {
                itemView.apply {

                    binding.textDateTime.text=chat.tanggal

                    storage.child(chat.pesan)
                        .downloadUrl.addOnSuccessListener {uri->
                            Glide.with(this)
                                .load(uri)
                                .into(binding.imageChatSent)
                        }
                        .addOnFailureListener {
                            Log.d("image failed",it.message.toString())
                        }

                }
            }

        }

    class ReceiveImageViewHolder(layoutChatReceiverImageBinding: LayoutChatReceiverImageBinding)
        :RecyclerView.ViewHolder(layoutChatReceiverImageBinding.root)
    {
        private val binding=layoutChatReceiverImageBinding

        private val storage=FirebaseStorage.getInstance().reference

        fun bind(chat: Chat)
        {
            itemView.apply {

                binding.textDateTime.text=chat.tanggal

                storage.child(chat.pesan)
                    .downloadUrl.addOnSuccessListener {uri->
                        Glide.with(this)
                            .load(uri)
                            .into(binding.imageChatSent)
                    }
                    .addOnFailureListener {
                        Log.d("image failed",it.message.toString())
                    }

            }
        }

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

       return when(viewType)
        {

            VIEW_TYPE_SENT->{
                val binding=LayoutChatSenderTextBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                SendMessageViewHolder(binding)
            }

            VIEW_TYPE_RECEIVE->{
                val binding=LayoutChatReceiverTextBinding.inflate(LayoutInflater.from(parent.context),parent, false)
                ReceiveMessageViewHolder(binding)
            }

            VIEW_TYPE_SENT_IMAGE->{
                val binding=LayoutChatSenderImageBinding.inflate(LayoutInflater.from(parent.context),parent, false)
                SendImageViewHolder(binding)
            }

           else->{
               //VIEW_TYPE_RECEIVE_IMAGE
               val binding=LayoutChatReceiverImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
               ReceiveImageViewHolder(binding)
           }
       }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position))
        {
            VIEW_TYPE_SENT->{
                (holder as SendMessageViewHolder).bind(arrayListChat[position])
                holder
            }

            VIEW_TYPE_RECEIVE->{
                (holder as ReceiveMessageViewHolder).bind(arrayListChat[position])
            }

            VIEW_TYPE_SENT_IMAGE->{
                (holder as SendImageViewHolder).bind(arrayListChat[position])
            }

            VIEW_TYPE_RECEIVE_IMAGE->{
                (holder as ReceiveImageViewHolder).bind(arrayListChat[position])
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        var viewType=0

        val chatData=arrayListChat[position]

        if(chatData.emailPengirim==emailSaatIni && chatData.tipe==Constant().KEY_TEXT)
        {
            viewType=VIEW_TYPE_SENT
        }

        else if(chatData.emailPengirim!=emailSaatIni && chatData.tipe==Constant().KEY_TEXT)
        {
            viewType=VIEW_TYPE_RECEIVE
        }

        else if (chatData.emailPengirim==emailSaatIni && chatData.tipe==Constant().KEY_IMAGE)
        {
            viewType=VIEW_TYPE_SENT_IMAGE
        }

        else if(chatData.emailPengirim!=emailSaatIni && chatData.tipe==Constant().KEY_IMAGE)
        {
            viewType=VIEW_TYPE_RECEIVE_IMAGE
        }


        return viewType
    }

    override fun getItemCount(): Int {
        return arrayListChat.size
    }

}