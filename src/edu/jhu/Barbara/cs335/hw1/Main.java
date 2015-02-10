package edu.jhu.Barbara.cs335.hw1;

import edu.jhu.Barbara.cs335.hw1.helperPackage.Helper2;

import java.io.IOException;
import java.util.ArrayList;

public class Main
{
  /**
   * A simple test program.
   * <p>
   * This program simply prints Hello World to stdout.
   * It also demonstrates proper package use.
   * 
   *
   * @return void Nothing error-code is return. 
   */
  public static void main(String[] args) throws IOException
  {
      Helper f = new Helper();
      f.setFoo(3);
      Helper2 b = new Helper2();
      b.setBar(5);

      DataReader dataInput = new DataReader("data/map1.txt", true);
      ArrayList<ArrayList<DataReader.Coordinate>> map = dataInput.readData();
      int height = dataInput.height; int length = dataInput.length;
      int startX = dataInput.startX; int startY = dataInput.startY;
      int goalX = dataInput.goalX; int goalY = dataInput.goalY;
      Compass navMap = new Compass(map, length, height, startX, startY, goalX, goalY);
      BFS search1 = new BFS(navMap);
      search1.calculatePath();
  }

}