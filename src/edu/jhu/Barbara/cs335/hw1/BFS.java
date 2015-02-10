package edu.jhu.Barbara.cs335.hw1;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;

import javax.xml.crypto.Data;
import java.util.*;

/**
 * Created by Barbara on 2/9/2015.
 */
public final class BFS {
    private Compass nav;
    private String lastMove = "empty";
    private int totalCost = 0;
    private int extentOfSearch = 0;
    private boolean reachedGoal = false;
    private boolean branch = false;
    private ArrayList<Entry> path = new ArrayList<Entry>();
    private PriorityQueue<Entry> q = new PriorityQueue<Entry>(4, new Comparator<Entry>() {
        @Override
        public int compare(Entry e1, Entry e2) throws NullPointerException, ClassCastException {
            if (e1.cost < e2.cost) {
                return -1;
            } else if (e1.cost == e2.cost) {
                return 0;
            } else {
                return 1;
            }
        }
    });

    private class Entry {
        String value;
        Integer cost;
        String label;
        boolean branch;
        Boolean searched;
    }

    public BFS(Compass compass) {
        this.nav = compass;
    }

    public void assignCost (Entry e) {
        if (!e.searched) {
            if (e.value.equals(".")) {
                e.cost = 1;
                this.q.add(e);
            } else if (e.value.equals(",")) {
                e.cost = 2;
                this.q.add(e);
            } else if (e.value.equals("s")) {
                e.cost = 1; //do not add start back as an option
            } else if (e.value.equals("g")) {
                e.cost = 1;
                reachedGoal = true;
                this.q.add(e);
            } else {
                e.cost = 100; //large, arbitrary value to demonstrate impossibility of moving in that direction
            }
            this.extentOfSearch++;
        }
    }

    public void probeBreadth() {
        Entry left = new Entry();
        left.label = "left";
        left.value = nav.getLeft().data;
        left.searched = nav.getLeft().searched;
        assignCost(left);

        Entry right = new Entry();
        right.label = "right";
        right.value = nav.getRight().data;
        right.searched = nav.getRight().searched;
        assignCost(right);

        Entry up = new Entry();
        up.label = "up";
        up.value = nav.getUp().data;
        up.searched = nav.getUp().searched;
        assignCost(up);

        Entry down = new Entry();
        down.label = "down";
        down.value = nav.getDown().data;
        down.searched = nav.getDown().searched;
        assignCost(down);

        if (this.q.size() > 1) {
            this.branch = true;
        }

        this.path.add(cheapestStep(q.poll())); //finds cheapest step and implements
        this.q.clear(); //clears queue to receive new coordinates
        this.branch = false;
    }

    public void updateVariables(Entry cheap) {
        this.lastMove = cheap.label; //step is recorded for next iteration
        totalCost += cheap.cost;
    }

    public Entry cheapestStep(Entry cheap) {
        if (branch) {
            cheap.branch = true;
        }
        //Commits path to the cheapest cardinal direction:
        if (cheap.label.equals("left") && !cheap.searched && !lastMove.equals("right")) {
            this.nav.stepLeft();
            updateVariables(cheap);
            return cheap;
        } else if (cheap.label.equals("right") && !cheap.searched && !lastMove.equals("left")) {
            this.nav.stepRight();
            updateVariables(cheap);
            return cheap;
        } else if (cheap.label.equals("up") && !cheap.searched && !lastMove.equals("down")) {
            this.nav.stepUp();
            updateVariables(cheap);
            return cheap;
        } else if (cheap.label.equals("down") && !cheap.searched && !lastMove.equals("up")) {
            this.nav.stepDown();
            updateVariables(cheap);
            return cheap;
        } else {
            return cheapestStep(q.poll());
        }
    }

    public void calculatePath() {
        while(!reachedGoal) {
            try {
                probeBreadth(); //creates priority queue
                nav.currentPosition();
            }
            catch (NullPointerException x) {
                //We have found a dead-end path, so we must restart the process:
                for (ListIterator<Entry> it = this.path.listIterator(this.path.size()); it.hasPrevious(); ) {
                    Entry e = it.previous();
                    if (e.branch) {
                        this.branch = true; //recycle the branch boolean to designate when to start removing values
                        if (e.label.equals("left")) {
                            nav.stepRight();
                        } else if (e.label.equals("right")) {
                            nav.stepLeft();
                        } else if (e.label.equals("up")) {
                            nav.stepDown();
                        } else if (e.label.equals("down")) {
                            nav.stepUp();
                        }
                        nav.currentPosition();
                        it = this.path.listIterator(0); //we only want the first branch we come across
                    }
                    if (!this.branch) {
                        if (e.label.equals("left")) {
                            nav.stepRight();
                        } else if (e.label.equals("right")) {
                            nav.stepLeft();
                        } else if (e.label.equals("up")) {
                            nav.stepDown();
                        } else if (e.label.equals("down")) {
                            nav.stepUp();
                        }
                        nav.currentPosition();
                        it.remove();
                        totalCost--;
                    }
                }
                this.branch = false;
            }
        }


        //Lastly, we output the cheapest path:
        for (Entry e: this.path) {
            System.out.print(e.label + "(cost: " + e.cost + ") -> ");
        }
        System.out.print("goal (cost: 1) ~ Total Cost: " + totalCost + ", Extent of Search: " + extentOfSearch + " nodes expanded\n");
        this.nav.currentPosition();
    }
}
