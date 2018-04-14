package r.seobo.test;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
    private BluetoothSocket btSocket = null;
    boolean isBtConnected = false;
    private UserLocation userLocation = new UserLocation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trilateration_data);

        textStatus = (TextView)findViewById(R.id.status);
        btDataView = (TextView)findViewById(R.id.btDataView);
        userCoord = (TextView)findViewById(R.id.userCoord);
        btDataView.setMovementMethod(new ScrollingMovementMethod());

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if ( myBluetooth != null ){

            Intent i = this.getIntent();
            remoteBTAddress = i.getStringExtra("remoteBTAddress");
            String info = i.getStringExtra("info");
            remote = myBluetooth.getRemoteDevice(remoteBTAddress);
            if (remote != null){
                myBluetooth.cancelDiscovery();
                //new ConnectBT().execute(); // TODO: returns back to start menu screen
                BluetoothConnector btc = new BluetoothConnector(remote,true,myBluetooth);
                try {
                    btSocket = btc.connect().getUnderlyingSocket();
                }
                catch(IOException e) {
                    try {
                        BluetoothSocket bluetoothSocket = (BluetoothSocket) remote.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(remote, 1);
                        bluetoothSocket.connect();
                        isBtConnected = true;
                    } catch (NoSuchMethodException e1) {
                        Log.w("BT", "No such method found", e1);
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    textStatus.setText(e.toString());
                }
                if (btSocket != null) {
                    if (btSocket.isConnected()) {
                        textStatus.setText("Bluetooth Socket Connected");
                    }
                }

            }
            else{
                Intent j = new Intent(this, StartMenu.class);
                j.putExtra("missedRemoteInfo",info);
                startActivity(j);
            }
        }
        else{
            Intent i = new Intent(this,StartMenu.class);
            startActivity(i);
        }


        userLocation.setListener(new UserLocation.ChangeListener() { // everytime coordinates are updated, change value
            @Override
            public void onChange() {
                String temp = String.format("x: %4.2f, y: %4.2f\n", userLocation.getLocation()[0], userLocation.getLocation()[1]);
                userCoord.setText(temp);
            }
        });
    }

    //UI Thread
    private class ConnectBT extends AsyncTask<Void, Void, Void>{
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(TrilaterationData.this, "Connecting...", "Please wait");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
            BluetoothDevice remote = myBluetooth.getRemoteDevice(remoteBTAddress);//connects to the device's remoteBTAddress and checks if it's available
            ParcelUuid[] uuids = remote.getUuids();
            Random r = new Random(System.currentTimeMillis());

            int tries = 0;
            while(tries < 4) {
                try {

                    if (btSocket == null || !isBtConnected) {
                        btSocket = remote.createRfcommSocketToServiceRecord(uuids[r.nextInt(uuids.length)].getUuid());//create a RFCOMM (SPP) connection
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        btSocket.connect();//start connection - works!
                        btSocket.getOutputStream().write(1);
                        btSocket.getOutputStream().flush();
                        tries = 0;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ConnectSuccess = false; //if the try failed, you can check the exception here
                    ++tries;
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);
            TextView t = (TextView) findViewById(R.id.status);
            if (!ConnectSuccess)
            {
                t.setText("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                t.setText("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    public void readBTData(View v){
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
    public void saveDistData(double d1, double d2, double d3){
        //gotta figure out how to store this data
        // this is preliminary, just the original
        double[] d = {d1,d2,d3};
        btDataView.setText("d1: "+String.valueOf(d1) +", d2: "+ String.valueOf(d2) + ", d3: " + String.valueOf(d3) + "\n");
        trilaterate(FIXED_ANCHOR_POSITIONS,d);
    }

    /**
     * Trilaterate based on 2D positions and distances.
     */
    public void trilaterate(double[][] positions, double[] distances) {
        TrilaterationFunction trilaterationFunction = new TrilaterationFunction(positions, distances);

        NonLinearLeastSquaresSolver nlSolver = new NonLinearLeastSquaresSolver(trilaterationFunction, new LevenbergMarquardtOptimizer());

        LeastSquaresOptimizer.Optimum nonLinearOptimum;
        nonLinearOptimum = nlSolver.solve();
        double[] centroid = nonLinearOptimum.getPoint().toArray();
        userLocation.setLocation(centroid);
    }

    public void disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.getInputStream().close();
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { textStatus.setText("Error");}
        }
        finish(); //return to the first layout
    }
    @Override
    protected void onDestroy() {
        if (btSocket!=null){
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

}
