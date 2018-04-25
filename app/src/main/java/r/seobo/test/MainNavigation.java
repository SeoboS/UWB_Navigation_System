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

    public String stepToString(){
        String path = "No current path!";
        if(current_path[current_index] == -1) {
            return path;
        }
        int int1 = current_path[current_index];         //current vertex
        if(current_path[current_index+1] == -1) {
            return path;
        }
        int int2 = current_path[current_index+1];       //next vertex

        switch(int1) {
            case 0: path = "Exit Room 103. ";
                break;
            case 1: path = "Exit Room 105. ";
                break;
            case 2: path = "Exit Room 107. ";
                break;
            case 3: path = "Exit Room 109. ";
                break;
            case 4: path = "Exit Room 111. ";
                break;
            case 5: path = "Exit Room 113. ";
                break;
            case 6: path = "Exit Room 115. ";
                break;
            case 7: path = "Exit Room 117. ";
                break;
            case 8: path = "Exit Room 119. ";
                break;
            case 9: path = "Exit Room 102. ";
                break;
            case 10: path = "Exit Room 104. ";
                break;
            case 11: path = "Exit Room 106. ";
                break;
            case 12: path = "Exit Room 108. ";
                break;
            case 13: path = "Exit Room 110. ";
                break;
            case 14: path = "Exit Room 112. ";
                break;
            case 15: path = "Exit Room 114. ";
                break;
            case 16: path = "Exit Room 116. ";
                break;
            case 17: path = "Exit Room 118. ";
                break;
            case 18: path = "Exit Room 120. ";
                break;
            case 19: path = "Exit Room 122. ";
                break;
            case 20: path = "Exit Room 124. ";
                break;
            case 21: path = "Exit Room 126. ";
                break;
            case 22: path = "Exit Room 128. ";
                break;
            case 23: path = "Exit Room 130. ";
                break;
            case 24: path = "Exit Room 132. ";
                break;
            case 25: path = "Exit Room 134a. ";
                break;
            case 26: path = "Exit Room 134b. ";
                break;
            case 27: path = "Exit men's restroom. ";
                break;
            case 28: path = "Exit women's restroom. ";
                break;
            case 29: path = "Enter building through exit 1. ";
                break;
            case 30: path = "Enter building through exit 2. ";
                break;
            case 31: path = "Exit stairwell 1. ";
                break;
            case 32: path = "Exit stairwell 2. ";
                break;
            case 33: path = "";
                break;
            case 34: path = "";
                break;
            case 35: path = "";
                break;
            case 36: path = "";
                break;
            case 37: path = "Continue past door. ";
                break;
            case 38: path = "Continue past door. ";
                break;
            case 39: path = "Continue past door. ";
                break;
            case 40: path = "Continue past door. ";
                break;
            default: path = "";
        };

        switch(int2) {
            case 0: path += "Continue to Room 103.";
                break;
            case 1: path += "Continue to Room 105.";
                break;
            case 2: path += "Continue to Room 107.";
                break;
            case 3: path += "Continue to Room 109.";
                break;
            case 4: path += "Continue to Room 111.";
                break;
            case 5: path += "Continue to Room 113.";
                break;
            case 6: path += "Continue to Room 115.";
                break;
            case 7: path += "Continue to Room 117.";
                break;
            case 8: path += "Continue to Room 119.";
                break;
            case 9: path += "Continue to Room 102.";
                break;
            case 10: path += "Continue to Room 104.";
                break;
            case 11: path += "Continue to Room 106.";
                break;
            case 12: path += "Continue to Room 108.";
                break;
            case 13: path += "Continue to Room 110.";
                break;
            case 14: path += "Continue to Room 112.";
                break;
            case 15: path += "Continue to Room 114.";
                break;
            case 16: path += "Continue to Room 116.";
                break;
            case 17: path += "Continue to Room 118.";
                break;
            case 18: path += "Continue to Room 120.";
                break;
            case 19: path += "Continue to Room 122.";
                break;
            case 20: path += "Continue to Room 124.";
                break;
            case 21: path += "Continue to Room 126.";
                break;
            case 22: path += "Continue to Room 128.";
                break;
            case 23: path += "Continue to Room 130.";
                break;
            case 24: path += "Continue to Room 132.";
                break;
            case 25: path += "Continue to Room 134a.";
                break;
            case 26: path += "Continue to Room 134b.";
                break;
            case 27: path += "Continue to men's restroom.";
                break;
            case 28: path += "Continue to women's restroom.";
                break;
            case 29: path += "Exit building through exit 1.";
                break;
            case 30: path += "Exit building through exit 2.";
                break;
            case 31: path += "Enter stairwell 1.";
                break;
            case 32: path += "Enter stairwell 2.";
                break;
            case 33: path += "Continue to corner of hallway";
                break;
            case 34: path += "Continue to corner of hallway";
                break;
            case 35: path += "Continue to corner of hallway";
                break;
            case 36: path += "Continue to corner of hallway";
                break;
            case 37: path += "Continue towards hallway door.";
                break;
            case 38: path += "Continue towards hallway door.";
                break;
            case 39: path += "Continue towards hallway door.";
                break;
            case 40: path += "Continue towards hallway door.";
                break;
            default: path += "";
        };

        return path;
    }

    public String hallStepToString(){
        String path = "No current path!";
        if(current_path[current_index] == -1) {
            return path;
        }
        if(current_path[current_index+1] == -1) {
            return path;
        }
        int int2 = current_path[current_index+1];       //next vertex

        path = "";
        switch(int2) {
            case 0: path += "Continue to Room 103.";
                break;
            case 1: path += "Continue to Room 105.";
                break;
            case 2: path += "Continue to Room 107.";
                break;
            case 3: path += "Continue to Room 109.";
                break;
            case 4: path += "Continue to Room 111.";
                break;
            case 5: path += "Continue to Room 113.";
                break;
            case 6: path += "Continue to Room 115.";
                break;
            case 7: path += "Continue to Room 117.";
                break;
            case 8: path += "Continue to Room 119.";
                break;
            case 9: path += "Continue to Room 102.";
                break;
            case 10: path += "Continue to Room 104.";
                break;
            case 11: path += "Continue to Room 106.";
                break;
            case 12: path += "Continue to Room 108.";
                break;
            case 13: path += "Continue to Room 110.";
                break;
            case 14: path += "Continue to Room 112.";
                break;
            case 15: path += "Continue to Room 114.";
                break;
            case 16: path += "Continue to Room 116.";
                break;
            case 17: path += "Continue to Room 118.";
                break;
            case 18: path += "Continue to Room 120.";
                break;
            case 19: path += "Continue to Room 122.";
                break;
            case 20: path += "Continue to Room 124.";
                break;
            case 21: path += "Continue to Room 126.";
                break;
            case 22: path += "Continue to Room 128.";
                break;
            case 23: path += "Continue to Room 130.";
                break;
            case 24: path += "Continue to Room 132.";
                break;
            case 25: path += "Continue to Room 134a.";
                break;
            case 26: path += "Continue to Room 134b.";
                break;
            case 27: path += "Continue to men's restroom.";
                break;
            case 28: path += "Continue to women's restroom.";
                break;
            case 29: path += "Exit building through exit 1.";
                break;
            case 30: path += "Exit building through exit 2.";
                break;
            case 31: path += "Enter stairwell 1.";
                break;
            case 32: path += "Enter stairwell 2.";
                break;
            case 33: path += "Continue to corner of hallway";
                break;
            case 34: path += "Continue to corner of hallway";
                break;
            case 35: path += "Continue to corner of hallway";
                break;
            case 36: path += "Continue to corner of hallway";
                break;
            case 37: path += "Continue towards hallway door.";
                break;
            case 38: path += "Continue towards hallway door.";
                break;
            case 39: path += "Continue towards hallway door.";
                break;
            case 40: path += "Continue towards hallway door.";
                break;
            default: path += "";
        };

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
        if(temp_current_vertex >40)
            return 6;
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
