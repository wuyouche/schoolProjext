package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentSetBinding


class Set : Fragment() {
    private lateinit var binding: FragmentSetBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sh = requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedRadioButton = binding.radioGroup.findViewById<RadioButton>(checkedId)
            val index = binding.radioGroup.indexOfChild(selectedRadioButton)
            if (index == 1) {
                with(sh.edit()) {
                    putString("lan", "en")
                    apply()
                }
            } else {
                with(sh.edit()) {
                    putString("lan", "zh")
                    apply()
                }
            }

        }
        binding.ok.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
        if (sh.getString("lan", "zh") == "zh") {
            binding.radioButton1.isChecked = true
        } else {
            binding.radioButton2.isChecked = true
        }

    }

}