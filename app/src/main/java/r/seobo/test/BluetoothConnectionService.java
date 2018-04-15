package r.seobo.test;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothConnectionService extends Service {

    private BluetoothSocketWrapper bluetoothSocket;
    private boolean secure;
    private BluetoothAdapter myBluetooth;
    private int candidate;
    private BluetoothDevice remote;
    private static BluetoothSocket socket;
    private ParcelUuid[] uuidCandidates;


    public BluetoothConnectionService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String info = intent.getStringExtra("info");
        String address = intent.getStringExtra("address");
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        remote = myBluetooth.getRemoteDevice(address);
        uuidCandidates = remote.getUuids();
        try {
            this.setSocket(connect().getUnderlyingSocket());
            Intent i = new Intent(BluetoothConnectionService.this, StartMenu.class);
            i.putExtra("info","Bluetooth Socket connected");
            startActivity(i);
        }catch(IOException e) {
            Intent i = new Intent(BluetoothConnectionService.this, StartMenu.class);
            i.putExtra("info",e.toString());
            startActivity(i);
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //BluetoothSocket btSocket = BluetoothConnector.getSocket();
        BluetoothConnectionService.disconnect();
    }

    public static synchronized void setSocket(BluetoothSocket s){
        BluetoothConnectionService.socket = s;
    }

    public static synchronized BluetoothSocket getSocket(){
        return BluetoothConnectionService.socket;
    }

    public BluetoothSocketWrapper connect() throws IOException{
        boolean success = false;
        while (selectSocket()) {
            myBluetooth.cancelDiscovery();
            if (!bluetoothSocket.getUnderlyingSocket().isConnected()) {
                try {
                    bluetoothSocket.connect();
                    bluetoothSocket.getOutputStream().write(1);
                    bluetoothSocket.getOutputStream().flush();
                    success = true;
                    break;
                } catch (IOException e) {
                    //try the fallback
                    try {
                        bluetoothSocket = new FallbackBluetoothSocket(bluetoothSocket.getUnderlyingSocket());
                        Thread.sleep(500);
                        bluetoothSocket.connect();
                        bluetoothSocket.getOutputStream().write(1);
                        bluetoothSocket.getOutputStream().flush();
                        success = true;
                        break;
                    } catch (FallbackException e1) {
                        Log.w("BT", "Could not initialize FallbackBluetoothSocket classes.", e);
                    } catch (InterruptedException e1) {
                        Log.w("BT", e1.getMessage(), e1);
                    } catch (IOException e1) {
                        Log.w("BT", "Fallback failed. Cancelling.", e1);
                    }

                }
            }
            else{
                if (socket.isConnected()) {
                    Intent i = new Intent(BluetoothConnectionService.this, StartMenu.class);
                    i.putExtra("info", "Bluetooth Socket Connected");
                    startActivity(i);
                    break;
                }
            }
        }

        if (!success) {
            throw new IOException("Could not connect to device: "+ remote.getAddress());
        }

        Runtime.getRuntime().addShutdownHook(new Thread(){public void run(){
            try {
                bluetoothSocket.close();
                System.out.println("The server is shut down!");
            } catch (IOException e) { /* failed */ }
        }});

        return bluetoothSocket;
    }

    private boolean selectSocket() throws IOException {
        if (candidate >= uuidCandidates.length) { /// exception with the null length here
            return false;
        }

        BluetoothSocket tmp;
        UUID uuid = uuidCandidates[candidate++].getUuid();

        Log.i("BT", "Attempting to connect to Protocol: "+ uuid);
        if (secure) {
            tmp = remote.createRfcommSocketToServiceRecord(uuid);
        } else {
            tmp = remote.createInsecureRfcommSocketToServiceRecord(uuid);
        }
        bluetoothSocket = new NativeBluetoothSocket(tmp);

        return true;
    }

    public static interface BluetoothSocketWrapper {

        InputStream getInputStream() throws IOException;

        OutputStream getOutputStream() throws IOException;

        String getRemoteDeviceName();

        void connect() throws IOException;

        String getRemoteDeviceAddress();

        void close() throws IOException;

        BluetoothSocket getUnderlyingSocket();

    }

    public static class NativeBluetoothSocket implements BluetoothSocketWrapper {

        private BluetoothSocket socket;

        public NativeBluetoothSocket(BluetoothSocket tmp) {
            this.socket = tmp;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return socket.getOutputStream();
        }

        @Override
        public String getRemoteDeviceName() {
            return socket.getRemoteDevice().getName();
        }

        @Override
        public void connect() throws IOException {
            socket.connect();
        }

        @Override
        public String getRemoteDeviceAddress() {
            return socket.getRemoteDevice().getAddress();
        }

        @Override
        public void close() throws IOException {
            socket.close();
        }

        @Override
        public BluetoothSocket getUnderlyingSocket() {
            return socket;
        }

    }

    public class FallbackBluetoothSocket extends NativeBluetoothSocket {

        private BluetoothSocket fallbackSocket;

        public FallbackBluetoothSocket(BluetoothSocket tmp) throws FallbackException {
            super(tmp);
            try
            {
                Class<?> clazz = tmp.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[] {Integer.valueOf(1)};
                fallbackSocket = (BluetoothSocket) m.invoke(tmp.getRemoteDevice(), params);
            }
            catch (Exception e)
            {
                throw new FallbackException(e);
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return fallbackSocket.getInputStream();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return fallbackSocket.getOutputStream();
        }


        @Override
        public void connect() throws IOException {
            fallbackSocket.connect();
        }


        @Override
        public void close() throws IOException {
            fallbackSocket.close();
        }

    }

    public static class FallbackException extends Exception {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public FallbackException(Exception e) {
            super(e);
        }

    }

    public static void disconnect(){
        if (socket!=null) //If the btSocket is busy
        {
            try
            {
                socket.getInputStream().close();
                socket.close(); //close connection
                OutputStream os = BluetoothConnectionService.getSocket().getOutputStream();
                if (os != null) {
                    try{
                        os.close();
                    } catch (Exception e){
                        os = null;
                    }
                }
                InputStream is = socket.getInputStream();
                if (is != null) {
                    try{
                        is.close();
                    } catch (Exception e){
                        is = null;
                    }
                }
            }
            catch (IOException e) {
                Log.d("BluetoothConnector",e.toString());
            }
        }
        //finish(); //return to the first layout
    }

}
