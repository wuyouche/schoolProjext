package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.SetBinding
import kotlin.reflect.KFunction1

class SetAdapter(
    private val context: Context,
    private val changePage: () -> Unit,
    private val move: () -> Unit,
    private val changeState: KFunction1<String, Unit>
) : RecyclerView.Adapter<SetAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: SetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            when (position) {
                0 -> binding.option.text = context.getString(R.string.edit)
                1 -> binding.option.text = context.getString(R.string.set)
                2 -> binding.option.text = context.getString(R.string.H)
                3 -> binding.option.text = context.getString(R.string.F)
            }
            binding.option.setOnClickListener {
                when (position) {
                    0 -> move()
                    1 -> changePage()
                    2 -> changeState("H")

                    3 -> changeState("F")
                }
            }
            if (position == 1) {
                binding.line.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = SetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(position)
    }
}