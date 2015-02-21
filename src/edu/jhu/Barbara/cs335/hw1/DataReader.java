package edu.jhu.Barbara.cs335.hw1;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/** CS335 ARTIFICIAL INTELLIGENCE - ASSIGNMENT 1
 * The DataReader class essentially recreates given map files in an ArrayList of ArrayLists format. The resulting map is
 * accessed for three different types of searches, Breadth First Search (BFS), Depth First Search (DFS), and ASTAR (A*).
 * Each search is implemented in a separate method, but shares a common print function.
 *
 * @author Barbara Holt
 */
public class DataReader {
    private Scanner scanner;
    public ArrayList<ArrayList<Coordinate>> mapData;
    private ArrayList<Coordinate> mapRow;
    private Stack<Coordinate> stax = new Stack<>();
    private LinkedList<Coordinate> q = new LinkedList<>();
    private PriorityQueue<Coordinate> priorityQ = new PriorityQueue<>(4, new Comparator<Coordinate>() {
        /**
         * This compare method overrides the default Comparator, allowing it to compare Coordinate objects by their
         *  stored values, summing the current distance from the goal (heuristic) and the distance from start
         *
         * @param coord1    The first coordinate passed to the Comparator
         * @param coord2    The second coordinate passed to the Comparator
         * @return          A value 1, 0, or -1, indicating if coord1 is greater, equal to, or less than coord2,
         *                  respectively
         *
         * @throws NullPointerException     Throws an exception if one coordinate object is equal to null, or contains
         *                                  a null value
         * @throws ClassCastException       Throws an exception if both objects passed to the Comparator aren't
         *                                  Coordinate objects as expected
         */
        @Override
        public int compare(Coordinate coord1, Coordinate coord2) throws NullPointerException, ClassCastException {
            if (coord1.currentTotalCost + coord1.distanceFromGoal < coord2.currentTotalCost + coord2.distanceFromGoal) {
                return -1;
            } else if (coord1.currentTotalCost + coord1.distanceFromGoal == coord2.currentTotalCost + coord2.distanceFromGoal) {
                return 0;
            } else {
                return 1;
            }
        }
    });
    private String[] firstLine;
    private String[] line;
    static int height;
    static int length;
    private int startX;
    private int startY;
    private int goalX;
    private int goalY;
    private int totalCost;
    private int nodesExpanded;
    private boolean found;
    private StringBuilder printout = new StringBuilder();
    private Coordinate start;

    /**
     * This simple method is called to create a new scanner when DataReader is instantiated.
     *
     * @param filename  String detailing the path to the file to be read
     * @throws FileNotFoundException    Throws exception if an invalid file-path is given
     */
    public DataReader(String filename) throws FileNotFoundException {
        this.scanner = new Scanner(new BufferedInputStream(new FileInputStream(filename)));
    }

    /**
     * This method closes the scanner, so no more files can be input.
     */
    public void closeScanner() {
        this.scanner.close();
    }

    /**
     * An object class to represent each value read in from the map files. Stored values track each coordinate's value,
     * position, distance from the goal, if this "node" has been expanded, and link to neighboring coordinates.
     */
    public class Coordinate {
        String data;
        int posX;
        int posY;
        int cost;
        int currentTotalCost = cost;
        double distanceFromGoal;
        boolean searched = false;
        Coordinate left;
        Coordinate right;
        Coordinate up;
        Coordinate down;
        Coordinate parent;
    }

