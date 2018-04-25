package r.seobo.test;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;

import static r.seobo.test.Constants.ECE_GRAPH;

public class MainNavigation implements Serializable{

    private
        int pSize = 41;
        int [] current_path = new int[41];
        int current_index = 0;
        int current_x = 0;
        int current_y = 0;
        int current_vertex = -1;
        int current_destination = -1;
        final int[][] graph = ECE_GRAPH;


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
        if(x >= 0 && x < 1000 && y >= 0 && y < 800)
            return 0;
        if(x >= 0 && x < 1000 && y >= 880 && y < 1500)
            return 1;
        if(x >= 0 && x < 1000 && y >= 15 && y < 1900)
            return 2;
        if(x >= 0 && x < 1000 && y >= 1900 && y < 2800)
            return 3;
        if(x >= 0 && x < 400 && y >= 2800 && y < 3100)
            return 4;
        if(x >= 0 && x < 400 && y >= 3100 && y < 3400)
            return 5;
        if(x >= 0 && x < 400 && y >= 3400 && y < 3800)
            return 6;
        if(x >= 0 && x < 400 && y >= 3800 && y < 4400)
            return 7;
        if(x >= 600 && x < 1000 && y >= 3000 && y < 3600)
            return 8;
        if(x >= 1500 && x < 2100 && y >= 1100 && y < 1400)
            return 9;
        if(x >= 1500 && x < 2100 && y >= 1400 && y < 1800)
            return 10;
        if(x >= 1500 && x < 2100 && y >= 1800 && y < 2200)
            return 11;
        if(x >= 1500 && x < 2100 && y >= 2200 && y < 2600)
            return 12;
        if(x >= 1500 && x < 2100 && y >= 2600 && y < 3100)
            return 13;
        if(x >= 1500 && x < 2100 && y >= 3100 && y < 3600)
            return 14;
        if(x >= 2400 && x < 3000 && y >= 200 && y < 500)
            return 15;
        if(x >= 2400 && x < 3000 && y >= 500 && y < 800)
            return 16;
        if(x >= 2400 && x < 3000 && y >= 800 && y < 1100)
            return 17;
        if(x >= 2400 && x < 3000 && y >= 1100 && y < 1400)
            return 18;
        if(x >= 2400 && x < 3000 && y >= 1400 && y < 1800)
            return 19;
        if(x >= 2400 && x < 3000 && y >= 1800 && y < 2200)
            return 20;
        if(x >= 2400 && x < 3000 && y >= 2200 && y < 2500)
            return 21;
        if(x >= 2400 && x < 3000 && y >= 2500 && y < 2800)
            return 22;
        if(x >= 2400 && x < 3000 && y >= 2800 && y < 3200)
            return 23;
        if(x >= 2400 && x < 3000 && y >= 3200 && y < 3600)
            return 24;
        if(x >= 1500 && x < 2100 && y >= 4000 && y < 4400)
            return 25;
        if(x >= 2400 && x < 3000 && y >= 4000 && y < 4400)
            return 26;
        if(x >= 1500 && x < 2100 && y >= 800 && y < 1100)
            return 27;
        if(x >= 1600 && x < 2100 && y >= 500 && y < 800)
            return 28;
        if(x >= 1000 && x < 1400 && y >= 4000 && y < 4400)
            return 29;
        if(x >= 1200 && x < 1500 && y >= 0 && y < 400)
            return 30;
        if(x >= 2400 && x < 3000 && y >= 0 && y < 200)
            return 31;
        if(x >= 0 && x < 1000 && y >= 4400 && y < 4800)
            return 32;
        if(x >= 1000 && x < 1500 && y >= 400 && y < 700)
            return 33;
        if(x >= 1000 && x < 1500 && y >= 3600 && y < 4000)
            return 34;
        if(x >= 2100 && x < 3000 && y >= 3600 && y < 4000)
            return 35;
        if(x >= 2000 && x < 2400 && y >= 200 && y < 500)
            return 36;
        if(x >= 600 && x < 1000 && y >= 2800 && y < 3000)
            return 37;
        if(x >= 600 && x < 1000 && y >= 3600 && y < 3800)
            return 38;
        if(x >= 1500 && x < 2000 && y >= 3600 && y < 4000)
            return 39;
        if(x >= 1600 && x < 2000 && y >= 200 && y < 500)
            return 40;
        if(x >= 0 && x < 3000 && y >= 0 && y < 4400)
            return 41;
        return -1;
    }

    public int navigate(int x, int y){
        current_x = x;
        current_y = y;
        int temp_current_vertex = findVertex(x,y);
        if(current_vertex >40)
            return 2;
        if(current_destination == -1) {
            current_vertex = temp_current_vertex;
            return 1;
        }

        if(current_vertex == temp_current_vertex) {
            return 2;
        }
        current_vertex = temp_current_vertex;
        if(current_path[current_index+1] == current_vertex) {
            if(current_vertex == current_destination) {
                current_index = 0;
                current_destination = -1;
                current_path[0] = -1;
                return 3;
            }
            else {
                current_index += 1;
                current_vertex = temp_current_vertex;
                return 4;
            }
        }
        else {
            setCurrentPath(current_vertex, current_destination);
            return 5;
        }
    }

}
