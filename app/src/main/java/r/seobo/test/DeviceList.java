package r.seobo.test;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;



import static r.seobo.test.Constants.*;

public class DeviceList extends AppCompatActivity {

    ListView devicelist;
    BluetoothAdapter myBluetooth;
    private String mConnectedDeviceName;
    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

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
            String deviceName = info.substring(0,info.length() - 17);
            // Make an intent to start next activity.
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice remote = myBluetooth.getRemoteDevice(address);

            if (remote != null) {
                // send address and start bluetooth service
                Intent i = new Intent(DeviceList.this, NaviActivity.class);
                i.putExtra("deviceName",deviceName);
                i.putExtra("address",address);
                i.setAction(Intent.ACTION_SEND);
                startActivity(i);
            }
            else{
                Intent j = new Intent(DeviceList.this, StartMenu.class);
                j.putExtra("missedRemoteInfo",info);
                startActivity(j);
            }



        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        Activity activity = this;
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        Activity activity = this;
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the Bluetooth Service*/
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Activity activity = DeviceList.this;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case STATE_LISTEN:
                        case STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


}
