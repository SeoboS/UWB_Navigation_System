package r.seobo.test;

public class UserLocation{
    private double[] location;
    private ChangeListener listener;

    public void setLocation(double[] l){
        this.location = l;
        if (listener != null) listener.onChange();
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