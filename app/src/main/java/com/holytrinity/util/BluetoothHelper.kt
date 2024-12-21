package com.holytrinity.util

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

private const val REQUEST_ENABLE_BT = 1

class BluetoothHelper(private val activity: Activity, private val context: Context) {

    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private val REQUEST_BT_PERMISSIONS = 1

    fun checkAndRequestBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_BT_PERMISSIONS)
            }
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.BLUETOOTH), REQUEST_BT_PERMISSIONS)
        }
    }

    fun toggleBluetooth() {
        if (bluetoothAdapter?.isEnabled == true) {
            turnOffBluetooth()  // Turn off Bluetooth if it is enabled
        } else {
            turnOnBluetooth()  // Turn on Bluetooth if it is disabled
        }
    }

    private fun turnOnBluetooth() {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            Toast.makeText(context, "Turning Bluetooth ON", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Bluetooth is already ON", Toast.LENGTH_SHORT).show()
        }
    }

    private fun turnOffBluetooth() {
        if (bluetoothAdapter?.isEnabled == true) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            bluetoothAdapter?.disable()
            Toast.makeText(context, "Turning Bluetooth OFF", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Bluetooth is already OFF", Toast.LENGTH_SHORT).show()
        }
    }

    fun connectPrinter(): Boolean {
        val device = findBluetoothDevice() ?: return false
        val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        return try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()
            outputStream = bluetoothSocket?.outputStream
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun findBluetoothDevice(): BluetoothDevice? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            if (device.name == "PT210_777D") {
                return device
            }
        }
        return null
    }

    private val cmdPrintStart = byteArrayOf(0x10.toByte(), 0xFF.toByte(), 0xFE.toByte(), 0x01.toByte())
    private val cmdPrintEnd = byteArrayOf(0x1B.toByte(), 0x4A.toByte(), 0x40.toByte(), 0x10.toByte(), 0xFF.toByte(), 0xFE.toByte(), 0x45.toByte())
    private val cmdSetPrintInfo = byteArrayOf(0x1D.toByte(), 0x76.toByte(), 0x30.toByte(), 0x00.toByte(), 0x30.toByte(), 0x00.toByte())

    suspend fun printTextFeed(message: String): Boolean {
        try {
            val messageBytes = message.toByteArray(Charsets.UTF_8)

            withContext(Dispatchers.IO) {
                outputStream?.write(cmdPrintStart)
                outputStream?.write(messageBytes)
                outputStream?.write(cmdPrintEnd)
                outputStream?.flush()
            }

            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    fun disconnectPrinter() {
        try {
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == REQUEST_BT_PERMISSIONS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Bluetooth permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
