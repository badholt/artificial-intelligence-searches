package edu.jhu.Barbara.cs335.hw1;

import java.util.ArrayList;
import java.util.InputMismatchException;

/**
 * Created by Barbara on 2/6/2015.
 */
public class Compass {
    public ArrayList<ArrayList<DataReader.Coordinate>> map;
    private int length;
    private int height;
    private int goalX;
    private int goalY;
    private int startX;
    private int startY;
    public int posX;
    public int posY;
    public boolean searched;

    public Compass(ArrayList<ArrayList<DataReader.Coordinate>> map, Integer length, Integer height, Integer startX, Integer startY, Integer goalX, Integer goalY) {
        this.map = map;
        this.length = length;
        this.height = height;
        this.goalX = goalX;
        this.goalY = goalY;
        this.startX = posX = startX;
        this.startY = posY = startY;
    }

    public void currentPosition() {
        System.out.println("\tposX: \t" + this.posX + " \tposY: \t" + this.posY);
    }

    public boolean searched(Integer givenX, Integer givenY) {
        if (this.map.get(givenX).get(givenY).searched) {
            searched = true;
        } else {
            searched = false;
        }
        return searched;
    }

    public DataReader.Coordinate getLeft() {
        return this.map.get(posY).get(posX - 1);
    }

    public DataReader.Coordinate getRight() {
        return this.map.get(posY).get(posX + 1);
    }

    public DataReader.Coordinate getUp() {
        return this.map.get(posY - 1).get(posX);
    }

    public DataReader.Coordinate getDown() {
        return this.map.get(posY + 1).get(posX);
    }

    public DataReader.Coordinate getCurrent(DataReader.Coordinate c) {
        return c;
    }

    public void stepLeft() {
        if (posX >= 0 && !getLeft().equals("#")) {
            --posX;
            this.map.get(posY).get(posX).searched = true;
        }
    }

    public void stepRight() {
        if (posX <= this.length && !getRight().equals("#")) {
            ++posX;
            this.map.get(posY).get(posX).searched = true;
        }
    }

    public void stepUp() {
        if (posY >= 0 && !getUp().equals("#")) {
            --posY;
            this.map.get(posY).get(posX).searched = true;
        }
    }

    public void stepDown() {
        if (posY <= this.height && !getDown().equals("#")) {
            ++posY;
            this.map.get(posY).get(posX).searched = true;
        }
    }
}