    /**
     * This method creates an ArrayList of ArrayLists to represent the columns and rows of each map read. The first line
     * read in indicates the size of ArrayLists to be produced. Afterwards each value and its characteristics are added
     * to a new Coordinate object to be stored within the ArrayList, mapData.     *
     */
    public void readData() {
        this.mapData = new ArrayList<>();

        /**We read the first line, which contains the length and height of the map:*/
        this.firstLine = this.scanner.nextLine().split(" ");
        length = Integer.parseInt(firstLine[0]);
        height = Integer.parseInt(firstLine[1]);

        /**Next we iterate over the map characters:*/
        int j = 0;
        while (this.scanner.hasNext()) {
            /**Fills in y-coordinates of map:*/
            this.mapRow = new ArrayList<>();
            this.mapData.add(mapRow);

            /**Fills in x-coordinates of map:*/
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
                /**Calculates the total cost:*/
                if (coordinate.data.equals(".")|coordinate.data.equals("s")|coordinate.data.equals("g")) {
                    coordinate.currentTotalCost=coordinate.cost = 1;
                } else if (coordinate.data.equals(",")) {
                    coordinate.currentTotalCost=coordinate.cost = 2;
                }
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
        /**Fill in blank coordinates around boundary lines of map:*/
        for (ArrayList<Coordinate> row : mapData) {
            Coordinate coordinate = row.get(mapRow.size() - 1);
            coordinate.right = new Coordinate();
        }
        for (Coordinate coordinate : this.mapData.get(this.mapData.size() - 1)) {
            coordinate.down = new Coordinate();
        }
    }

    /**
     * This method computes the total cost of the goal-finding path, while building a string, which will be printed
     * horizontally with arrows dictating the route.
     *
     * @param coordinate    The goal-finding "node" in the form of a Coordinate object
     * @return  A printout retracing the path taken to the goal, the path's cost, and the number of nodes expanded in
     *          the search is returned.
     */
    private String printHorizontalPath(Coordinate coordinate) {
        totalCost++; /**Adds one cost value for the step into the goal*/
        this.printout.append(" >- ]" + this.goalY + " ," + this.goalX +"[ 'g'");
        do {
            /**Calculates the total cost:*/
            totalCost += coordinate.cost;
            /**Build the string detailing the path to the goal:*/
            this.printout.append(" >- ]" + coordinate.posY + " ," + coordinate.posX +"[ '" + coordinate.data + "'");
            coordinate = coordinate.parent;
        } while (coordinate.parent != null);
        this.printout.append(" >- ]" + coordinate.posY + " ," + coordinate.posX +"[ '" + coordinate.data + "'");
        this.printout.delete(0, 4);
        this.printout.reverse();

        /**Format the string to conform to a "margin":*/
        if (this.printout.length() > 70) {
            int printMargin = 1;
            while (this.printout.length() > 70*printMargin) {
                this.printout.insert(70*printMargin + (printMargin - 1), "\n");
                printMargin++;
            }
        }

        this.printout.append("\nTotal Cost of Path: " + totalCost + "\nNumber of Nodes Expanded During Search: "
                + nodesExpanded + "\n");
        return printout.toString();
    }

    /**
     * This method DOES NOT compute the total cost of the goal-finding path, while building a string. It simply reports
     * it within the string which will be printed horizontally with arrows dictating the route.
     *
     * @param coordinate    The goal-finding "node" in the form of a Coordinate object
     * @return  A printout retracing the path taken to the goal, the path's cost, and the number of nodes expanded in
     *          the search is returned.
     */
    private String printHorizontalPath(Coordinate coordinate, int totalCost) {
        this.printout.append(" >- ]" + this.goalY + " ," + this.goalX +"[ 'g'");
        do {
            /**Builds the string detailing the path to the goal:*/
            this.printout.append(" >- ]" + coordinate.posY + " ," + coordinate.posX +"[ '" + coordinate.data + "'");
            coordinate = coordinate.parent;
        } while (coordinate.parent != null);
        this.printout.append(" >- ]" + coordinate.posY + " ," + coordinate.posX +"[ '" + coordinate.data + "'");
        this.printout.delete(0, 4);
        this.printout.reverse();

        /**Formats the string to conform to a "margin":*/
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

    /**
     * This method computes the total cost of the goal-finding path, while building a string, which will be printed
     * vertically with arrows dictating the route.
     *
     * @param coordinate    The goal-finding "node" in the form of a Coordinate object
     * @return  A printout retracing the path taken to the goal, the path's cost, and the number of nodes expanded in
     *          the search is returned.
     */
    private String printVerticalPath(Coordinate coordinate) {
        String s = " [" + this.goalX + ", " + this.goalY + "] Goal\n^";
        totalCost ++; /**Adds one cost value for the step into the goal*/
        do {
            /**Calculates the total cost:*/
            totalCost += coordinate.cost;
            /**Builds the string detailing the path to the goal:*/
            s += "[" + coordinate.posX + ", " + coordinate.posY +"] '" + coordinate.data + "'\n^";
            coordinate = coordinate.parent;
        } while (coordinate.parent != null);
        s += "[" + this.startX + ", " + this.startY + "] Start\nTotal Cost of Path: " + totalCost
                + "\nNumber of Nodes Expanded During Search: " + nodesExpanded + "\n";
        return s;
    }

    /**
     * This method DOES NOT compute the total cost of the goal-finding path, while building a string. It simply reports
     * it within the string which will be printed vertically with arrows dictating the route.
     *
     * @param coordinate    The goal-finding "node" in the form of a Coordinate object
     * @return  A printout retracing the path taken to the goal, the path's cost, and the number of nodes expanded in
     *          the search is returned.
     */
    private String printVerticalPath(Coordinate coordinate, int totalCost) {
        String s = " [" + this.goalX + ", " + this.goalY + "] Goal\n^";
        do {
            /**Build the string detailing the path to the goal:*/
            s += "[" + coordinate.posX + ", " + coordinate.posY +"] '" + coordinate.data + "'\n^";
            coordinate = coordinate.parent;
        } while (coordinate.parent != this.start);
        s += "[" + this.startX + ", " + this.startY + "] Start\nTotal Cost of Path: " + totalCost
                + "\nNumber of Nodes Expanded During Search: " + nodesExpanded + "\n";
        return s;
    }

    /**
     * This method initiates a Breadth First Search, implemented with a LinkedList queue.
     *
     * @param vertical  This boolean indicates whether the user wants the printout vertical or horizontal
     * @throws InterruptedException Throws an exception when multiple threads are in use and one is interrupted.
     *                              This is not applicable to this program, because multiple threads aren't used.
     */
    public void BFS(boolean vertical) throws InterruptedException {
        long pre = System.nanoTime();
        this.found = false;
        this.nodesExpanded = 0;
        this.start = this.mapData.get(this.startY).get(this.startX);
        cardinalQueue(this.start);

        Coordinate current = new Coordinate();
        while (!this.found && !this.q.isEmpty()) {
            current = this.q.poll();
            cardinalQueue(current);
        }
        long post = System.nanoTime();
        if (this.found) {
            if (vertical) {
                System.out.println(printVerticalPath(current));
            } else {
                System.out.println(printHorizontalPath(current));
            }
        } else {
            System.out.println("No path to the goal was found!\tTotal Cost: \u221E" +
                    "\tNumber of Nodes Expanded During Search: " + nodesExpanded);
        }
        System.out.println("Runtime: " + (post - pre) / 1000000.0 + " ms");
    }

    /**
     * This queue looks in the four cardinal directions (left, right, up, down) for the goal. If found the search task
     * is complete, but if not the qualifying nodes (values other than #) will be added to the queue, and the process
     * repeated. The first coordinate added will be the first out.
     *
     * @param coordinate The current expanded node to evaluated along with its neighbors
     */
    public void cardinalQueue(Coordinate coordinate) {
        coordinate.searched = true;
        this.nodesExpanded++;
        if (!coordinate.left.data.equals("#")) {
            if (coordinate.left.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.left.searched) {
                coordinate.left.parent = coordinate;
                this.q.add(coordinate.left);
            }
        }
        if (!coordinate.right.data.equals("#")) {
            if (coordinate.right.data.equals("g")) {
                found = true;
            }
            if (!coordinate.right.searched) {
                coordinate.right.parent = coordinate;
                this.q.add(coordinate.right);
            }
        }
        if (!coordinate.up.data.equals("#")) {
            if (coordinate.up.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.up.searched) {
                coordinate.up.parent = coordinate;
                this.q.add(coordinate.up);
            }
        }
        if (!coordinate.down.data.equals("#")) {
            if (coordinate.down.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.down.searched) {
                coordinate.down.parent = coordinate;
                this.q.add(coordinate.down);
            }
        }
    }

    /**
     * This method initiates a Depth First Search, implemented with a stack.
     *
     * @param vertical  This boolean indicates whether the user wants the printout vertical or horizontal
     */
    public void DFS(boolean vertical) {
        long pre = System.nanoTime();
        this.found = false;
        this.nodesExpanded = 0;
        this.start = this.mapData.get(startY).get(startX);
        cardinalStack(this.start);

        Coordinate current = new Coordinate();
        while (!this.found && !this.stax.isEmpty()) {
            current = this.stax.pop();
            cardinalStack(current);
        }
        long post = System.nanoTime();
        if (this.found) {
            if (vertical) {
                System.out.println(printVerticalPath(current));
            } else {
                System.out.println(printHorizontalPath(current));
            }
        } else {
            System.out.println("No path to the goal was found!\tTotal Cost: \u221E" +
                "\tNumber of Nodes Expanded During Search: " + nodesExpanded);
        }
        System.out.println("Runtime: " + (post - pre) / 1000000.0 + " ms");
    }

    /**
     * This stack looks in the four cardinal directions (left, right, up, down) for the goal. If found the search task
     * is complete, but if not the qualifying nodes (values other than #) will be added to the stack, and the process
     * repeated. The first coordinate added will be the last one out.
     *
     * @param coordinate The current expanded node to evaluated along with its neighbors
     */
    public void cardinalStack(Coordinate coordinate) {
        coordinate.searched = true;
        this.nodesExpanded++;
        if (!coordinate.left.data.equals("#")) {
            if (coordinate.left.data.equals("g")) {
                found = true;
            }
            if (!coordinate.left.searched) {
                coordinate.left.parent = coordinate;
                this.stax.add(coordinate.left);
            }
        }
        if (!coordinate.right.data.equals("#")) {
            if (coordinate.right.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.right.searched) {
                coordinate.right.parent = coordinate;
                this.stax.add(coordinate.right);
            }
        }
        if (!coordinate.up.data.equals("#")) {
            if (coordinate.up.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.up.searched) {
                coordinate.up.parent = coordinate;
                this.stax.add(coordinate.up);
            }
        }
        if (!coordinate.down.data.equals("#")) {
            if (coordinate.down.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.down.searched) {
                coordinate.down.parent = coordinate;
                stax.add(coordinate.down);
            }
        }
    }

    /**
     * This method initiates an A* Search, implemented with a PriorityQueue. The PriorityQueue uses a custom
     * Comparator to sort nodes most likely to lead to the goal towards the front. A basic heuristic is also
     * added to "predict" likelihoods by calculating each coordinate's distance from the goal.
     *
     * @param vertical  This boolean indicates whether the user wants the printout vertical or horizontal
     */
    public void ASTAR(boolean vertical) {
        long pre = System.nanoTime();
        this.found = false;
        this.nodesExpanded = 0;
        this.start = this.mapData.get(startY).get(startX);
        priorityQueue(this.start);

        Coordinate current = new Coordinate();
        while (!this.found && !this.priorityQ.isEmpty()) {
            current = this.priorityQ.poll();
            priorityQueue(current);
        }
        long post = System.nanoTime();
        if (this.found) {
            if (vertical) {
                System.out.println(printVerticalPath(current, current.currentTotalCost));
            } else {
                System.out.println(printHorizontalPath(current, current.currentTotalCost));
            }
        } else {
            System.out.println("No path to the goal was found!\tTotal Cost: \u221E" +
                    "\tNumber of Nodes Expanded During Search: " + this.nodesExpanded);
        }
        System.out.println("Runtime: " + (post - pre) / 1000000.0 + " ms");
    }

    /**
     * This queue looks in the four cardinal directions (left, right, up, down) for the goal. If found the search task
     * is complete, but if not the qualifying nodes (values other than #) will be added to the queue, and the process
     * repeated. The most promising nodes will be removed first from the front. Therefore, the order of entry is not
     * significant.
     *
     * @param coordinate The current expanded node to evaluated along with its neighbors
     */
    public void priorityQueue(Coordinate coordinate) {
        coordinate.searched = true;
        this.nodesExpanded++;
        if (!coordinate.left.data.equals("#")) {
            if (coordinate.left.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.left.searched) {
                coordinate.left.parent = coordinate;
                coordinate.left.currentTotalCost += coordinate.currentTotalCost;
                this.priorityQ.add(coordinate.left);
            }
        }
        if (!coordinate.right.data.equals("#")) {
            if (coordinate.right.data.equals("g")) {
                found = true;
            }
            if (!coordinate.right.searched) {
                coordinate.right.parent = coordinate;
                coordinate.right.currentTotalCost += coordinate.currentTotalCost;
                this.priorityQ.add(coordinate.right);
            }
        }
        if (!coordinate.up.data.equals("#")) {
            if (coordinate.up.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.up.searched) {
                coordinate.up.parent = coordinate;
                coordinate.up.currentTotalCost += coordinate.currentTotalCost;
                this.priorityQ.add(coordinate.up);
            }
        }
        if (!coordinate.down.data.equals("#")) {
            if (coordinate.down.data.equals("g")) {
                this.found = true;
            }
            if (!coordinate.down.searched) {
                coordinate.down.parent = coordinate;
                coordinate.down.currentTotalCost += coordinate.currentTotalCost;
                this.priorityQ.add(coordinate.down);
            }
        }
    }
}
