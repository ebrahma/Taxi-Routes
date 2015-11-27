import java.io.IOException;
import java.util.Scanner;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import java.util.PriorityQueue;


/**
 * Class to find k nearest taxis and return shortest path for 
 * selected taxi to reach user.
 * @author Emily, Hansin, Mariya
 */
public final class Taxis {
    /**
     * Number of arguments needed.
     */
    public static final int NUM_ARGS = 4;
    
    /**
     * Number for Checkstyle compliance.
     */
    public static final int INDEX_NUM = 3;
    
    /**
     * Constructor for Checkstyle compliance.
     */
    private Taxis() {
        
    }
    
    /**
     * Main method for finding k nearest taxis and returning shortest path for
     * selected taxi using Dijkstra's.
     * @param args user's input.
     * @throws IOException if insufficient arguments are input.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < NUM_ARGS) {
            throw new IllegalArgumentException("Three arguments are required.");
        }

        int k = Integer.parseInt(args[0]);
        String f1 = args[1];
        String f2 = args[2];
        String f3 = args[INDEX_NUM];
        Scanner kb = new Scanner(System.in);
        Scanner mapLocations = new Scanner(new FileReader(f1));
        Scanner mapConnections = new Scanner(new FileReader(f2));
        Scanner driverLocations = new Scanner(new FileReader(f3));
        ArrayList<String> mapLoc = new ArrayList<String>();
        ArrayList<Vertex> vert = new ArrayList<Vertex>();

        System.out.println("Collecting map locations...");
        
        while (mapLocations.hasNextLine()) {
            //get map locations and store in an array
            String location = mapLocations.nextLine().trim();
            mapLoc.add(location);
        }
        
        System.out.println("Collecting map connections...");
        Map<String, Vertex> myMap = new HashMap<String, Vertex>();


        while (mapConnections.hasNextLine()) {
            String connections = mapConnections.nextLine();
            int end1 = connections.indexOf(",");
            String name1 = connections.substring(1, end1);
            int end2 = connections.indexOf(")");
            String name2 = connections.substring(end1 + 2, end2);

            String w = connections.substring(end2 + 1);

            Scanner intFind = new Scanner(w);
            int weight = intFind.nextInt();

            //get map connections and get last integer
            if (myMap.get(name1) == null) {
                Vertex v1 = new Vertex(name1);
                Vertex v2 = new Vertex(name2);

                Edge e1 = new Edge(v2, weight);
                v1.adj.add(e1);
                myMap.put(name1, v1);
                vert.add(v1);

            } else {
                Vertex v2 = new Vertex(name2);
                Edge e1 = new Edge(v2, weight);
                myMap.get(name1).adj.add(e1);
            }


            if (myMap.get(name2) == null) {
                Vertex v1 = new Vertex(name2);
                Vertex v2 = new Vertex(name1);
                Edge e1 = new Edge(v2, weight);
                v1.adj.add(e1);
                myMap.put(name2, v1);
                vert.add(v1);
            } else {
                Vertex v2 = new Vertex(name1);
                Edge e1 = new Edge(v2, weight);
                myMap.get(name2).adj.add(e1);
            }
            intFind.close();
        }

        System.out.println("Collecting driver locations...");
        
        System.out.println("The map locations are: ");
        for (int i = 0; i < mapLoc.size(); i++) {
            //print out all the map locations
            System.out.println((i + 1) + ": " + mapLoc.get(i));
        }
        System.out.println();

        System.out.println("Enter number of recent "
                + "client pickup request location: ");
        int clientNum = kb.nextInt();
        String chosenName = mapLoc.get(clientNum - 1);
        chosenName = chosenName.trim();

        System.out.println();
        getPaths(myMap.get(chosenName), myMap);
        Map<String, Integer> driverMap = new HashMap<String, Integer>();
        Map<Integer, String> driverToLocation = new HashMap<Integer, String>();

        ArrayList<Vertex> driverLoc = new ArrayList<Vertex>();

        while (driverLocations.hasNextLine()) {
            int driverID = driverLocations.nextInt();
            String location = driverLocations.nextLine().trim();
            driverMap.put(location, driverID);
            driverLoc.add(myMap.get(location));
            driverToLocation.put(driverID, location);
            //get driver locations
        }

        Collections.sort(driverLoc);

    
        System.out.println("The " + k 
                + " drivers to alert about this pickup are:");
        for (int i = 0; i < k; i++) {
            System.out.println(driverMap.get(driverLoc.get(i).name) 
                    + ": " + driverLoc.get(i).name);
        }

        //print out the driver numbers and their locations
        System.out.println();
        System.out.println("Enter the ID number of the driver who responded: ");
        int driverID = kb.nextInt();
        String target = driverToLocation.get(driverID);

        System.out.println();
        System.out.println("The recommended route for driver " 
                + driverID + " is: ");

        ArrayList<Vertex> path = shortestPath(myMap.get(target));
            
        System.out.print("(");
        for (int i = 0; i < path.size() - 1; i++) {
            System.out.print(path.get(i).name + " -> ");
        }

        System.out.print(path.get(path.size() - 1).name);
        System.out.println(")");
        System.out.println("Expected total time: " 
                + myMap.get(target).minDistance);
        
        kb.close();
        mapConnections.close();
        mapLocations.close();
        driverLocations.close();            

    }

    /**
     * Finds paths via Dijkstra's algorithm.
     * @param start starting position.
     * @param myMap map of Strings relating to locations.
     */
    public static void getPaths(Vertex start, Map<String, Vertex> myMap) {
        start.minDistance = 0;
        PriorityQueue<Vertex> vertices = new PriorityQueue<Vertex>();
        vertices.add(start);

        while (!vertices.isEmpty()) {
            Vertex x = vertices.poll();
            for (Edge edge1 : x.adj) {
                Vertex vertex1 = myMap.get(edge1.next.name);
                int distance = x.minDistance + edge1.weight;

                if (distance < vertex1.minDistance) {
                    vertices.remove(vertex1);
                    vertex1.minDistance = distance;
                    vertex1.previous = x;
                    vertices.add(vertex1);
                }
            }
        }
    }
    
