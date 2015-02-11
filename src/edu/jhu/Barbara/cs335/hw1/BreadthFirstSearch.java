package edu.jhu.Barbara.cs335.hw1;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.Iterator;
import java.util.Stack;

/**
 * Created by Barbara on 2/10/2015.
 */
public class BreadthFirstSearch {
    public Stack<Entry> stax = new Stack<Entry>();
    private DataReader.Compass nav;
    private Entry start;
    private boolean reachedGoal = false;
    private String path;
    public GeneralPath line;
    private StringBuilder printout = new StringBuilder();
    private Integer totalCost = 0;
    private Integer nodesExpanded = 0;

    public BreadthFirstSearch(DataReader.Compass compass) {
        this.nav = compass;
    }

    private class Entry {
        private String value;
        private Entry parent;
        private DataReader.Coordinate coordinate;
        private int posX;
        private int posY;
        private int level;
        Entry (Integer x, Integer y, Integer i, Entry prev) {
            this.parent = prev;
            this.posX = x;
            this.posY = y;
            this.level = i;
            DataReader.Coordinate data = nav.getCurrent(x, y);
            this.value = data.data;
            this.coordinate = data;
        }
    }

    public void radialStack(Entry e) {
        if (!reachedGoal) {
            if (!e.coordinate.leftSearched) {
                Entry left = new Entry(e.posX - 1, e.posY, e.level + 1, e);
                left.coordinate.rightSearched = true;
                if (left.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(left));
                } else if (!left.value.equals("#")) {
                    this.stax.push(left);
                    nodesExpanded++;
                }
            }
            if (!e.coordinate.rightSearched) {
                Entry right = new Entry(e.posX + 1, e.posY, e.level + 1, e);
                right.coordinate.leftSearched = true;
                if (right.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(right));
                } else if (!right.value.equals("#")) {
                    this.stax.push(right);
                    nodesExpanded++;
                }
            }
            if (!e.coordinate.upSearched) {
                Entry up = new Entry(e.posX, e.posY - 1, e.level + 1, e);
                up.coordinate.downSearched = true;
                if (up.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(up));
                } else if (!up.value.equals("#")) {
                    this.stax.push(up);
                    nodesExpanded++;
                }
            }
            if (!e.coordinate.downSearched) {
                Entry down = new Entry(e.posX, e.posY + 1, e.level + 1, e);
                down.coordinate.upSearched = true;
                if (down.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(down));
                } else if (!down.value.equals("#")) {
                    this.stax.push(down);
                    nodesExpanded++;
                }
            }
            e.coordinate.leftSearched = e.coordinate.rightSearched = e.coordinate.upSearched = e.coordinate.downSearched = true;
            //Crucial to BFS vs. DFS:
            for (Iterator<Entry> it = this.stax.iterator(); it.hasNext();) {
                Entry current = it.next();
                /*System.out.println(current.value + " [" + current.posX + ", " + current.posY + "]\tLevel: "
                        + current.level + "\tBooleans: " + current.coordinate.leftSearched
                        + current.coordinate.rightSearched + current.coordinate.upSearched
                        + current.coordinate.downSearched);*/
                it.remove();
                radialStack(current);
            }
        }
    }

    private String printPath(Entry e) {
        //Calculate the total cost:
        if (e.value.equals(".")) {
            totalCost++;
        } else if (e.value.equals(",")) {
            totalCost += 2;
        }
        //Build the string detailing the path to the goal:
        this.printout.append(" >- ]" + e.posY + " ," + e.posX +"[ " + e.value);
        if (e.parent != this.start) {
            return printPath(e.parent);
        }
        this.printout.append(" >- ]" + e.parent.posY + " ," + e.parent.posX +"[ " + e.parent.value);
        this.printout.delete(0, 4);
        this.printout.reverse();
        totalCost++; //add one more cost value for the step into the goal
        this.printout.append("\nTotal Cost of Path: " + totalCost + "\nNumber of Nodes Expanded During Search: " + nodesExpanded);
        this.path = printout.toString();

        this.line = plotPath(e);

        return path;
    }

    public GeneralPath plotPath(Entry e) {
        GeneralPath path = new GeneralPath();

        path.moveTo(e.posX, e.posY);
        return drawLine(path, e);
    }

    private GeneralPath drawLine(GeneralPath line, Entry e) {
        if (e.parent != this.start) {
            line.lineTo(e.parent.posX, e.parent.posY);
            return drawLine(line, e.parent);
        }
        line.lineTo(e.parent.posX, e.parent.posY); //connect back to start
        return line;
    }

    public void findGoal() {
        this.start = new Entry(this.nav.startX, this.nav.startY, 0, null);
        radialStack(this.start);
    }
}