package com.example.iot_project.buttons

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.iot_project.MainActivity
import com.example.iot_project.R

class ButtonEnableBT (context: MainActivity?, bluetoothAdapter: BluetoothAdapter) {

    private var state : Boolean = false

    private var button : Button

    init {
        button = context!!.findViewById(R.id.buttonEnableBT)

        if (bluetoothAdapter == null) {
            button.isEnabled
        } else if (bluetoothAdapter.isEnabled) {
            button.text = "Désactiver le Bluetooth"
            state = true
        }

        button.setOnClickListener() {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                when {
                    ActivityCompat.checkSelfPermission(
                        context!!.applicationContext,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        if(state) {
                            activityResultLauncher?.launch(
                                Intent("android.bluetooth.adapter.action.REQUEST_DISABLE")
                            )
                        } else {
                            activityResultLauncher?.launch(
                                Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            )
                        }
                    }
                    /*(context.shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) -> {
                        showRationaleDialog(
                            context.getString(R.string.rationale_title),
                            context.getString(R.string.rationale_desc),
                            android.Manifest.permission.BLUETOOTH_CONNECT,
                            "REQUEST_PERMISSION"
                        )*/
                    //}
                    else -> {
                        /*requestPermissionLauncher.launch(
                            Manifest.permission.BLUETOOTH_CONNECT
                        )*/
                    }
                }
            }
            else {
                if (state) {
                    activityResultLauncher?.launch(Intent("android.bluetooth.adapter.action.REQUEST_DISABLE"))
                } else {
                    activityResultLauncher?.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                }
            }
        }
    }

    // Fonction d'enregistrement de l'activité du bouton
    private var activityResultLauncher =
        context?.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        )
        { result: ActivityResult ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                Toast.makeText(context,"RESULT_OK", Toast.LENGTH_SHORT).show()
                if (state) {
                    button.text = "Activer le bluetooth"
                    state = false;
                } else {
                    button.text = "Désactiver le Bluetooth"
                    state = true;
                }
            } else if (result.resultCode == ComponentActivity.RESULT_CANCELED) {
                Toast.makeText(context,"RESULT_CANCELED", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context,"RESULT_PROBLEM", Toast.LENGTH_SHORT).show()
            }
        }
}