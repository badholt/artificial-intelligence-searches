package edu.jhu.Barbara.cs335.hw1;
import javax.xml.crypto.Data;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;


public class DataReader {
    private Scanner scanner;
    private ArrayList<ArrayList<Coordinate>> mapData;
    private ArrayList<Coordinate> mapRow;
    private String[] firstLine;
    private String[] line;
    static int height;
    static int length;
    private int startX;
    private int startY;
    private int goalX;
    private int goalY;

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
        boolean leftSearched = false; boolean rightSearched = false;
        boolean upSearched = false; boolean downSearched = false;

        public void refresh() {
            this.leftSearched = this.rightSearched = this.upSearched = this.downSearched = false;
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
                this.mapRow.add(coordinate);
            }
            j++;
        }
    }

    public void refreshMap () {
        for (ArrayList<Coordinate> row : this.mapData) {
            for (Coordinate coordinate : row) {
                coordinate.refresh();
            }
        }
    }

    public String toString() {
        String s = "";
        for (ArrayList<Coordinate> row : this.mapData) {
            for (Coordinate coordinate : row) {
                if (coordinate.data.equals(".")|coordinate.data.equals(",")) {
                    s += " " + coordinate.data + "   ";
                } else {
                    s += coordinate.data + "   ";
                }
            }
            s += "\n";
        }
        return s;
    }
}
