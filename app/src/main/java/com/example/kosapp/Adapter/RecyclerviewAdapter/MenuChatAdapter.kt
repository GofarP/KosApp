package com.example.kosapp.Adapter.RecyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kosapp.Model.MenuChat
import com.example.kosapp.databinding.LayoutMenuChatBinding
import com.google.firebase.storage.FirebaseStorage

class MenuChatAdapter(private val arrayListMenuChat:ArrayList<MenuChat>,
                      private val emailPengguna:String,
                      private val  itemOnClick: ItemOnClickMenuChat )

    : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private val VIEW_TYPE_PENGIRIM=1
    private val VIEW_TYPE_PENERIMA=2

    class MenuReceiverViewHolder(layoutMenuChatBinding: LayoutMenuChatBinding)
        :RecyclerView.ViewHolder(layoutMenuChatBinding.root)
    {
            val binding=layoutMenuChatBinding
            val storage=FirebaseStorage.getInstance().reference

            fun bindPengirim(menuChat: MenuChat, itemAdapterCallback: ItemOnClickMenuChat)
            {
                itemView.apply {
                    binding.lblnama.text=menuChat.usernamePenerima
                    binding.lblpesan.text=menuChat.pesanTerakhir

                    storage.child(menuChat.fotoPenerima).downloadUrl.addOnSuccessListener { uri->
                        Glide.with(this)
                            .load(uri)
                            .into(binding.ivprofile)
                    }

                    itemView.setOnClickListener {
                        itemAdapterCallback.onClick(this, menuChat)
                    }
                }
            }

        fun bindPenerima(menuChat: MenuChat, itemOnClick: ItemOnClickMenuChat)
        {
            itemView.apply {
                binding.lblnama.text=menuChat.usernamePengirim
                binding.lblpesan.text=menuChat.pesanTerakhir

                storage.child(menuChat.fotoPengirim).downloadUrl.addOnSuccessListener {uri->
                    Glide.with(this)
                        .load(uri)
                        .into(binding.ivprofile)
                }

                itemView.setOnClickListener {
                    itemOnClick.onClick(this, menuChat)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       val binding=LayoutMenuChatBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return MenuReceiverViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position))
        {
            VIEW_TYPE_PENGIRIM->{
                (holder as MenuReceiverViewHolder).bindPengirim(arrayListMenuChat[position],itemOnClick)
            }

            VIEW_TYPE_PENERIMA->{
                (holder as MenuReceiverViewHolder).bindPenerima(arrayListMenuChat[position], itemOnClick)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        var viewType=0

        val menuChatData=arrayListMenuChat[position]

        if(menuChatData.emailPengirim==emailPengguna)
        {
            viewType=VIEW_TYPE_PENGIRIM
        }

        else
        {
            viewType=VIEW_TYPE_PENERIMA
        }

        return viewType

    }

    override fun getItemCount(): Int {
        return arrayListMenuChat.size
    }

    interface ItemOnClickMenuChat{
        fun onClick(v: View, menuChat: MenuChat)
    }
}