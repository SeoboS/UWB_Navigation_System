package r.seobo.test;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;


import java.lang.reflect.Array;
import java.util.ArrayList;

import static r.seobo.test.Constants.*;

public class TrilaterationData extends AppCompatActivity {

    private static final String TAG = "MY_APP_DEBUG_TAG";
    private static final String NAME = "UWB_Device";

    TextView textStatus, userCoord;
    private ListView mCoordinateView;
    private ArrayAdapter<String> mArrayAdapter;


    private BluetoothAdapter myBluetooth;
    private UserLocation userLocation;
    private String mConnectedDeviceName;
    private BluetoothCoordinateService bts;
    private String address;
    private String deviceName;

    private Double distAnch1 = -2.0, distAnch2 = -2.0, distAnch3 = -2.0;


//    PointsGraphSeries<DataPoint> series2;
//    private ArrayList<XYValue> xyValueArray;
//    private static final int MAX_COORDS_ON_GRAPH = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //currentView =
        setContentView(R.layout.activity_trilateration_data);

        textStatus = (TextView)findViewById(R.id.status);
        userCoord = (TextView)findViewById(R.id.userCoord);
        mCoordinateView = (ListView) findViewById(R.id.in);

//
//        final GraphView graph = (GraphView) findViewById(R.id.graph);
//        //graph.getViewport().setScalable(true);
//        //graph.getViewport().setScalableY(true);
//        graph.getViewport().setScrollable(true);
//        graph.getViewport().setScrollableY(true);
//
//        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>( new DataPoint[] {
//                new DataPoint(FIXED_ANCHOR_POSITIONS[0][0], FIXED_ANCHOR_POSITIONS[0][1]),
//                new DataPoint(FIXED_ANCHOR_POSITIONS[1][0], FIXED_ANCHOR_POSITIONS[1][1]),
//                new DataPoint(FIXED_ANCHOR_POSITIONS[2][0], FIXED_ANCHOR_POSITIONS[2][1])
//        }
//        );
//        graph.addSeries(series);
//        xyValueArray = new ArrayList<>(MAX_COORDS_ON_GRAPH);
//        series2 = new PointsGraphSeries<>();

        userLocation = new UserLocation();
        Intent i = this.getIntent();
        deviceName = i.getStringExtra("deviceName");
        address = i.getStringExtra("address");

