package com.example.iot_project

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.iot_project.buttons.ButtonEnableBT
import com.example.iot_project.buttons.ButtonSearchDevice
import com.example.iot_project.utils.ListDevice


class MainActivity : ComponentActivity() {

    private lateinit var mBluetoothAdapter : BluetoothAdapter
    private lateinit var buttonEnableBT : ButtonEnableBT
    private lateinit var buttonDiscoverBTDevices : ButtonSearchDevice
    private lateinit var mbr: MonBroadcastReceiver
    private lateinit var listDevice: ListDevice


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        mBluetoothAdapter = (this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        buttonEnableBT = ButtonEnableBT(this, mBluetoothAdapter)

        listDevice = ListDevice(this)
        buttonDiscoverBTDevices = ButtonSearchDevice(this, mBluetoothAdapter, listDevice)

        val mbr = MonBroadcastReceiver()

        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mbr, intentFilter)
    }

    internal inner class MonBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                Toast.makeText(context, "ACTION_STATE_CHANGED", Toast.LENGTH_SHORT).show()
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
                Toast.makeText(context, "ACTION_DISCOVERY_STARTED", Toast.LENGTH_SHORT).show()
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                }

                // Affichage des bonded devices
                val pairedDevices: Set<BluetoothDevice> = mBluetoothAdapter.bondedDevices
                for (pairedDevice in pairedDevices) {
                    var bonding: String = ""
                    if(pairedDevice.bondState == 11) bonding = "Connecté"
                    listDevice.listBondedString?.add(pairedDevice.name + " " + pairedDevice.address + " " + bonding)
                    listDevice.listBondedAdapter?.notifyDataSetChanged()
                }
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                Toast.makeText(context, "ACTION_DISCOVERY_FINISHED", Toast.LENGTH_SHORT).show()
                //buttonDiscoverBTDevices.isEnabled=true

                buttonDiscoverBTDevices.scanLeDevice()
            }
            if (BluetoothDevice.ACTION_FOUND == action) {
                //Toast.makeText(context, "ACTION_FOUND", Toast.LENGTH_LONG).show()
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE,BluetoothDevice::class.java)
                } else {
                    @Suppress("DEPRECATION") intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                }
                if (device != null) {
                    var deviceInfos: String = ""
                    if(device.name !=null) deviceInfos += device.name + " "
                    deviceInfos += device.address + " BT2 "
                    if(device.bondState == 11) deviceInfos += "Connecté "
                    if(!listDevice.listBT2BLEString!!.contains(deviceInfos)){
                        listDevice.listBT2BLEString?.add(deviceInfos)
                        listDevice.listBT2BLEAdapter?.notifyDataSetChanged()

                        listDevice.listBT2BLEDevices?.add(device)
                    }
                }
            }
        }
    }



    private fun showRationaleDialog(
        title: String, message: String, permission: String, requestCode: String
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok", { dialog, which -> })
        builder.create().show()
    }

    // Fonction d'enregistrement de permission
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        )
        { isGranted: Boolean ->
            if(isGranted) {
                Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "NOT OK", Toast.LENGTH_SHORT).show()
            }
        }
}