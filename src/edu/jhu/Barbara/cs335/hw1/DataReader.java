package edu.jhu.Barbara.cs335.hw1;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class DataReader {

    private Scanner scanner;
    private ArrayList<ArrayList<Coordinate>> mapData;
    private ArrayList<Coordinate> mapRow;
    private String[] firstLine;
    private String[] line;
    public int height;
    public int length;
    public int startX;
    public int startY;
    public int goalX;
    public int goalY;

    public DataReader(String filename, boolean classification) throws FileNotFoundException {
        this.scanner = new Scanner(new BufferedInputStream(new FileInputStream(filename)));
    }

    public void close() {
        this.scanner.close();
    }

    public class Coordinate {
        String data;
        int posX;
        int posY;
        boolean searched = false;
    }

    public ArrayList<ArrayList<Coordinate>> readData() {
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

        return this.mapData;
    }
}
