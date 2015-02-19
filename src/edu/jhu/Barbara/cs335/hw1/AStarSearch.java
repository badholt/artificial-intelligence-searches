package edu.jhu.Barbara.cs335.hw1;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by Barbara on 2/12/2015.
 */
public class AStarSearch {
    private DataReader.Compass nav;
    private StringBuilder printout;
    private String path;
    private int nodesExpanded;
    private int totalCost;
    private boolean reachedGoal;
    private Entry start;

    private PriorityQueue<Entry> q = new PriorityQueue<Entry>(4, new Comparator<Entry>() {
        @Override
        public int compare(Entry e1, Entry e2) throws NullPointerException, ClassCastException {
            if (e1.currentTotalCost + e1.distanceFromGoal < e2.currentTotalCost + e2.distanceFromGoal) {
                return -1;
            } else if (e1.currentTotalCost + e1.distanceFromGoal == e2.currentTotalCost + e2.distanceFromGoal) {
                return 0;
            } else {
                return 1;
            }
        }
    });

    public AStarSearch(DataReader.Compass compass) {
        this.nav = compass;
    }

    private class Entry {
        private DataReader.Coordinate coordinate;
        private String value;
        private Entry parent;
        private int cost;
        private int distanceFromGoal;
        private int currentTotalCost;
        private int posX;
        private int posY;
        private int level;
        boolean leftSearched = false; boolean rightSearched = false;
        boolean upSearched = false; boolean downSearched = false;

        Entry(Integer x, Integer y, Integer i, Entry prev) {
            this.parent = prev;
            this.posX = x;
            this.posY = y;
            this.currentTotalCost = 0;
            this.distanceFromGoal = (int) Math.sqrt(Math.abs(nav.goalX - this.posX) + Math.abs(nav.goalY - this.posY)); //heuristic?
            this.level = i;
            DataReader.Coordinate data = nav.getCurrent(x, y);
            this.coordinate = data;
            this.value = data.data;
            if (this.value.equals(".") | this.value.equals("g") | this.value.equals("s")) {
                this.cost = 1;
            } else if (this.value.equals(",")) {
                this.cost = 2;
            } else {
                this.cost = 100; //#'s receive an arbitrary, high number to represent the impossibility of the movement
            }
        }
    }

    public void cardinalEnqueue(Entry e) {
        if (!reachedGoal) {
            if (!e.leftSearched) {
                Entry left = new Entry(e.posX - 1, e.posY, e.level + 1, e);
                left.rightSearched = true;
                left.currentTotalCost = e.currentTotalCost + left.cost;
                nodesExpanded++;
                if (left.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(left));
                } else if (!left.value.equals("#")) {
                    this.q.add(e);
                    e.leftSearched = true;
                }
            }
            if (!e.rightSearched) {
                Entry right = new Entry(e.posX + 1, e.posY, e.level + 1, e);
                right.leftSearched = true;
                right.currentTotalCost = e.currentTotalCost + right.cost;
                nodesExpanded++;
                if (right.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(right));
                } else if (!right.value.equals("#")) {
                    this.q.add(e);
                    e.rightSearched = true;
                }
            }
            if (!e.upSearched) {
                Entry up = new Entry(e.posX, e.posY - 1, e.level + 1, e);
                up.downSearched = true;
                up.currentTotalCost = e.currentTotalCost + up.cost;
                nodesExpanded++;
                if (up.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(up));
                } else if (!up.value.equals("#")) {
                    this.q.add(e);
                    e.upSearched = true;
                }
            }
            if (!e.downSearched) {
                Entry down = new Entry(e.posX, e.posY + 1, e.level + 1, e);
                down.upSearched = true;
                down.currentTotalCost = e.currentTotalCost + down.cost;
                nodesExpanded++;
                if (down.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(down));
                } else if (!down.value.equals("#")) {
                    this.q.add(e);
                    e.downSearched = true;
                }
            }
            findBestStep();
        }
    }

    private void findBestStep() {
        if (this.q.size() > 0) {
            Entry chosen = this.q.poll();
            while (this.q.size() > 0) {
                this.q.poll(); //remove the rejects
            }
            cardinalEnqueue(chosen);
        }
        System.out.println("No path to the goal was found!\tTotal Cost: \u221E" +
                "\tNumber of Nodes Expanded During Search: " + nodesExpanded);
    }

    public void findGoal() {
        this.start = new Entry(this.nav.startX, this.nav.startY, 0, null);
        cardinalEnqueue(this.start);
    }

    private String printPath(Entry e) {
        //Calculate the total cost:
        if (e.value.equals(".")) {
            totalCost++;
        } else if (e.value.equals(",")) {
            totalCost += 2;
        }

        //Build the string detailing the path to the goal:
        this.printout.append(" >- ]" + e.posY + " ," + e.posX +"[ '" + e.value + "'");
        if (e.parent != start) {
            return printPath(e.parent);
        }
        this.printout.append(" >- ]" + e.parent.posY + " ," + e.parent.posX +"[ '" + e.parent.value + "'");
        this.printout.delete(0, 4);
        this.printout.reverse();
        totalCost++; //add one more cost value for the step into the goal

        //Format the string to conform to a "margin":
        if (this.printout.length() > 70) {
            int printMargin = 1;
            while (this.printout.length() > 70*printMargin + (printMargin - 1)) {
                this.printout.insert(70*printMargin + (printMargin - 1), "\n");
                printMargin++;
            }
        }

        this.printout.append("\nTotal Cost of Path: " + totalCost + "\nNumber of Nodes Expanded During Search: "
                + nodesExpanded + "\n");
        this.path = printout.toString();
        return path;
    }
}
