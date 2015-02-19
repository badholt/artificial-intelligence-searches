package edu.jhu.Barbara.cs335.hw1;
import sun.misc.Queue;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;


public class DataReader {
    private Scanner scanner;
    public ArrayList<ArrayList<Coordinate>> mapData;
    private ArrayList<Coordinate> mapRow;
    private Stack<Coordinate> stax = new Stack<Coordinate>();
    private Queue<Coordinate> q = new Queue<Coordinate>();
    private String[] firstLine;
    private String[] line;
    static int height;
    static int length;
    private int startX;
    private int startY;
    private int goalX;
    private int goalY;
    private boolean found;
    private int totalCost;
    private StringBuilder printout = new StringBuilder();
    private int nodesExpanded;
    Coordinate start;

    public DataReader(String filename, boolean classification) throws FileNotFoundException {
        this.scanner = new Scanner(new BufferedInputStream(new FileInputStream(filename)));
    }

    public void closeScanner() {
        this.scanner.close();
    }

    public class Coordinate {
        String data;
        int posX;
        int posY;
        boolean searched = false;
        Coordinate left;
        Coordinate right;
        Coordinate up;
        Coordinate down;
        Coordinate parent;

        public String toString() {
            return "Parent: " + parent.data + " For " + data + " At [" + posX + ", " + posY + "]";
        }
    }

    public class Compass {
        ArrayList<ArrayList<DataReader.Coordinate>> map; int startX; int startY; int goalX; int goalY; int height; int length;

        public Compass (ArrayList<ArrayList<DataReader.Coordinate>> map, int startX, int startY, int goalX, int goalY) {
            this.map = map;
            this.goalX = goalX;
            this.goalY = goalY;
            this.height = DataReader.height;
            this.length = DataReader.length;
            this.startX = startX;
            this.startY = startY;
        }

        public Coordinate getCurrent(int posX, int posY) {
            return this.map.get(posY).get(posX);
        }
    }

    public Compass compass() {
        Compass guide = new Compass(this.mapData, this.startX, this.startY, this.goalX, this.goalY);
        return guide;
    }

    public void readData() {
        this.mapData = new ArrayList<ArrayList<Coordinate>>();

        //We read the first line, which contains the length and height of the map:
        this.firstLine = this.scanner.nextLine().split(" ");
        this.length = Integer.parseInt(firstLine[0]);
        this.height = Integer.parseInt(firstLine[1]);

        //Next we iterate over the map characters:
        int j = 0;
        while (this.scanner.hasNext()) {
            //Fills in y-coordinates of map:
            this.mapRow = new ArrayList<Coordinate>();
            this.mapData.add(mapRow);

            //Fills in x-coordinates of map:
            this.line = this.scanner.next().split("");
            for (int i = 0; i < this.line.length; i++) {
                if ("s".equals(this.line[i].toLowerCase())) {
                    this.startX = i;
                    this.startY = mapData.indexOf(mapRow);
                } else if ("g".equals(this.line[i].toLowerCase())) {
                    this.goalX = i;
                    this.goalY = mapData.indexOf(mapRow);
                }
                Coordinate coordinate = new Coordinate();
                coordinate.posX = i;
                coordinate.posY = j;
                coordinate.data = this.line[i];
                if (this.mapRow.size() > 0) {
                    coordinate.left = this.mapRow.get(i - 1);
                    coordinate.left.right = coordinate;
                } else {
                    coordinate.left = new Coordinate();
                }
                if (this.mapData.size() > 1) {
                    coordinate.up = this.mapData.get(this.mapData.size() - 2).get(i);
                    coordinate.up.down = coordinate;
                } else {
                    coordinate.up = new Coordinate();
                }
                this.mapRow.add(coordinate);
            }
            j++;
        }
        //Fill in blank coordinates around boundary lines of map:
        for (ArrayList<Coordinate> row : mapData) {
            Coordinate coordinate = row.get(mapRow.size() - 1);
            coordinate.right = new Coordinate();
        }
        for (Coordinate coordinate : this.mapData.get(this.mapData.size() - 1)) {
            coordinate.down = new Coordinate();
        }
    }

