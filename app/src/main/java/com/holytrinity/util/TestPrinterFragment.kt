    package com.holytrinity.util

    import android.os.Bundle
    import androidx.fragment.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Toast
    import com.holytrinity.R
    import com.holytrinity.databinding.FragmentTestPrinterBinding
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext


    class TestPrinterFragment : Fragment() {
        private lateinit var binding: FragmentTestPrinterBinding
        private lateinit var bluetoothFn: BluetoothHelper

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentTestPrinterBinding.inflate(layoutInflater)
            return binding.root
        }

        @Deprecated("Deprecated in Java")
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            bluetoothFn.onRequestPermissionsResult(requestCode, grantResults)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            bluetoothFn = BluetoothHelper(requireActivity(), this.requireContext())

            binding.btnReqPerm.setOnClickListener {
                bluetoothFn.checkAndRequestBluetoothPermission()
            }

            binding.btnOnBt.setOnClickListener {
                bluetoothFn.turnOnBluetooth()
            }

            binding.btnConnPrinter.setOnClickListener {
                val isConnected = bluetoothFn.connectPrinter()
                if (isConnected) {
                    Toast.makeText(requireContext(), "Connected to Printer", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Connection Failed", Toast.LENGTH_SHORT).show()
                }
            }

            val message = "Test Printer"

            binding.btnPrint.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val isPrinted = bluetoothFn.printTextFeed(message)
                    withContext(Dispatchers.Main) {
                        if (isPrinted) {
                            Toast.makeText(requireContext(), "Printed Successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Printing Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            binding.btnDisconnect.setOnClickListener {
                bluetoothFn.disconnectPrinter()
                Toast.makeText(requireContext(), "Disconnected from Printer", Toast.LENGTH_SHORT).show()
            }
        }
    }
