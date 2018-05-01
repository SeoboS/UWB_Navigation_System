package r.seobo.test;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.Random;

public class UserLocation{
    private double[] location;
    private ChangeListener listener;
    private Random rand;

    /***
     * Only set location if new location is new.
     * @param
     */
    public UserLocation(){
        rand = new Random(System.currentTimeMillis());
    }
    public void setLocation(double[] l) {
        if (location == null) {
            this.location = l;
            if (listener != null) listener.onChange();
        } else {
            int diffVal = 0;
            for (int i = 0; i < l.length; ++i) {
                if (l[i] != location[i]) {
                    diffVal = 1;
                    break;
                }
            }
            if (diffVal == 1) {
                l[0] = l[0] + rand.nextDouble()/10.0;
                this.location = l;
                if (listener != null) listener.onChange();
            }
        }
    }
    public void setLocation(int[] l){
        double[] temp = new double[2];
        for (int i = 0; i < l.length; ++i){
            temp[i] = (double) l[i];
        }
        setLocation(temp);
    }

    public double[] getLocation(){
        return this.location;
    }
    public ChangeListener getListener() {
        return listener;
    }

    public void setListener (ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }

}