    public String toString() {
        String s = "";
        for (ArrayList<Coordinate> row : this.mapData) {
            for (Coordinate coordinate : row) {
                if (coordinate.data.equals(".")|coordinate.data.equals(",")) {
                    s += coordinate.data + "\t";
                } else {
                    s += coordinate.data + "\t";
                }
            }
            s += "\n";
        }
        return s;
    }

    private String printPath(Coordinate e) {
        totalCost++; //add one cost value for the step into the goal
        this.printout.append(" >- ]" + this.goalY + " ," + this.goalX +"[ 'g'");
        do {
            //Calculate the total cost:
            if (e.data.equals(".")) {
                totalCost++;
            } else if (e.data.equals(",")) {
                totalCost += 2;
            }
            //Build the string detailing the path to the goal:
            this.printout.append(" >- ]" + e.posY + " ," + e.posX +"[ '" + e.data + "'");
            e = e.parent;
        } while (e.parent != null);
        this.printout.append(" >- ]" + e.posY + " ," + e.posX +"[ '" + e.data + "'");
        this.printout.delete(0, 4);
        this.printout.reverse();

        //Format the string to conform to a "margin":
        if (this.printout.length() > 70) {
            int printMargin = 1;
            while (this.printout.length() > 70*printMargin) {
                this.printout.insert(70*printMargin + (printMargin - 1), "\n");
                printMargin++;
            }
        }

        this.printout.append("\nTotal Cost of Path: " + totalCost + "\nNumber of Nodes Expanded During Search: " + nodesExpanded + "\n");
        return printout.toString();
    }

    public void BFS() throws InterruptedException {
        this.found = false;
        this.nodesExpanded = 0;
        this.start = this.mapData.get(this.startY).get(this.startX);
        cardinalQueue(this.start);

        Coordinate current = new Coordinate();
        while (!this.found) {
            current = this.q.dequeue();
            cardinalQueue(current);
        }
        System.out.println(printPath(current));
    }

    public void cardinalQueue(Coordinate coordinate) {
        coordinate.searched = true;
        this.nodesExpanded++;
        if (!coordinate.left.data.equals("#")) {
            if (coordinate.left.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.left.searched) {
                coordinate.left.parent = coordinate;
                this.q.enqueue(coordinate.left);
            }
        }
        if (!coordinate.right.data.equals("#")) {
            if (coordinate.right.data.equals("g")) {
                found = true;
            }
            if (!coordinate.right.searched) {
                coordinate.right.parent = coordinate;
                this.q.enqueue(coordinate.right);
            }
        }
        if (!coordinate.up.data.equals("#")) {
            if (coordinate.up.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.up.searched) {
                coordinate.up.parent = coordinate;
                this.q.enqueue(coordinate.up);
            }
        }
        if (!coordinate.down.data.equals("#")) {
            if (coordinate.down.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.down.searched) {
                coordinate.down.parent = coordinate;
                this.q.enqueue(coordinate.down);
            }
        }
    }

    public void DFS() {
        this.found = false;
        this.nodesExpanded = 0;
        this.start = this.mapData.get(startY).get(startX);
        cardinalStack(this.start);

        Coordinate current = new Coordinate();
        while (!this.found) {
            current = this.stax.pop();
            cardinalStack(current);
        }
        System.out.println(printPath(current));
    }

    public void cardinalStack(Coordinate coordinate) {
        coordinate.searched = true;
        this.nodesExpanded++;
        if (!coordinate.left.data.equals("#")) {
            if (coordinate.left.data.equals("g")) {
                found = true;
            }
            if (!coordinate.left.searched) {
                coordinate.left.parent = coordinate;
            }
            this.stax.add(coordinate.left);
        }
        if (!coordinate.right.data.equals("#")) {
            if (coordinate.right.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.right.searched) {
                coordinate.right.parent = coordinate;
            }
            this.stax.add(coordinate.right);
        }
        if (!coordinate.up.data.equals("#")) {
            if (coordinate.up.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.up.searched) {
                coordinate.up.parent = coordinate;
            }
            this.stax.add(coordinate.up);
        }
        if (!coordinate.down.data.equals("#")) {
            if (coordinate.down.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.down.searched) {
                coordinate.down.parent = coordinate;
            }
            stax.add(coordinate.down);
        }
        if (this.stax.size() == 0) {

        }
    }
}
