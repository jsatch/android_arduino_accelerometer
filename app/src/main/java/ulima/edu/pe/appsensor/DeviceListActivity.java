package ulima.edu.pe.appsensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ulima.edu.pe.appsensor.model.Device;

/**
 * Created by hernan on 2/15/18.
 */

public class DeviceListActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener{
    ListView lviDeviceList;
    Button butRefrescar;

    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_devicelist);

        butRefrescar = findViewById(R.id.butRefrescar);
        lviDeviceList = findViewById(R.id.lviDeviceList);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "Bluetooth no disponible", Toast.LENGTH_LONG).show();
            finish();
        }else if (!mBluetoothAdapter.isEnabled()){
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        butRefrescar.setOnClickListener(this);
        lviDeviceList.setOnItemClickListener(this);

    }


    @Override
    public void onClick(View view) {
        Set<BluetoothDevice> devicesPaired = mBluetoothAdapter.getBondedDevices();
        List<Device> devices = new ArrayList();
        if (devicesPaired.size() == 0){
            Toast.makeText(this, "No hay dispositivos", Toast.LENGTH_LONG).show();
        }else{
            for (BluetoothDevice bd : devicesPaired){
                devices.add(new Device(bd.getName(), bd.getAddress()));
            }
        }
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, devices);
        lviDeviceList.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Device deviceSelected = (Device)adapterView.getItemAtPosition(i);
        Log.i("APPSENSORES",  deviceSelected.toString());
        Intent intent = new Intent(DeviceListActivity.this, GraphActivity.class);
        intent.putExtra("DEVICE_ADDRESS", deviceSelected.getAddress());
        startActivity(intent);
    }
}