    /**
     * Assembles shortest path to target.
     * @param target the Vertex being targeted.
     * @return an ArrayList containing the shortest path to target.
     */
    public static ArrayList<Vertex> shortestPath(Vertex target) {
        ArrayList<Vertex> path = new ArrayList<Vertex>();
        for (Vertex v = target; v != null; v = v.previous) {
            path.add(v);
        }
        return path;
    }

    /**
     * Class for Vertex object that will correspond to physical locations.
     */
    public static final class Vertex implements Comparable<Vertex> {
        
        /**
         * String containing the name of the location.
         */
        String name;
        
        /**
         * ArrayList containing all Edges leaving this Vertex.
         */
        ArrayList<Edge> adj;
        
        /**
         * Distance to this Vertex.
         */
        Integer minDistance;
        
        /**
         * Previous Vertex in path.
         */
        Vertex previous;
        
        /**
         * Constructor for Vertex object.
         * @param n String containing the name of the location.
         */
        public Vertex(String n) {
            this.name = n;
            this.adj = new ArrayList<Edge>();
            this.minDistance = new Integer(Integer.MAX_VALUE);
            this.previous = null;
        }
        
        /**
         * Overrides compareTo method.
         * @param b Vertex to be compared to.
         * @return integer representing 
         * which Vertex has greater distance.
         */
        public int compareTo(Vertex b) {
            if (this.minDistance < b.minDistance) {
                return -1;
            } else if (this.minDistance > b.minDistance) {
                return 1;
            } 

            return 0;
        }
    }
    
    /**
     * Class for Edge object.
     */
    public static final class Edge {
        
        /**
         * Vertex that this Edge "points to".
         */
        Vertex next;
        
        /**
         * Weight of this Edge.
         */
        int weight;

        /**
         * Constructor for Edge object.
         * @param n Vertex Edge "points to".
         * @param w weight of this Edge.
         */
        public Edge(Vertex n, int w) {
            this.next = n;
            this.weight = w;
        }
    }

    


}