package r.seobo.test;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import static r.seobo.test.Constants.ADDR_ANCH_1;
import static r.seobo.test.Constants.ADDR_ANCH_2;
import static r.seobo.test.Constants.ADDR_ANCH_3;
import static r.seobo.test.Constants.FIXED_ANCHOR_POSITIONS;
import static r.seobo.test.Constants.STATE_CONNECTED;
import static r.seobo.test.Constants.STATE_CONNECTING;
import static r.seobo.test.Constants.STATE_LISTEN;
import static r.seobo.test.Constants.STATE_NONE;

public class NaviActivity extends AppCompatActivity {

    private MainNavigation N1;
    private UserLocation userLocation;


    private BluetoothAdapter myBluetooth;
    private String mConnectedDeviceName;
    private BluetoothCoordinateService bts;
    private String address;
    private String deviceName;
    private boolean destSet;

    private Double distAnch1 = -2.0, distAnch2 = -2.0, distAnch3 = -2.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        final TextView userCoord = (TextView)findViewById(R.id.userCoord);
        initSpinners();
        N1 = new MainNavigation();

        userLocation = new UserLocation();
        destSet = false;

        Intent i = this.getIntent();

        // if this came from the devicelist screen, then it will have a bundle. If had arrived to navigation activity via the start menu, there will be no bundle
        if (i.getExtras() != null) {
            deviceName = i.getStringExtra("deviceName");
            address = i.getStringExtra("address");

            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            bts = new BluetoothCoordinateService(this, mHandler);
            bts.connect(myBluetooth.getRemoteDevice(address), true);

            // everytime coordinates are updated, update text display on bottom with user coordinates.
            // this is supposed to function as the update coordinate button does
            userLocation.setListener(new UserLocation.ChangeListener() {
                @Override
                public void onChange() {
                    String temp = String.format("x: %d, y: %d\n", userLocation.getLocation()[0], userLocation.getLocation()[1]);
                    userCoord.setText(temp);
                    if (destSet) {
                        updateCoordinates(userLocation.getLocation()[0], userLocation.getLocation()[1]);
                    }
                }
            });
        }
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
            Activity activity = NaviActivity.this;
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mArrayAdapter.clear();
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
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String line = new String(readBuf);
                    try {
                        line = new String(readBuf, "UTF-8");
                    }catch(Exception e){
                        line = new String(readBuf,msg.arg1);
                    }
                    String temp = "", type, value;
                    byte[] buffer = new byte[1024];
                    int bytes;

                    //verific
                    String msgToSend;
                    for (String str : line.split(",")) {      // get all split data
                        if (str == " "){
                            continue;
                        }
                        temp = temp.concat(str + " ");
                        int ind = str.indexOf(": ");
                        if (ind != -1) {
                            type = str.substring(0, ind);
                            value = str.substring(str.indexOf(":") + 1);
                            if (type != null && value != null) {
                                switch (type) {
                                    case ADDR_ANCH_1:
                                        distAnch1 = Double.valueOf(value);
                                        break;
                                    case ADDR_ANCH_2:
                                        distAnch2 = Double.valueOf(value);
                                        break;
                                    case ADDR_ANCH_3:
                                        distAnch3 = Double.valueOf(value);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                        else{
                            setStatus("Couldn't get anchor dist data ");
                        }
                        temp.concat("\n");
                    }

                    if (distAnch1 > -1 && distAnch2 > -1 && distAnch3 > -1) {
                        userLocation.setLocation(TrilaterationData.saveDistDataAndTrilaterate(distAnch1, distAnch2, distAnch3));
                    }

                    //mArrayAdapter.add(temp);
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


    public void initSpinners(){
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Room_Array_1, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        return;
    }

    public void Button1(View v) {
        TextView t1 = (TextView) findViewById(R.id.textView1);
        TextView t2 = (TextView) findViewById(R.id.textView2);
        EditText e1 = (EditText)findViewById(R.id.editText1);
        EditText e2 = (EditText)findViewById(R.id.editText2);

        //Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        //Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        if (e1.getText().toString() == "" || e2.getText().toString() == "" ) {
            t1.setText("Enter a start and destination");
        }
        else{
            int int1 = Integer.parseInt(e1.getText().toString());
            int int2 = Integer.parseInt(e2.getText().toString());
            if(int1 >40 || int1<0 || int2>40 || int2<0) {
                t1.setText("Invalid selection");
                return;
            }
            //t1.setText("Shortest distance between vertices " + Integer.toString(int1) + " and " + Integer.toString(int2) + " is: " + Integer.toString(N1.shortestPathValue(int1, int2)));
            t1.setText("Destination set!");
            N1.setCurrentPath(int1, int2);
            t2.setText(N1.pathToString() + "\n" + N1.stepToString());
            destSet = true;
        }
    }

    //RENAMED FROM BUTTON2 TO UPDATECOORDINATES
    // this is the method that clicking the button will access
    public void updateCoordinates(View v) {
        EditText e1 = (EditText)findViewById(R.id.editText1);
        EditText e2 = (EditText)findViewById(R.id.editText2);
        int int1 = Integer.parseInt(e1.getText().toString());
        int int2 = Integer.parseInt(e2.getText().toString());

        updateCoordinates(int1,int2);
    }

    //this is the method that can also be called by button AND also when the user location is updated
    public void updateCoordinates(int int1, int int2){
        TextView t1 = (TextView)findViewById(R.id.textView1);
        TextView t2 = (TextView)findViewById(R.id.textView2);
        int res = N1.navigate(int1,int2);
        String res2 = "No current destination!";
        if(res == 2)
            res2 = "Location unchanged!";
        if(res == 3)
            res2 = "You have arrived at your destination!";
        if(res == 4)
            res2 = "You have made progress towards your destination!";
        if(res == 5)
            res2 = "Wrong way! New path to destination displayed!";
        if(res == 6)
            res2 = "Presently in hallway!";
        t1.setText(res2);


        // using the following for testing purposes since steptostring isnt done yet
        //N1.setCurrentPath(int1, int2);

        if(res == 6)
            t2.setText(N1.pathToString() + "\n" + N1.hallStepToString());// Special form of stepToString without exitting current room, b/c you are in hall now
        else
            t2.setText(N1.pathToString() + "\n" + N1.stepToString());
    }




}
