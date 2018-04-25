package r.seobo.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.Intent;

public class NaviActivity extends AppCompatActivity {

    private MainNavigation N1;
    private UserLocation userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);
        final TextView userCoord = (TextView)findViewById(R.id.userCoord);
        initSpinners();
        N1 = new MainNavigation();

        userLocation.setListener(new UserLocation.ChangeListener() { // everytime coordinates are updated, change value
            @Override
            public void onChange() {
                String temp = String.format("x: %4.2f, y: %4.2f\n", userLocation.getLocation()[0], userLocation.getLocation()[1]);
                userCoord.setText(temp);

                TextView t1 = (TextView) findViewById(R.id.textView1);
                TextView t2 = (TextView) findViewById(R.id.textView2);
                int int1 = userLocation.getLocation()[0];
                int int2 = userLocation.getLocation()[1];
                t1.setText("Shortest distance between vertices " + Integer.toString(int1) + " and " + Integer.toString(int2) + " is: " + Integer.toString(N1.shortestPathValue(int1,int2)));
                N1.setCurrentPath(int1,int2);
                t2.setText(N1.pathToString());
            }
        });

    }


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
        int int1 = Integer.parseInt(e1.getText().toString());
        int int2 = Integer.parseInt(e2.getText().toString());
        t1.setText("Shortest distance between vertices " + Integer.toString(int1) + " and " + Integer.toString(int2) + " is: " + Integer.toString(N1.shortestPathValue(int1,int2)));
        N1.setCurrentPath(int1,int2);
        t2.setText(N1.pathToString());
    }

    public void Button2(View v) {
        TextView t2 = (TextView)findViewById(R.id.textView2);
        EditText e1 = (EditText)findViewById(R.id.editText1);
        EditText e2 = (EditText)findViewById(R.id.editText2);
        int int1 = Integer.parseInt(e1.getText().toString());
        int int2 = Integer.parseInt(e2.getText().toString());
        N1.navigate(int1,int2);
        t2.setText(N1.pathToString());
    }




}


    /*

    MainNavigation N1;
    Intent i = new Intent(this,TrilaterationData.class);
    i.putExtra("Navi1", N1);

        Intents
    */


    /*
    MainNavigation N1;
    Intent i = new Intent(this,NaviActivity.class);


    */