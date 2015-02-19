package edu.jhu.Barbara.cs335.hw1;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    //private DrawingCanvas canvas;
//    public GeneralPath path;
//    public Main(GeneralPath line) {
//      this.path = line;
//    }
    public static String mapImage;
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner inputScan = new Scanner(System.in);
        System.out.println("/-----------------------------------------------------------------------------------/" +
                "\n\tPlease enter your desired map (1 - 8) as the first argument, and then your\n\tdesired search"
                + " algorithm (BFS, DFS, or A*) as the second argument.\n\n\t\tEX: '1 BFS' for MAP1.TXT and BREADTH" +
                "FIRST SEARCH\n/-----------------------------------------------------------------------------------/");
        String destination;
        DataReader dataInput = null;
        while (inputScan.hasNext()) {
            String input = inputScan.nextLine();
            String[] line = input.toLowerCase().split(" ");
            String num = line[0];
            int mapNum = Integer.parseInt(num);
            switch (mapNum) {
                case 1:
                    destination = "data/map1.txt";
                    break;
                case 2:
                    destination = "data/map2.txt";
                    break;
                case 3:
                    destination = "data/map3.txt";
                    break;
                case 4:
                    destination = "data/map4.txt";
                    break;
                case 5:
                    destination = "data/map5.txt";
                    break;
                case 6:
                    destination = "data/map6.txt";
                    break;
                case 7:
                    destination = "data/map7.txt";
                    break;
                case 8:
                    destination = "data/map8.txt";
                    break;
                default:
                    destination = "data/map1.txt";
                    break;
            }

            //Read and process input data maps:
            dataInput = new DataReader(destination, true);
            dataInput.readData();
            mapImage = dataInput.toString();

            String search = line[1];
            if (search.toUpperCase().equals("BFS")) {
                //Breadth First Search (BFS):
                System.out.println("BREADTH FIRST SEARCH (BFS):");
                long pre = System.nanoTime();
                dataInput.BFS();
                long post = System.nanoTime();
                System.out.println("Runtime: " + (post - pre) / 1000000.0 + " ms");
            } else if (search.toUpperCase().equals("DFS")) {
                //Depth First Search (DFS):
                System.out.println("\nDEPTH FIRST SEARCH (DFS):");
                long pre = System.nanoTime();
                dataInput.DFS();
                long post = System.nanoTime();
                System.out.println("Runtime: " + (post - pre) / 1000000.0 + " ms");
            } else if (search.toUpperCase().equals("A*")) {
                //A* Search (ASTAR):
                System.out.println("\nA* SEARCH (ASTAR):");
                long pre = System.nanoTime();
                AStarSearch searchThree = new AStarSearch(dataInput.compass()); //TESTING: ONLY GOOD FOR MAP1-MAP4
                searchThree.findGoal();
                long post = System.nanoTime();
                System.out.println("Runtime: " + (post - pre) / 1000000.0 + " ms");
            }
            //System.out.print(mapImage);
        }

        dataInput.closeScanner(); //end use of the map read earlier
        inputScan.close();
    }
}