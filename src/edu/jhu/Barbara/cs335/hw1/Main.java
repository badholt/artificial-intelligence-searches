package edu.jhu.Barbara.cs335.hw1;

import edu.jhu.Barbara.cs335.hw1.helperPackage.Helper2;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.Scanner;

public class Main extends JApplet {
    private DrawingCanvas canvas;
    public static String mapImage;
    public GeneralPath path;

    public Main(GeneralPath line) {
      this.path = line;
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame();

        //From scaffolding:
        Helper f = new Helper();
        f.setFoo(3);
        Helper2 b = new Helper2();
        b.setBar(5);

        Scanner inputScan = new Scanner(System.in);
        System.out.println("Please enter your desired map (1 - 8) as the first argument, and then\nyour desired search"
                + " algorithm (BFS, DFS, or A*) as the second argument.\n\nEX: '1 BFS' for MAP1.TXT and BREADTH FIRST SEARCH");
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
                BreadthFirstSearch searchOne = new BreadthFirstSearch(dataInput.compass()); //TESTING: ONLY GOOD FOR MAP1-MAP4 & MAP8
                searchOne.findGoal();
                Main map = new Main(searchOne.line);
                map.init();
                frame.getContentPane().add(map);
                frame.setDefaultCloseOperation(1);
                frame.setSize(250, 250);
                frame.setVisible(true);
            } else if (search.toUpperCase().equals("DFS")) {
                //Depth First Search (DFS):
                System.out.println("\nDEPTH FIRST SEARCH (DFS):");
                DepthFirstSearch searchTwo = new DepthFirstSearch(dataInput.compass()); //TESTING: ONLY GOOD FOR MAP1-MAP4
                searchTwo.findGoal();
                Main map2 = new Main(searchTwo.line);
                map2.init();
                frame.getContentPane().add(map2);
                frame.setDefaultCloseOperation(1);
                frame.setSize(250, 250);
                frame.setVisible(true);
            }
        }

        dataInput.refreshMap();
        dataInput.closeScanner(); //end use of the map read earlier
        inputScan.close();
    }

    public void init() {
        Container container = getContentPane();
        JPanel panel = new JPanel();
        canvas = new DrawingCanvas();
        container.add(canvas);
    }

    class DrawingCanvas extends Canvas {

        public DrawingCanvas() {
            setBackground(Color.white);
            setSize(50, 50);
        }

        public void paint(Graphics g) {
            g.setFont(new Font("Century Gothic MT", Font.PLAIN, 14));

            Graphics2D g2D = (Graphics2D) g;
            drawString(g2D, mapImage, 25, 5);
            g2D.setPaint(Color.red);
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2D.translate(30, 16);
            g2D.scale(20, 22);
            g2D.draw(path);
        }

        void drawString(Graphics g, String mapImage, int x, int y) {
            for (String line : mapImage.split("\n"))
                g.drawString(line, x, y += g.getFontMetrics().getHeight());
        }
    }
}