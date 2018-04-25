package r.seobo.test;

public class UserLocation{
    private int[] location;
    private ChangeListener listener;

    public void setLocation(int[] l){
        this.location = l;
        if (listener != null) listener.onChange();
    }
    public int[] getLocation(){
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