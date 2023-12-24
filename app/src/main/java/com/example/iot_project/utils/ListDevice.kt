package com.example.iot_project.utils

import android.bluetooth.BluetoothDevice
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.ComponentActivity
import com.example.iot_project.R

class ListDevice (context: ComponentActivity){

    private var titleBoundedDevices : TextView
    private var listViewBoundedDevices : ListView
    var listBondedString: ArrayList<String>? = null
    var listBondedAdapter: ArrayAdapter<String>? = null
    var listBondedDevices: ArrayList<BluetoothDevice>? = null

    private var titleBT2BLEDevices : TextView
    private var listViewBT2BLEDevices : ListView
    var listBT2BLEString: ArrayList<String>? = null
    var listBT2BLEAdapter: ArrayAdapter<String>? = null
    var listBT2BLEDevices: ArrayList<BluetoothDevice>? = null

    init {
        titleBoundedDevices = context.findViewById(R.id.titleBoundedDevices)
        listViewBoundedDevices = context.findViewById(R.id.listViewBoundedDevices)

        titleBT2BLEDevices = context.findViewById(R.id.titleBT2BLEDevices)
        listViewBT2BLEDevices = context.findViewById(R.id.listViewBT2BLEDevices)

        listBondedString = ArrayList<String>()
        listBondedAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1,
            listBondedString!!
        )
        listViewBoundedDevices.adapter = listBondedAdapter

        listBT2BLEString = ArrayList<String>()
        listBT2BLEAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1,
            listBT2BLEString!!
        )
        listViewBT2BLEDevices.adapter = listBT2BLEAdapter
    }

    fun clear() {
        listBondedDevices?.clear()
        listBT2BLEDevices?.clear()
        listBondedString?.clear()
        listBT2BLEString?.clear()

        listBondedAdapter?.notifyDataSetChanged()
        listBT2BLEAdapter?.notifyDataSetChanged()
    }
}