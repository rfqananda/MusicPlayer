package com.example.features.mainscreen.ui.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.uicomponent.R.color
import com.example.features.databinding.ItemListMusicBinding
import com.example.features.mainscreen.ui.model.TrackUi
import com.example.uicomponent.extension.visibleIf

class ListMusicAdapter : RecyclerView.Adapter<ListMusicAdapter.ViewHolder>() {

    private var items = listOf<TrackUi>()
    private var onClickListener: ((Int) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun submitData(newItems: List<TrackUi>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemListMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TrackUi, position: Int) {
            with(binding) {
                val color = if(item.isSelected) color.blueSecondary else color.transparent

                containerItemListMusic.setBackgroundColor(
                    ContextCompat.getColor(root.context, color)
                )

                songImage.load(item.songImage) {
                    crossfade(true)
                    transformations(RoundedCornersTransformation(40f))
                }

                indicator.visibleIf(item.isSelected)
                root.setOnClickListener {
                    onClickListener?.invoke(position)
                }
            }

            fun setupMarquee(textView: TextView, text: String) {
                textView.text = text
                if (item.isSelected) {
                    textView.ellipsize = TextUtils.TruncateAt.MARQUEE
                    textView.isSelected = true
                    textView.marqueeRepeatLimit = -1
                } else {
                    textView.ellipsize = TextUtils.TruncateAt.END
                    textView.isSelected = false
                }
            }

            setupMarquee(binding.songName, item.songName)
            setupMarquee(binding.artist, item.artist)
            setupMarquee(binding.album, item.album)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListMusicBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}
