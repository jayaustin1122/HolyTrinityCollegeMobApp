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
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        checkInternetAndNavigate()
    }

    private fun checkInternetAndNavigate() {
        // Launch a coroutine tied to the view lifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            if (isInternetAvailable()) {
                // Internet is available, proceed with splash delay
                delay(SPLASH_DELAY)
                navigateToSignIn()
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

    private fun navigateToSignIn() {
        findNavController().navigate(R.id.signInFragment)
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
                        navigateToSignIn()
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    // Internet connection lost while the dialog is showing
                    // Optionally, you can show the dialog again or handle it as needed
                }
            }

            connectivityManager.registerDefaultNetworkCallback(networkCallback!!)
        } else {
            // For API levels below N, use deprecated methods or consider alternative approaches
            // One approach is to use a BroadcastReceiver, but it's deprecated in newer APIs
            // Here, we'll skip implementing it for brevity
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
