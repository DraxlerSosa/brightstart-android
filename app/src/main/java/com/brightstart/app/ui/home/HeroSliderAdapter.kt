package com.brightstart.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brightstart.app.databinding.ItemHeroSlideBinding

data class HeroSlide(val imageRes: Int, val title: String, val subtitle: String)

class HeroSliderAdapter(private val slides: List<HeroSlide>) :
    RecyclerView.Adapter<HeroSliderAdapter.VH>() {

    inner class VH(val b: ItemHeroSlideBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemHeroSlideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )
    override fun getItemCount() = slides.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        val s = slides[position]
        holder.b.slideImage.setImageResource(s.imageRes)
        holder.b.slideTitle.text = s.title
        holder.b.slideSubtitle.text = s.subtitle
    }
}
