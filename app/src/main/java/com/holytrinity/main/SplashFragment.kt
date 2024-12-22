package com.holytrinity.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.databinding.FragmentSplashBinding
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val SPLASH_DELAY: Long = 2000
    private lateinit var connectivityManager: ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var noInternetDialog: SweetAlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        checkInternetAndNavigate()
    }

    private fun checkInternetAndNavigate() {
        // Launch a coroutine tied to the view lifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            if (isInternetAvailable()) {
                // Internet is available, proceed with splash delay
                delay(SPLASH_DELAY)
                navigateToNextScreen()
            } else {
                // No internet, show dialog and start listening for connectivity changes
                showNoInternetDialog()
                registerNetworkCallback()
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun navigateToNextScreen() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            val roleId = sharedPreferences.getInt("role_id", -1)
            when (roleId) {
                1 -> findNavController().navigate(R.id.adminDrawerFragment)
                2 -> findNavController().navigate(R.id.registrarDrawerHolderFragment)
//                3 -> findNavController().navigate(R.id.accountingHomeFragment)
                4 -> findNavController().navigate(R.id.cashierDrawerFragment)
                5 -> findNavController().navigate(R.id.instructorDrawerHolderFragment)
                6 -> findNavController().navigate(R.id.parentDrawerHolderFragment)
                7 -> findNavController().navigate(R.id.studentDrawerHolderFragment)
                10 -> findNavController().navigate(R.id.benefactorDrawerHolderFragment)
                else -> findNavController().navigate(R.id.signInFragment)
            }
        } else {
            findNavController().navigate(R.id.signInFragment)
        }
    }

    private fun showNoInternetDialog() {
        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("No Internet Connection")
            .setContentText("Please check your internet connection and try again.")
            .setConfirmText("Exit")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                requireActivity().finishAffinity()
            }
            .show()
    }

    private fun registerNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    viewLifecycleOwner.lifecycleScope.launch {
                        noInternetDialog?.dismissWithAnimation()
                        delay(500)
                        navigateToNextScreen()
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(networkCallback!!)
        }
    }

    private fun unregisterNetworkCallback() {
        if (::connectivityManager.isInitialized) {
            networkCallback?.let {
                connectivityManager.unregisterNetworkCallback(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        unregisterNetworkCallback()
        noInternetDialog = null
    }
}
