package com.eureka.synanto.utility;

import android.util.Log;

import com.eureka.synanto.fragments.MapFragment;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Node {

    public int nodeID;
    public LatLng location;

    public Node parent;
    public ArrayList<Node> successors;

    private ArrayList<Integer> adjacencies;

    public double costF;
    public double costG;
    public double costH;

    public Node(int nodeID, LatLng location) {
        this.nodeID = nodeID;
        this.location = location;
        this.parent = null;
        this.successors = null;

        adjacencies = getAdjacencyList();
    }

    // Test
    public Node(LatLng location) {
        this.location = location;
        this.parent = null;
        this.successors = null;

        getNodeID();
        adjacencies = getAdjacencyList();

        Log.d("xxxAdjacency", adjacencies.size()+"");
    }

    // Test
    public Node(int id) {
        nodeID = id;
        this.parent = null;
        this.successors = null;

        adjacencies = getAdjacencyList();
        location = getLocationByID();
    }

    //Test
    private void getNodeID() {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(MapFragment.context.getAssets().open("gf-nodes.txt"), "UTF-8"));

            String line;
            while((line = br.readLine()) != null) {
                if (line.contains(getLatitude() + "," + getLongitude())) {
                    nodeID = Integer.parseInt(line.substring(1, line.indexOf(':')));
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Test
    private LatLng getLocationByID() {
        BufferedReader br = null;
        LatLng loc = null;

        try {
            br = new BufferedReader(new InputStreamReader(MapFragment.context.getAssets().open("gf-nodes.txt"), "UTF-8"));

            String line;
            while((line = br.readLine()) != null) {
                String temp = String.format("#%d:", nodeID);
                if (line.contains(temp)) {
                    loc = new LatLng(
                            Double.parseDouble(line.substring(line.indexOf(':')+1, line.indexOf(','))),
                            Double.parseDouble(line.substring(line.indexOf(',')+1, line.length()))
                    );
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return loc;
    }

    // Test
    public void generateSuccessors() {
        successors = new ArrayList<>();

        for (Integer i : adjacencies) {
             successors.add(new Node(i));
        }
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public double getLongitude() {
        return location.getLongitude();
    }

    public void generateSuccessors(ArrayList<Node> nodes) {
        successors = new ArrayList<>();

        for (Integer i : adjacencies) {
             successors.add(nodes.get(i-1));
        }
    }

    private ArrayList<Integer> getAdjacencyList() {

        ArrayList<Integer> adjList = new ArrayList<Integer>();
        ArrayList<Integer> spaces = new ArrayList<Integer>();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(MapFragment.context.getAssets().open("gf-adj.txt"), "UTF-8"));

            String line;
            String temp;

            while((line = br.readLine()) != null) {
                int lineID = Integer.parseInt(line.substring(1, line.indexOf(':')));

                if (nodeID == lineID) {
                    line = line.substring(line.indexOf(':') + 1, line.length());

                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == '-')
                            spaces.add(i);
                    }

                    int k = 0;

                    for (int i = 0; i < spaces.size(); i++) {
                        temp = line.substring(k, spaces.get(i));
                        adjList.add(Integer.parseInt(temp));
                        k = spaces.get(i) + 1;
                    }

                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return adjList;
    }

    public void reset() {
        parent = null;
        successors = null;
        costG = 0;
        costH = 0;
        costF = 0;
    }
}
