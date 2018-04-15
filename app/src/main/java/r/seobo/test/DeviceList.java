package r.seobo.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DeviceList extends AppCompatActivity {

    ListView devicelist;
    BluetoothAdapter myBluetooth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        devicelist = (ListView)findViewById(R.id.listView);

        getPairedDevice();
    }

    public void getPairedDevice(){
        ArrayList<BluetoothDevice> pairedDevices;
        Bundle b = this.getIntent().getExtras();
        if (b != null) {

            Serializable k = b.getSerializable("Arr");
            pairedDevices = (ArrayList<BluetoothDevice>) k; // check this, paireddevice coming back null

            ArrayList list = new ArrayList();

            if (pairedDevices.size()>0)
            {
                for(BluetoothDevice bt : pairedDevices)
                {
                    list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the remoteBTAddress
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
            }

            final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list); // TODO: double check layout
            devicelist.setAdapter(adapter);
            devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
        }
        else{
            Toast.makeText(getApplicationContext(), "Could not Communicate with Start Menu.", Toast.LENGTH_LONG).show();
        }
    }


    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC remoteBTAddress, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice remote = myBluetooth.getRemoteDevice(address);

            if (remote != null) {
                Intent i = new Intent(DeviceList.this, BluetoothConnectionService.class);
                i.putExtra("info",info);
                i.putExtra("address",address);
                i.setAction(Intent.ACTION_SEND);
                startService(i);
            }
            else{
                Intent j = new Intent(DeviceList.this, StartMenu.class);
                j.putExtra("missedRemoteInfo",info);
                startActivity(j);
            }



        }
    };


}
