package r.seobo.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainNavigation extends AppCompatActivity {
    int pSize;
    int [] current_path;
    int current_index = 0;
    int current_x = 0;
    int current_y = 0;
    int current_vertex = -1;
    int current_destination = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        pSize = getResources().getInteger(R.integer.pathSize);
        current_path = new int[pSize];
        initSpinners();
    }

    public void initSpinners(){
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Room_Array_1, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
    }

    int[][] graph = new int[][]{
            {-1, 12, -1, 9, -1, -1, -1, -1, -1},
            {12, -1, 6, -1, 8, -1, -1, -1, -1},
            {-1, 6, -1, -1, -1, 15, -1, -1, -1},
            {9, -1, -1, -1, 10, -1, 13, -1, -1},
            {-1, 8, -1, 10, -1, 7, -1, 2, -1},
            {-1, -1, 15, -1, 7, -1, -1, -1, 11},
            {-1, -1, -1, 13, -1, -1, -1, 10, -1},
            {-1, -1, -1, -1, 2, -1, 10, -1, 14},
            {-1, -1, -1, -1, -1, 11, -1, 14, -1}
    };


    public void Button1(View v) {
        TextView t1 = (TextView) findViewById(R.id.textView1);
        TextView t2 = (TextView) findViewById(R.id.textView2);
        EditText e1 = (EditText)findViewById(R.id.editText1);
        EditText e2 = (EditText)findViewById(R.id.editText2);
        int int1 = Integer.parseInt(e1.getText().toString());
        int int2 = Integer.parseInt(e2.getText().toString());
        t1.setText("Shortest distance between vertices " + Integer.toString(int1) + " and " + Integer.toString(int2) + " is: " + Integer.toString(shortestPathValue(int1,int2)));
        setCurrentPath(int1,int2);
        t2.setText(pathToString());
    }

    public void Button2(View v) {
        TextView t2 = (TextView)findViewById(R.id.textView2);
        EditText e1 = (EditText)findViewById(R.id.editText1);
        EditText e2 = (EditText)findViewById(R.id.editText2);
        int int1 = Integer.parseInt(e1.getText().toString());
        int int2 = Integer.parseInt(e2.getText().toString());
        navigate(int1,int2);
        t2.setText(pathToString());
    }


    public int shortestPathValue(int start, int end) {
        if (start > (pSize-1) || end > (pSize-1) || start < 0 || end < 0)
            return 0;

        int[] hasVisited = new int[pSize];
        int[] shortestPaths = new int[pSize];
        for (int i = 0; i < pSize; i++) {
            hasVisited[i] = 0;
            shortestPaths[i] = -1;
        }
        hasVisited[start] = 1;
        shortestPaths[start] = 0;
        for (int i = 0; i < pSize; i++) {
            if (graph[start][i] != -1) {
                shortestPaths[i] = graph[start][i];
            }
        }
        while (hasVisited[end] != 1) {
            int min = -1;
            int next = -1;
            for (int i = 0; i < pSize; i++) {
                if (hasVisited[i] == 1)
                    continue;
                if (min == -1) {
                    min = shortestPaths[i];
                    next = i;
                    continue;
                }
                if (shortestPaths[i] != -1) {
                    if (shortestPaths[i] < min) {
                        min = shortestPaths[i];
                        next = i;
                    }
                }
            }
            hasVisited[next] = 1;
            for (int i = 0; i < pSize; i++) {
                if (hasVisited[i] == 1)
                    continue;
                if (graph[next][i] != -1) {
                    if (shortestPaths[i] == -1 || shortestPaths[i] > (graph[next][i] + shortestPaths[next])) {
                        shortestPaths[i] = (graph[next][i] + shortestPaths[next]);
                    }
                }
            }

        }
        return shortestPaths[end];
    }

    public void setCurrentPath(int start, int end) {
        if(start > (pSize-1) || end > (pSize-1) || start < 0 || end < 0)
            return;
        current_vertex = start;
        current_index = 0;
        current_destination = end;
        int[] hasVisited = new int[pSize];
        int[] shortestPaths = new int[pSize];
        for(int i = 0; i<pSize; i++) {
            hasVisited[i] = 0;
            shortestPaths[i] = -1;
        }
        hasVisited[start] = 1;
        shortestPaths[start] = 0;
        for(int i = 0; i < pSize; i++) {
            if(graph[start][i] != -1) {
                shortestPaths[i] = graph[start][i];
            }
        }
        while(hasVisited[end] != 1)
        {
            int min = -1;
            int next = -1;
            for(int i = 0; i < pSize; i++) {
                if(hasVisited[i] == 1)
                    continue;
                if(min == -1) {
                    min = shortestPaths[i];
                    next = i;
                    continue;
                }
                if(shortestPaths[i] != -1) {
                    if (shortestPaths[i] < min) {
                        min = shortestPaths[i];
                        next = i;
                    }
                }
            }
            hasVisited[next] = 1;
            for(int i = 0; i < pSize; i++) {
                if(hasVisited[i] == 1)
                    continue;
                if(graph[next][i] != -1) {
                    if(shortestPaths[i] == -1 || shortestPaths[i] > (graph[next][i] + shortestPaths[next])){
                        shortestPaths[i] = (graph[next][i] + shortestPaths[next]);
                    }
                }
            }

        }
        int [] revPath = new int [pSize];
        for(int i = 0; i < pSize; i++) {
            revPath[i] = -1;
        }
        int j = 0;
        int current = end;
        while(true){
            int min = -1;
            int next = -1;
            for(int i = 0; i < pSize; i++) {
                if(hasVisited[i] != 1)
                    continue;
                if(graph[i][current] != -1 && graph[i][current] != 0) {
                    if(min == -1) {
                        min = shortestPaths[i] + graph[i][current];
                        next = i;
                        continue;
                    }
                    if ((shortestPaths[i] + graph[i][current]) < min) {
                        min = shortestPaths[i] + graph[i][current];
                        next = i;
                    }
                }
            }
            revPath[j] = current;
            current = next;
            j++;
            if(current == start) {
                revPath[j] = start;
                break;
            }
        }
        int i = 0;
        while(j >= 0) {
            current_path[i] = revPath[j];
            i++;
            j--;
        }
        if(i < pSize-1)
            current_path[i] = -1;
        return;
    }

    public String pathToString(){
        String path = "No current path!";
        if(current_path[current_index] == -1) {
            return path;
        }
        path = "Current path is: " + Integer.toString(current_path[current_index]);
        for(int i = current_index+1; current_path[i] != -1; i++)
            path += "->" + Integer.toString(current_path[i]);

        return path;
    }

    public int findVertex(int x, int y)
    {
        if(x >= 0 && x < 100 && y >= 0 && y < 100)
            return 6;
        if(x >= 100 && x < 200 && y >= 0 && y < 100)
            return 7;
        if(x >= 200 && x <= 300 && y >= 0 && y < 100)
            return 8;
        if(x >= 0 && x < 100 && y >= 100 && y < 200)
            return 3;
        if(x >= 100 && x < 200 && y >= 100 && y < 200)
            return 4;
        if(x >= 200 && x <= 300 && y >= 100 && y < 200)
            return 5;
        if(x >= 0 && x < 100 && y >= 200 && y <= 300)
            return 0;
        if(x >= 100 && x < 200 && y >= 200 && y <= 300)
            return 1;
        if(x >= 200 && x <= 300 && y >= 200 && y <= 300)
            return 2;
        return -1;
    }

    public void navigate(int x, int y){
        current_x = x;
        current_y = y;
        int temp_current_vertex = findVertex(x,y);

        if(current_destination == -1) {
            TextView t1 = (TextView) findViewById(R.id.textView1); // DEMONSTRATION
            t1.setText("No destination set!");
            current_vertex = temp_current_vertex;
            return;
        }

        if(current_vertex == temp_current_vertex) {
            TextView t1 = (TextView) findViewById(R.id.textView1); // DEMONSTRATION
            t1.setText("Same vertex as before!");
            return;
        }
        current_vertex = temp_current_vertex;
        if(current_path[current_index+1] == current_vertex) {
            TextView t1 = (TextView) findViewById(R.id.textView1); // DEMONSTRATION
            if(current_vertex == current_destination) {
                t1.setText("Arrival!");
                current_index = 0;
                current_destination = -1; // May want to clear index/dest/path
                current_path[0] = -1;
            }
            else {
                t1.setText("Progress!");
                current_index += 1;
                current_vertex = temp_current_vertex;
                return;
            }
        }
        else {
            setCurrentPath(current_vertex, current_destination);
            TextView t1 = (TextView) findViewById(R.id.textView1); // DEMONSTRATION
            t1.setText("Deviated from path!");
            return;
        }
    }

}
