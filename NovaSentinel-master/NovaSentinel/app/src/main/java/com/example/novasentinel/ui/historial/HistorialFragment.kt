package com.example.novasentinel.ui.historial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.novasentinel.databinding.FragmentHistorialBinding

class HistorialFragment : Fragment() {

    private var _binding: FragmentHistorialBinding? = null
    private val binding get() = _binding!!

    private lateinit var historialViewModel: HistorialViewModel
    private lateinit var historialAdapter: HistorialAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistorialBinding.inflate(inflater, container, false)

        // Configura el RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        historialAdapter = HistorialAdapter(emptyList())
        binding.recyclerView.adapter = historialAdapter

        // Inicializa el ViewModel
        historialViewModel = ViewModelProvider(this).get(HistorialViewModel::class.java)

        // Observa los datos de alertas y actualiza el adaptador
        historialViewModel.alerts.observe(viewLifecycleOwner) { alertas ->
            if (alertas.isNotEmpty()) {
                historialAdapter.updateData(alertas)
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
            } else {
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

