package com.bandesha.convocards.adapters
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bandesha.convocards.R
import com.bandesha.convocards.databinding.ConvoCardsAdapterLayoutBinding
import com.bandesha.convocards.models.Cards
import com.bumptech.glide.Glide
import kotlin.math.log

class CardAdapter(
    private val context: Context,
    private val list: List<Cards>,
) : RecyclerView.Adapter<CardAdapter.Holder>() {
    private val TAG = "CardAdapter"
    class Holder (itemView:View):RecyclerView.ViewHolder(itemView){
        val  binding = ConvoCardsAdapterLayoutBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.convo_cards_adapter_layout,parent,false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]

        Log.d(TAG, "onBindViewHolder: "+item.gifUrl)

        Glide.with(context)
//            .asGif()
            .load(item.gifUrl)
            .into(holder.binding.imageView)

    }


}