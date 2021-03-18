package com.yoyo.recordapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yoyo.recordapp.bean.Word

class ListAdapter(
    private var context: Context,
    private var list: MutableList<Word>,
    clickListener: ClickListener
) :
    RecyclerView.Adapter<ListViewHolder>() {

    private var clickListener: ClickListener? = null

    private var layoutInflater: LayoutInflater? = null

    init {
        layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        var view = layoutInflater?.inflate(R.layout.item_word, parent, false)!!

        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var word = list[position]
        holder.tvNum!!.setText(word.id.toString())
        holder.tvName!!.setText(word.name.toString())
        holder.tvExample!!.setText(word.example.toString())

        holder.itemView.setOnLongClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.menuInflater.inflate(R.menu.menu_main, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.action_delete ->{
                        clickListener?.deleteClick(word)
                        false
                    }
                    else -> {
                        clickListener?.updateClick(word)
                        false
                    }
                }
            }
            false
        }
    }

}

interface ClickListener {
    fun deleteClick(word:Word)
    fun updateClick(word:Word)
}

class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var tvNum: TextView? = null
    var tvName: TextView? = null
    var tvExample: TextView? = null
//    var updateTv: TextView? = null
//    var deleteTv: TextView? = null

    init {
        tvNum = view.findViewById(R.id.tvNum)
        tvName = view.findViewById(R.id.tvName)
        tvExample = view.findViewById(R.id.tvExample)
//        updateTv = view.findViewById(R.id.tvUpdate)
//        deleteTv = view.findViewById(R.id.tvDelete)
    }

}