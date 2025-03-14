package com.bandesha.convocards.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bandesha.convocards.R
import com.bandesha.convocards.databinding.CategoryAdapterLayputBinding
import com.bandesha.convocards.models.Category
import com.bumptech.glide.Glide

class CategoryAdapter(
    private val context: Context,
    private val list: List<Category>,
    private val onCategoryClick: OnCategoryClicked
) : RecyclerView.Adapter<CategoryAdapter.Holder>(){
    class Holder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val  binding = CategoryAdapterLayputBinding.bind(itemView)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.category_adapter_layput,parent,false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
//        Glide.with(context).load(item.categoryIcon).into(holder.binding.categoryIcon)
        holder.binding.categoryTitle.text = item.categoryName
        holder.binding.cardCategoryView.setOnClickListener {
            onCategoryClick.onDataReceived(item)
        }


    }

}