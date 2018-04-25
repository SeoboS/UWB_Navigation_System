package r.seobo.test;


public interface Constants {

    public static final String ADDR_ANCH_1 = "4369", ADDR_ANCH_2 = "8738", ADDR_ANCH_3 = "13107";

    public static final double[][] FIXED_ANCHOR_POSITIONS = {
            {0.0, 0.0},
            {200,0.0},
            {200,200} };
    /*
               x
               |
              1 m
               |
    x----1m----x
     */


    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    public static final int[][] ECE_GRAPH = new int[][]{
            {0, 600, 1099, 1798, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 300, 2795, -1, -1, 2097, -1, -1, -1},
            {600, 0, 500, 1199, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 898, 2196, -1, -1, 1498, -1, -1, -1},
            {1099, 500, 0, 700, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1397, 1697, -1, -1, 999, -1, -1, -1},
            {1798, 1199, 700, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2096, 998, -1, -1, 300, -1, -1, -1},
            {-1, -1, -1, -1, 0, 300, 499, 898, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 400, 1098, -1, -1},
            {-1, -1, -1, -1, 300, 0, 200, 599, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 699, 799, -1, -1},
            {-1, -1, -1, -1, 499, 200, 0, 400, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 998, 600, -1, -1},
            {-1, -1, -1, -1, 898, 599, 400, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1297, 600, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 500, -1, 900, -1, 300, -1, -1, -1, 100, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 300, 699, 998, 1596, -1, -1, 599, 400, 100, 300, 699, 998, 1397, 1596, 2195, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2293, 897, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 300, 0, 400, 699, 1297, -1, -1, 898, 699, 300, 100, 400, 699, 1098, 1297, 1896, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1994, 1196, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 699, 400, 0, 300, 898, -1, -1, 1297, 1098, 699, 400, 100, 300, 699, 898, 1497, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1595, 1595, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 998, 699, 300, 0, 599, -1, -1, 1596, 1397, 998, 699, 300, 100, 400, 599, 1198, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1296, 1894, -1, -1, -1, -1,},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 1596, 1297, 898, 599, 0, 600, -1, 2194, 1995, 1596, 1297, 898, 599, 200, 100, 600, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 698, 2493, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 600, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 400, -1, -1, -1, 500, -1, -1, -1, -1, 200, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 100, -1, -1, -1, 400},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 599, 898, 1297, 1596, 2194, -1, -1, 0, 200, 599, 898, 1297, 1596, 1995, 2194, 2793, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2891, 300, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 400, 699, 1098, 1397, 1995, -1, -1, 200, 0, 400, 699, 1098, 1397, 1796, 1995, 2594, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2692, 498, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 100, 300, 699, 998, 1596, -1, -1, 599, 400, 0, 300, 699, 998, 1397, 1596, 2195, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2293, 897, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 300, 100, 400, 699, 1297, -1, -1, 898, 699, 300, 0, 400, 699, 1098, 1297, 1896, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1994, 1196, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 699, 400, 100, 300, 898, -1, -1, 1297, 1098, 699, 400, 0, 300, 699, 898, 1497, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1595, 1595, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 998, 699, 300, 100, 599, -1, -1, 1596, 1397, 998, 699, 300, 0, 400, 599, 1198, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1296, 1894, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 1497, 1198, 699, 400, 200, -1, -1, 1995, 1796, 1397, 1098, 699, 400, 0, 200, 799, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 897, 2293, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 1596, 1297, 898, 599, 100, -1, -1, 2194, 1995, 1596, 1297, 898, 599, 200, 0, 600, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 698, 2492, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 2195, 1896, 1497, 1198, 600, -1, -1, 2793, 2594, 2195, 1896, 1497, 1198, 799, 600, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 100, 3091, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 200, -1, -1, -1, -1, -1, -1, -1, -1, 200, -1, -1, -1, 400, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 200, 0, -1, -1, -1, -1, -1, -1, -1, -1, 200, -1, -1, -1, 200, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 300, -1, -1, 899, -1, 899, -1, -1, -1, -1, -1, -1, 699},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 300, 0, -1, -1, 600, -1, 600, -1, -1, -1, -1, -1, -1, 400},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, 799, -1, 100, -1, -1, -1, -1, -1, -1, 700},
            {-1, -1, -1, -1, -1, -1, -1, -1, 500, -1, -1, -1, -1, -1, 400, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, 799, -1, 400, -1, -1, -1, 500, 400, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 899, 600, 799, -1, 0, -1, 800, -1, -1, -1, -1, -1, -1, 300},
            {-1, -1, -1, -1, -1, -1, -1, -1, 900, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 799, -1, 0, -1, 1000, -1, -1, -1, 800, -1, -1},
            {300, 898, 1387, 2096, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 899, 600, 100, -1, 800, -1, 0, 3090, -1, -1, 2390, -1, -1, 700},
            {2795, 2196, 1697, 998, -1, -1, -1, -1, 300, -1, -1, -1, -1, -1, 500, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 400, -1, 1000, 3090, 0, -1, -1, 690, 400, 600, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 2293, 1994, 1595, 1296, 698, -1, -1, 2891, 2692, 2293, 1994, 1595, 1296, 897, 698, 100, 200, 200, -1, -1, -1, -1, -1, -1, -1, -1, 0, 3190, -1, -1, 400, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, 897, 1196, 1595, 1894, 2493, -1, 100, 300, 498, 897, 1196, 1595, 1894, 2293, 2492, 3091, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 3190, 0, -1, -1, -1, 400},
            {2097, 1498, 999, 300, 400, 699, 998, 1297, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2390, 690, -1, -1, 0, -1, -1, -1},
            {-1, -1, -1, -1, 1098, 799, 600, 600, 100, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 500, -1, 800, -1, 400, -1, -1, -1, 0, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 200, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 400, 200, -1, -1, -1, 400, -1, -1, -1, 600, 400, -1, -1, -1, 0, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 400, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 699, 400, 700, -1, 300, -1, 700, -1, -1, 400, -1, -1, -1, 0}


    };



}