package com.example.iot_project.buttons

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.iot_project.MainActivity
import com.example.iot_project.R
import com.example.iot_project.utils.ListDevice

class ButtonSearchDevice (mainContext: MainActivity?, bluetoothAdapter: BluetoothAdapter, listDeviceI: ListDevice) {

    private var button : Button
    private var mBluetoothAdapter : BluetoothAdapter
    private var context: MainActivity
    private var listDevice: ListDevice

    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private var scanningBLE = false
    private val SCAN_PERIOD: Long = 10000

    init {
        button = mainContext!!.findViewById(R.id.buttonDiscoverBTDevices)
        mBluetoothAdapter = bluetoothAdapter
        context = mainContext!!
        listDevice = listDeviceI

        button.setOnClickListener {
            if(startDiscovery()) {
                button.isEnabled = false
            }
        }
    }

    private fun startDiscovery(): Boolean {
        if (mBluetoothAdapter == null) { // Pas de Bluetooth
            Toast.makeText(context, "Pas de Bluetooth", Toast.LENGTH_LONG).show()
            return false
        }
        return if (!mBluetoothAdapter.isEnabled) { // Bluetooth désactivé
            Toast.makeText(
                context,
                "Vous devez activer votre Bluetooth pour effectuer une recherche",
                Toast.LENGTH_LONG
            ).show()
            false
        } else { // Bluetooth activé
            listDevice.clear()
            bluetoothLeScanner = mBluetoothAdapter.bluetoothLeScanner

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            return mBluetoothAdapter.startDiscovery()
        }
    }

    // SCAN BLE
    fun scanLeDevice() {
        if (!scanningBLE) { // Stops scanning after a pre-defined scan period.
            Handler(Looper.getMainLooper()).postDelayed({
                scanningBLE = false
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                }
                bluetoothLeScanner.stopScan(leScanCallback)
                button.isEnabled = true
            }, SCAN_PERIOD)
            scanningBLE = true
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            scanningBLE = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    // CALLBACK SCAN BLE
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            var btdevice: BluetoothDevice = result.device
            var deviceInfos: String = ""
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
            }
            deviceInfos += btdevice.address + " BLE "
            if(btdevice.bondState == 11) deviceInfos += "Connecté "
            listDevice.listBT2BLEString?.add(deviceInfos,)
            listDevice.listBT2BLEAdapter!!.notifyDataSetChanged()
        }
    }
}