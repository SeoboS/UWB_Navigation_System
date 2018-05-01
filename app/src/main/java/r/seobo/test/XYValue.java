package r.seobo.test;

import android.support.annotation.NonNull;

/**
 * Created by mitchtabian on 1/27/2017.
 */

public class XYValue implements Comparable{
    private double x;
    private double y;

    public XYValue(double x, double  y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString(){
        return String.format("[x:%6.3f/y:%6.3f]",x, y);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        if (this.x <((XYValue)o).x)
            return -1;
        else if (this.x >((XYValue)o).x)
            return +1;
        return 0;
    }
}
