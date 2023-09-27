package com.example.iot_project

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView

class MainActivity : ComponentActivity() {

    private lateinit var mBluetoothAdapter : BluetoothAdapter
    private lateinit var buttonEnableBT : Button
    private var buttonEnableBTstate: Boolean = false


    private lateinit var buttonDiscoverBTDevices : Button

    private lateinit var titleBT2Devices : TextView
    private lateinit var listViewBT2Devices : ListView

    private lateinit var titleBoundedDevices : TextView
    private lateinit var listViewBoundedDevices : ListView

    private lateinit var titleBLEDevices : TextView
    private lateinit var listViewBLEDevices : ListView

    private var listBondedString: ArrayList<String>? = null
    private var listBondedAdapter: ArrayAdapter<String>? = null
    private var listBondedDevices: ArrayList<BluetoothDevice>? = null

    private var listBT2String: ArrayList<String>? = null
    private var listBT2Adapter: ArrayAdapter<String>? = null
    private var listBT2Devices: ArrayList<BluetoothDevice>? = null

    private var listBLEString: ArrayList<String>? = null
    private var listBLEAdapter: ArrayAdapter<String>? = null
    private var listBLEDevices: ArrayList<BluetoothDevice>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // CM 1
        initBluetoothManager()
        createButtonEnableBT()

        // CM 2
        initVarListDevice()
        createButtonDiscoverDevices()

        val mbr = MonBroadcastReceiver()

        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mbr, intentFilter)
    }

    private fun createButtonDiscoverDevices(){
        buttonDiscoverBTDevices = findViewById(R.id.buttonDiscoverBTDevices)

        buttonDiscoverBTDevices.setOnClickListener {
            if(startDiscovery()) {
                buttonDiscoverBTDevices.isEnabled = false
            }
        }
    }

    private fun initVarListDevice() {

        titleBoundedDevices = findViewById(R.id.titleBoundedDevices)
        listViewBoundedDevices = findViewById(R.id.listViewBoundedDevices)

        titleBT2Devices = findViewById(R.id.titleBT2Devices)
        listViewBT2Devices = findViewById(R.id.listViewBT2Devices)

        titleBLEDevices = findViewById(R.id.titleBLEDevices)
        listViewBLEDevices = findViewById(R.id.listViewBLEDevices)

        listBondedString = ArrayList<String>()
        listBondedAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            listBondedString!!
        )
        listViewBoundedDevices.adapter = listBondedAdapter

        listBT2String = ArrayList<String>()
        listBT2Adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            listBT2String!!
        )

        listViewBT2Devices.adapter = listBT2Adapter
        listBLEString = ArrayList<String>()
        listBLEAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            listBLEString!!
        )
        listViewBLEDevices.adapter = listBLEAdapter
    }

    internal inner class MonBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                Toast.makeText(context, "Changement de statut bluetooth", Toast.LENGTH_SHORT).show()
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
                Toast.makeText(context, "Discovery started", Toast.LENGTH_LONG).show()
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                Toast.makeText(context, "Recherche terminée", Toast.LENGTH_LONG).show()
            }
            if (BluetoothDevice.ACTION_FOUND == action) {
                Toast.makeText(context, "Action Found", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun startDiscovery(): Boolean {
        if (mBluetoothAdapter == null) { // Pas de Bluetooth
            Toast.makeText(this, "Pas de Bluetooth", Toast.LENGTH_LONG).show()
            return false
        }
        return if (!mBluetoothAdapter.isEnabled) { // Bluetooth désactivé
            Toast.makeText(
                this,
                "Vous devez activer votre Bluetooth pour effectuer une recherche",
                Toast.LENGTH_LONG
            ).show()
            false
        } else { // Bluetooth activé
            listBondedDevices?.clear()
            listBT2Devices?.clear()
            listBLEDevices?.clear()
            listBondedString?.clear()
            listBT2String?.clear()
            listBLEString?.clear()

            when {
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                )  == PackageManager.PERMISSION_GRANTED -> {
                    mBluetoothAdapter.startDiscovery()
                } else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.BLUETOOTH_SCAN
                    )
                }
            }

            Toast.makeText(this, "Discovery Started", Toast.LENGTH_LONG).show()
            true
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

    // Fonction d'enregistrement de l'activité
    private var activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )
        { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                Toast.makeText(this,"RESULT_OK", Toast.LENGTH_SHORT).show()
                if (buttonEnableBTstate) {
                    buttonEnableBT.text = "Activer le bluetooth"
                    buttonEnableBTstate = false;
                } else {
                    buttonEnableBT.text = "Désactiver le Bluetooth"
                    buttonEnableBTstate = true;
                }
            }
            else if (result.resultCode == RESULT_CANCELED) {
                Toast.makeText(this,"RESULT_CANCELED", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this,"RESULT_PROBLEM", Toast.LENGTH_SHORT).show()
            }
        }

    // Fonction d'enregistrement de permission
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        )
        { isGranted: Boolean ->
            if(isGranted) {
                //Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(this, "NOT OK", Toast.LENGTH_SHORT).show()
            }
        }

    private fun createButtonEnableBT(): Button {
        buttonEnableBT = findViewById(R.id.buttonEnableBT)

        if (mBluetoothAdapter == null) {
            buttonEnableBT.isEnabled
        } else if (mBluetoothAdapter.isEnabled) {
            buttonEnableBT.text = "Désactiver le Bluetooth"
            buttonEnableBTstate = true
        }

        buttonEnableBT.setOnClickListener() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                when {
                    ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        if(buttonEnableBTstate) {
                            activityResultLauncher.launch(
                                Intent("android.bluetooth.adapter.action.REQUEST_DISABLE")
                            )
                        } else {
                            activityResultLauncher.launch(
                                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            )
                        }
                    }
                    (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) -> {
                        showRationaleDialog(
                            getString(R.string.rationale_title),
                            getString(R.string.rationale_desc),
                            android.Manifest.permission.BLUETOOTH_CONNECT,
                            "REQUEST_PERMISSION"
                        )
                    }
                    else -> {
                        requestPermissionLauncher.launch(
                            Manifest.permission.BLUETOOTH_CONNECT
                        )
                    }
                }
            }
            else {
                if (buttonEnableBTstate) {
                    buttonEnableBT.text = "Activer le bluetooth"
                    buttonEnableBTstate = false;
                } else {
                    buttonEnableBT.text = "Désactiver le Bluetooth"
                    buttonEnableBTstate = true;
                }
            }
        }

        return buttonEnableBT
    }

    private fun initBluetoothManager() {
        var mBluetoothManager =
            this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = mBluetoothManager.adapter
    }
}