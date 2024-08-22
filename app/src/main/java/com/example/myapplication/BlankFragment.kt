package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.myapplication.databinding.FragmentBlankBinding

class BlankFragment : Fragment() {
    private lateinit var binding: FragmentBlankBinding
    private lateinit var citycardAdapter: Citycard
    private var cityName: MutableList<String> = mutableListOf()
    private var lowTemp: MutableList<String> = mutableListOf()
    private var highTemp: MutableList<String> = mutableListOf()
    private var nowTemp: MutableList<String> = mutableListOf()
    private var nowCould: MutableList<String> = mutableListOf()
    private var itemTouchHelper: ItemTouchHelper? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()

        citycardAdapter = Citycard(
            requireContext(), cityName, lowTemp, highTemp, nowTemp, nowCould, ::clearSearchBox, 0
        )
        binding.cityList.layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
        binding.cityList.adapter = citycardAdapter
        binding.allOption.layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
        binding.allOption.adapter =
            SetAdapter(
                requireContext(),
                move = ::move,
                changePage = ::changePage,
                changeState = ::changeTempState
            )


        binding.searchCity.addTextChangedListener { text ->
            if (citycardAdapter.deOrAd() == 1) {
                Toast.makeText(requireContext(), "close", Toast.LENGTH_SHORT).show()
            } else {
                citycardAdapter.filter(text.toString())
            }

        }
        binding.menuSet.setOnClickListener {
            binding.card2.visibility = View.VISIBLE
            binding.finsh.visibility = View.VISIBLE
            binding.menuSet.visibility = View.GONE
        }
        binding.finsh.setOnClickListener {
            binding.card2.visibility = View.GONE
            getData()
            citycardAdapter = Citycard(
                requireContext(),
                cityName,
                lowTemp,
                highTemp,
                nowTemp,
                nowCould,
                ::clearSearchBox,
                0
            )
            binding.cityList.layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
            binding.cityList.adapter = citycardAdapter
            binding.menuSet.visibility = View.VISIBLE
            binding.finsh.visibility = View.GONE
            itemTouchHelper?.attachToRecyclerView(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        citycardAdapter.stopUpdating()
    }

    fun clearSearchBox() {
        binding.searchCity.text?.clear()
        binding.searchCity.clearFocus()
    }

    private fun move() {
        getData()
        citycardAdapter = Citycard(
            requireContext(), cityName, lowTemp, highTemp, nowTemp, nowCould, ::clearSearchBox, 1
        )

        binding.cityList.layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
        binding.cityList.adapter = citycardAdapter
        itemTouchHelper = ItemTouchHelper(DragManageAdapter(citycardAdapter))
        itemTouchHelper!!.attachToRecyclerView(binding.cityList)
    }

    private fun changePage() {
        parentFragmentManager.beginTransaction().setCustomAnimations(
            android.R.anim.fade_in,
            android.R.anim.fade_out,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
            .replace(R.id.fragment_container, Set())
            .addToBackStack(null)
            .commit()
    }


    private fun changeTempState(state: String) {
        getData()
        val sh = requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)

        with(sh.edit()) {
            putString("temp", state)
            apply()
        }
        Log.d("hi", "hi")

        citycardAdapter = Citycard(
            requireContext(), cityName, lowTemp, highTemp, nowTemp, nowCould, ::clearSearchBox, 0
        )
        binding.cityList.layoutManager = LinearLayoutManager(requireContext(), VERTICAL, false)
        binding.cityList.adapter = citycardAdapter
    }

    private fun getData() {
        val sharedPrefs =
            requireContext().getSharedPreferences("cityData", Context.MODE_PRIVATE)
        cityName =
            sharedPrefs.getString("cityName", "")?.split(",")?.toMutableList() ?: mutableListOf()
        lowTemp =
            sharedPrefs.getString("lowTemp", "")?.split(",")?.toMutableList() ?: mutableListOf()
        highTemp =
            sharedPrefs.getString("highTemp", "")?.split(",")?.toMutableList() ?: mutableListOf()
        nowTemp =
            sharedPrefs.getString("nowTemp", "")?.split(",")?.toMutableList() ?: mutableListOf()
        nowCould =
            sharedPrefs.getString("nowCould", "")?.split(",")?.toMutableList() ?: mutableListOf()

        Log.d("jigj", nowTemp.toString())

    }
}