        // Initialize the array adapter for the conversation thread
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);

        mCoordinateView.setAdapter(mArrayAdapter);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        bts = new BluetoothCoordinateService(this,mHandler);
        bts.connect(myBluetooth.getRemoteDevice(address),true);


        userLocation.setListener(new UserLocation.ChangeListener() { // everytime coordinates are updated, change value
            @Override
            public void onChange() {
                String temp = String.format("x: %d, y: %d\n", userLocation.getLocation()[0], userLocation.getLocation()[1]);
                userCoord.setText(temp);
                /*
                if (xyValueArray.size() >= MAX_COORDS_ON_GRAPH) {
                    xyValueArray.remove(0);
                    xyValueArray.add(new XYValue(userLocation.getLocation()[0], userLocation.getLocation()[1]));
                }
                else{
                    xyValueArray.add(new XYValue(userLocation.getLocation()[0], userLocation.getLocation()[1]));
                }
                xyValueArray = sortArray(xyValueArray);

                for(int i = 0;i <xyValueArray.size(); i++){
                    try{
                        double x = xyValueArray.get(i).getX();
                        double y = xyValueArray.get(i).getY();
                        series2.appendData(new DataPoint(x,y),false, 10);
                    }catch (IllegalArgumentException e){
                        Log.e(TAG, "createScatterPlot: IllegalArgumentException: " + e.getMessage() );
                    }
                }
                graph.addSeries(series2);
                */

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
                            mArrayAdapter.clear();
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
                            textStatus.setText("Couldn't get anchor dist data ");
                        }
                        temp.concat("\n");
                    }

                    if (distAnch1 > -1 && distAnch2 > -1 && distAnch3 > -1) {
                        userLocation.setLocation(saveDistDataAndTrilaterate(distAnch1, distAnch2, distAnch3));
                    }

                    mArrayAdapter.add(temp);
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

//    public void readBTData(View v){
//        BluetoothSocket btSocket = BluetoothConnectionService.getSocket();
//        if(btSocket.isConnected()) {
//            try {
//                InputStream i = btSocket.getInputStream();
//                Scanner s = new Scanner(i);
//                s.useDelimiter("^\\[.*\\]$"); // gets the data in between the brackets [packet]
//                //btDataView.setText(s.delimiter().toString());
//                if (s.hasNextLine()) {
//                    String tmp, type, value;
//                    tmp = s.nextLine();
//                    if (tmp.length() > 1) {
//                        tmp = tmp.substring(1, tmp.length() - 1); //strip the brackets
//                    }
//                    //btDataView.append(tmp);
//                    //btDataView.setText(tmp); // useful for debugging the bluetooth connection.
//
//                    // this formatting is just for the distance data, not for debugging
//                    if (tmp.contains(ADDR_ANCH_1) || tmp.contains(ADDR_ANCH_2) || tmp.contains(ADDR_ANCH_3)) {        // exception case
//                        for (String str : tmp.split(", ")) {      // get all split data
//                            btDataView.append(str + " ");
//                            int ind = str.indexOf(": ");
//                            if (ind != -1) {
//                                textStatus.setText("data updated");
//                                type = str.substring(0, ind);
//                                value = str.substring(str.indexOf(":") + 1);
//                                if (type != null && value != null) {
//                                    switch (type) {
//                                        case ADDR_ANCH_1:
//                                            distAnch1 = Double.valueOf(value);
//                                            break;
//                                        case ADDR_ANCH_2:
//                                            distAnch2 = Double.valueOf(value);
//                                            break;
//                                        case ADDR_ANCH_3:
//                                            distAnch3 = Double.valueOf(value);
//                                            break;
//                                        default:
//                                            break;
//                                    }
//                                }
//                            }
//                            else{
//                                textStatus.setText("Couldn't get anchor dist data ");
//                            }
//                            btDataView.append("\n");
//                        }
//
//                        if (distAnch1 > -1 && distAnch2 > -1 && distAnch3 > -1) {
//                            saveDistDataAndTrilaterate(distAnch1, distAnch2, distAnch3);
//                        }
//
//                    } else {
//                        textStatus.setText("Invalid anchor distance packet");
//                    }
//                } else {
//                    try {
//                        s.reset();
//                        //btDataView.setText("DEBUG: "  + s.nextLine()); // useful for debugging the bluetooth connection.
//                        //byte[] buff = new byte[100];
//                        //i.read(buff, 0,buff.length);
//                        //btDataView.setText(new String(buff));
//                        textStatus.setText("No properly delimited packets found");
//                    } catch (Exception e) {
//                        textStatus.setText(e.toString());
//                    }
//                }
//            } catch (IOException e) {
//                textStatus.setText(e.toString());
//            }
//        }
//        else{
//            Intent i = new Intent(this,StartMenu.class);
//            i.putExtra("error","Socket not connected");
//            startActivity(i);
//        }
//    }

    /**
     * Sorts an ArrayList<XYValue> with respect to the x values.
     * @param array
     * @return
     */
    private ArrayList<XYValue> sortArray(ArrayList<XYValue> array){
        /*
        //Sorts the xyValues in Ascending order to prepare them for the PointsGraphSeries<DataSet>
         */
        int factor = Integer.parseInt(String.valueOf(Math.round(Math.pow(array.size(),2))));
        int m = array.size() - 1;
        int count = 0;
        Log.d(TAG, "sortArray: Sorting the XYArray.");


        while (true) {
            m--;
            if (m <= 0) {
                m = array.size() - 1;
            }
            //Log.d(TAG, "sortArray: m = " + m);
            try {
                //print out the y entrys so we know what the order looks like
                //Log.d(TAG, "sortArray: Order:");
                //for(int n = 0;n < array.size();n++){
                //Log.d(TAG, "sortArray: " + array.get(n).getY());
                //}
                int tempY = array.get(m - 1).getY();
                int tempX = array.get(m - 1).getX();
                if (tempX > array.get(m).getX()) {
                    array.get(m - 1).setY(array.get(m).getY());
                    array.get(m).setY(tempY);
                    array.get(m - 1).setX(array.get(m).getX());
                    array.get(m).setX(tempX);
                } else if (tempX == array.get(m).getX()) {
                    count++;
                    //Log.d(TAG, "sortArray: count = " + count);
                } else if (array.get(m).getX() > array.get(m - 1).getX()) {
                    count++;
                   // Log.d(TAG, "sortArray: count = " + count);
                }
                //break when factorial is done
                if (count == factor) {
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "sortArray: ArrayIndexOutOfBoundsException. Need more than 1 data point to create Plot." +
                        e.getMessage());
                break;
            }
        }
        return array;
    }

    /**
     * Handle storage of distance data
     * @param d1    distance between tag and first anchor
     * @param d2    distance between tag and second anchor
     * @param d3    distance between tag and third anchor
     */
    public static int[] saveDistDataAndTrilaterate(double d1, double d2, double d3){
        //gotta figure out how to store this data
        // this is preliminary, just the original
        double[] d = {d1,d2,d3};
        //userCoord.setText("d1: "+String.valueOf(d1) +", d2: "+ String.valueOf(d2) + ", d3: " + String.valueOf(d3) + "\n");
        return trilaterate(FIXED_ANCHOR_POSITIONS,d);
    }

    /**
     * Trilaterate based on 2D positions and distances.
     */
    public static int[] trilaterate(double[][] positions, double[] distances) {
        TrilaterationFunction trilaterationFunction = new TrilaterationFunction(positions, distances);

        NonLinearLeastSquaresSolver nlSolver = new NonLinearLeastSquaresSolver(trilaterationFunction, new LevenbergMarquardtOptimizer());

        LeastSquaresOptimizer.Optimum nonLinearOptimum;
        nonLinearOptimum = nlSolver.solve();
        double[] centroid = nonLinearOptimum.getPoint().toArray();
        int[] centroidInt = new int[centroid.length];

        for (int i = 0; i < centroid.length; ++i){ // convert double to int
            centroidInt[i] = (int) centroid[i];
        }

        return centroidInt;
        //userLocation.setLocation(centroidInt);
    }

}
