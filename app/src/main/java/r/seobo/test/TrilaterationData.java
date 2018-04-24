package r.seobo.test;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Random;
import java.util.Scanner;

import static r.seobo.test.Constants.STATE_CONNECTED;
import static r.seobo.test.Constants.STATE_CONNECTING;
import static r.seobo.test.Constants.STATE_LISTEN;
import static r.seobo.test.Constants.STATE_NONE;

public class TrilaterationData extends AppCompatActivity {

    private static final String TAG = "MY_APP_DEBUG_TAG";
    private static final String NAME = "UWB_Device";
    private static final String addrAnch1 = "4369", addrAnch2 = "8738", addrAnch3 = "13107";
    private static final double[][] FIXED_ANCHOR_POSITIONS = {
            {0.0, 0.0},
            {2.0,0.0},
            {2.0,2.0} };
    /*
               x
               |
              1 m
               |
    x----1m----x
     */

    TextView textStatus, btDataView, userCoord;

    private BluetoothAdapter myBluetooth;
    private BluetoothDevice remote;
    private String remoteBTAddress;
    private ProgressDialog progress;
    boolean isBtConnected = false;
    private UserLocation userLocation = new UserLocation();
    private String mConnectedDeviceName;
    private BluetoothCoordinateService bts;
    private String address;
    private String deviceName;
    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trilateration_data);

        textStatus = (TextView)findViewById(R.id.status);
        btDataView = (TextView)findViewById(R.id.btDataView);
        userCoord = (TextView)findViewById(R.id.userCoord);
        btDataView.setMovementMethod(new ScrollingMovementMethod());

        Intent i = this.getIntent();
        deviceName = i.getStringExtra("deviceName");
        address = i.getStringExtra("address");
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        bts = new BluetoothCoordinateService(this,mHandler);
        bts.connect(myBluetooth.getRemoteDevice(address),true);

        userLocation.setListener(new UserLocation.ChangeListener() { // everytime coordinates are updated, change value
            @Override
            public void onChange() {
                String temp = String.format("x: %4.2f, y: %4.2f\n", userLocation.getLocation()[0], userLocation.getLocation()[1]);
                userCoord.setText(temp);
            }
        });
    }


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
            Activity activity = TrilaterationData.this;
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

    public void readBTData(View v){
        BluetoothSocket btSocket = BluetoothConnectionService.getSocket();
        if(btSocket.isConnected()) {
            try {
                InputStream i = btSocket.getInputStream();
                Scanner s = new Scanner(i);
                s.useDelimiter("^\\[.*\\]$"); // gets the data in between the brackets [packet]
                //btDataView.setText(s.delimiter().toString());
                if (s.hasNextLine()) {
                    String tmp, type, value;
                    Double distAnch1 = -2.0, distAnch2 = -2.0, distAnch3 = -2.0;
                    tmp = s.nextLine();
                    if (tmp.length() > 1) {
                        tmp = tmp.substring(1, tmp.length() - 1); //strip the brackets
                    }
                    //btDataView.append(tmp);
                    //btDataView.setText(tmp); // useful for debugging the bluetooth connection.

                    // this formatting is just for the distance data, not for debugging
                    if (tmp.contains(addrAnch1) || tmp.contains(addrAnch2) || tmp.contains(addrAnch3)) {        // exception case
                        for (String str : tmp.split(", ")) {      // get all split data
                            btDataView.append(str + " ");
                            int ind = str.indexOf(": ");
                            if (ind != -1) {
                                textStatus.setText("data updated");
                                type = str.substring(0, ind);
                                value = str.substring(str.indexOf(":") + 1);
                                if (type != null && value != null) {
                                    switch (type) {
                                        case addrAnch1:
                                            distAnch1 = Double.valueOf(value);
                                            break;
                                        case addrAnch2:
                                            distAnch2 = Double.valueOf(value);
                                            break;
                                        case addrAnch3:
                                            distAnch3 = Double.valueOf(value);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                            else{
                                textStatus.setText("Couldn't get anchor dist data ");
                            }
                            btDataView.append("\n");
                        }

                        if (distAnch1 > -1 && distAnch2 > -1 && distAnch3 > -1) {
                            saveDistData(distAnch1, distAnch2, distAnch3);
                        }

                    } else {
                        textStatus.setText("Invalid anchor distance packet");
                    }
                } else {
                    try {
                        s.reset();
                        btDataView.setText("DEBUG: "  + s.nextLine()); // useful for debugging the bluetooth connection.
                        //byte[] buff = new byte[100];
                        //i.read(buff, 0,buff.length);
                        //btDataView.setText(new String(buff));
                        textStatus.setText("No properly delimited packets found");
                    } catch (Exception e) {
                        textStatus.setText(e.toString());
                    }
                }
            } catch (IOException e) {
                textStatus.setText(e.toString());
            }
        }
        else{
            Intent i = new Intent(this,StartMenu.class);
            i.putExtra("error","Socket not connected");
            startActivity(i);
        }
    }

    /**
     * Handle storage of distance data
     * @param d1    distance between tag and first anchor
     * @param d2    distance between tag and second anchor
     * @param d3    distance between tag and third anchor
     */
    private void saveDistData(double d1, double d2, double d3){
        //gotta figure out how to store this data
        // this is preliminary, just the original
        double[] d = {d1,d2,d3};
        btDataView.setText("d1: "+String.valueOf(d1) +", d2: "+ String.valueOf(d2) + ", d3: " + String.valueOf(d3) + "\n");
        trilaterate(FIXED_ANCHOR_POSITIONS,d);
    }

    /**
     * Trilaterate based on 2D positions and distances.
     */
    private void trilaterate(double[][] positions, double[] distances) {
        TrilaterationFunction trilaterationFunction = new TrilaterationFunction(positions, distances);

        NonLinearLeastSquaresSolver nlSolver = new NonLinearLeastSquaresSolver(trilaterationFunction, new LevenbergMarquardtOptimizer());

        LeastSquaresOptimizer.Optimum nonLinearOptimum;
        nonLinearOptimum = nlSolver.solve();
        double[] centroid = nonLinearOptimum.getPoint().toArray();
        userLocation.setLocation(centroid);
    }

}
