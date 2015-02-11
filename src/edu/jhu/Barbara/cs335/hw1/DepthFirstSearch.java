package edu.jhu.Barbara.cs335.hw1;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.Stack;

/**
 * Created by Barbara on 2/10/2015.
 */
public class DepthFirstSearch {
    public Stack<Entry> stax = new Stack<Entry>();
    private DataReader.Compass nav;
    private Entry start;
    private boolean reachedGoal = false;
    private String path;
    private StringBuilder printout = new StringBuilder();
    private Integer totalCost = 0;
    private Integer nodesExpanded = 0;
    public GeneralPath line;

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

    public DepthFirstSearch(DataReader.Compass compass) {
        this.nav = compass;
    }

    public void directStack(Entry e) {
        if (!reachedGoal) {
            if (!e.coordinate.leftSearched) {
                Entry left = new Entry(e.posX - 1, e.posY, e.level + 1, e);
                left.coordinate.rightSearched = true;
                nodesExpanded++;
                if (left.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(left));
                    this.line = plotPath(e);
                } else if (!left.value.equals("#")) {
                    this.stax.push(left);
                    e.coordinate.leftSearched = true;
                    directStack(left);
                }
            }
            if (!e.coordinate.rightSearched) {
                Entry right = new Entry(e.posX + 1, e.posY, e.level + 1, e);
                right.coordinate.leftSearched = true;
                nodesExpanded++;
                if (right.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(right));
                    this.line = plotPath(e);
                } else if (!right.value.equals("#")) {
                    this.stax.push(right);
                    e.coordinate.rightSearched = true;
                    directStack(right);
                }
            }
            if (!e.coordinate.upSearched) {
                Entry up = new Entry(e.posX, e.posY - 1, e.level + 1, e);
                up.coordinate.downSearched = true;
                nodesExpanded++;
                if (up.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(up));
                    this.line = plotPath(e);
                } else if (!up.value.equals("#")) {
                    this.stax.push(up);
                    e.coordinate.upSearched = true;
                    directStack(up);
                }
            }
            if (!e.coordinate.downSearched) {
                Entry down = new Entry(e.posX, e.posY + 1, e.level + 1, e);
                down.coordinate.upSearched = true;
                nodesExpanded++;
                if (down.value.toLowerCase().equals("g")) {
                    this.reachedGoal = true;
                    System.out.println(printPath(down));
                    this.line = plotPath(e);
                } else if (!down.value.equals("#")) {
                    this.stax.push(down);
                    e.coordinate.downSearched = true;
                    directStack(down);
                }
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
        if (e.parent != start) {
            return printPath(e.parent);
        }
        this.printout.append(" >- ]" + e.parent.posY + " ," + e.parent.posX +"[ " + e.parent.value);
        this.printout.delete(0, 4);
        this.printout.reverse();
        totalCost++; //add one more cost value for the step into the goal
        this.printout.append("\nTotal Cost of Path: " + totalCost + "\nNumber of Nodes Expanded During Search: " + nodesExpanded);
        this.path = printout.toString();
        return path;
    }

    public GeneralPath plotPath(Entry e) {
        GeneralPath line = new GeneralPath();

        line.moveTo(e.posX, e.posY);
        return drawLine(line, e);
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
        directStack(this.start);
    }
}
