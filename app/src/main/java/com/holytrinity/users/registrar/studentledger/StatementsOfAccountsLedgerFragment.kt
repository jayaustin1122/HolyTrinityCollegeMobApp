package com.holytrinity.users.registrar.studentledger
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SoaService
import com.holytrinity.databinding.FragmentStatementsOfAccountsLedgerBinding
import com.holytrinity.model.Soa
import com.holytrinity.users.registrar.adapter.SoaAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatementsOfAccountsLedgerFragment : Fragment() {
    private lateinit var binding: FragmentStatementsOfAccountsLedgerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatementsOfAccountsLedgerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch and display data
        fetchAllSoa()
    }

    private fun fetchAllSoa(studentId: String? = null) {
        val service = RetrofitInstance.create(SoaService::class.java)
        service.getAllSoa(studentId).enqueue(object : Callback<List<Soa>> {
            override fun onResponse(call: Call<List<Soa>>, response: Response<List<Soa>>) {
                if (response.isSuccessful && response.body() != null) {
                    val soaList = response.body()!!
                    setupRecyclerView(soaList)
                } else {
                    Log.e("Error", "Failed to fetch SOA: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Soa>>, t: Throwable) {
                Log.e("Error", "Failed to fetch SOA", t)
            }
        })
    }

    private fun setupRecyclerView(soaList: List<Soa>) {
        val adapter = SoaAdapter(soaList)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }
}
