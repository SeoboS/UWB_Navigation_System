package r.seobo.test;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StartMenu extends AppCompatActivity {

    Button b1,b2,b3,b4;
    TextView tv, dev_conn;
    String address, info;

    private BluetoothAdapter myBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menu);

        // Example of a call to a native method
        b1 = findViewById(R.id.button);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);
        b4 = findViewById(R.id.bluetooth_off);

        tv = findViewById(R.id.sample_text);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null ||  !myBluetooth.isEnabled() ){
            b2.setVisibility(tv.INVISIBLE);
            b3.setVisibility(tv.INVISIBLE);
        }
        else if (myBluetooth.isEnabled()){
            b2.setVisibility(tv.VISIBLE);
            b3.setVisibility(tv.VISIBLE);
        }

        //tv.setText("Hey bud");
        Intent i = this.getIntent();
        if ( i != null ){
            info = i.getStringExtra("info");
            if (info != null) {
                tv.setText(info);
//                address = info.substring(info.length() - 17);;
//                if (info != null) {
//                    dev_conn = findViewById(R.id.device_conn);
//                    String disp_str = getString(R.string.bluetooth_connected) + " " + info;
//                    dev_conn.setText(disp_str);
//                    dev_conn.setVisibility(dev_conn.VISIBLE);
//                }
            }
            String remoteInfo = i.getStringExtra("missedRemoteInfo");
            if (remoteInfo != null){
                tv.setText(remoteInfo);
            }
            String error = i.getStringExtra("error");
            if (error != null){
                tv.setText(error);
            }
        }
    }

    public void setupBluetooth(View v){
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null){
            tv.setText("Device doesn't support bluetooth");
            b2.setVisibility(v.INVISIBLE);
            b3.setVisibility(v.INVISIBLE);
        }
        else if (!myBluetooth.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
            b2.setVisibility(v.VISIBLE);
            b3.setVisibility(v.VISIBLE);
        }
        else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
            b2.setVisibility(v.VISIBLE);
            b3.setVisibility(v.VISIBLE);
        }
    }

    public void listDevices(View v){
        ArrayList<BluetoothDevice> bondedDevices = new ArrayList<>(myBluetooth.getBondedDevices());
        //Set<BluetoothDevice> bondedDevices = myBluetooth.getBondedDevices();
        if(bondedDevices.isEmpty()){
            Toast.makeText(getApplicationContext(), "No Paired Devices", Toast.LENGTH_LONG).show();
        }
        else {
            Intent i = new Intent(StartMenu.this, DeviceList.class);
            Bundle b = new Bundle();
            b.putSerializable("Arr", bondedDevices); // not sure if this casts
            //i.setClass(this, SearchDetailsActivity.class);
            i.putExtras(b);
            startActivity(i);
        }
    }

    public void startNavigation(View v){
        Intent i = new Intent(this, NaviActivity.class);
        startActivity(i);
    }

    public void getData(View v){
        Intent i = new Intent(this, TrilaterationData.class);
        i.putExtra("info",info);
        i.putExtra("remoteBTAddress",address);
        startActivity(i);
    }

    public void off(View v){
        if (myBluetooth != null) {
            myBluetooth.disable();
            Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_LONG).show();

            b2.setVisibility(v.INVISIBLE);
            b3.setVisibility(v.INVISIBLE);
        }
        else{
            tv.setText("Bluetooth already disabled");
        }
    }

    public double[] getDistBluetooth(){
        double[] res = {1,2};
        return res;
    }

}