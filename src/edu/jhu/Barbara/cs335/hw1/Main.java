package edu.jhu.Barbara.cs335.hw1;
import java.io.IOException;
import java.util.Scanner;

/** CS335 ARTIFICIAL INTELLIGENCE - ASSIGNMENT 1
 * The Main program creates the DataReader object, and interfaces with the
 * user, converting commands into applicable search calls.
 *
 * @author Barbara Holt
 */
public class Main {
    public static String mapImage;
    private static boolean vertical;
    /**
     * The Main method contains the entirety of the Main.java program.
     *
     * @param args                  Command line inputs are collected, but not used, as a text interface is provided
     *                              instead
     * @throws IOException          Throws an exception when there is some error with the I/O process
     * @throws InterruptedException Throws an exception when multiple threads are in use and one is interrupted.
     *                              This is not applicable to this program, because multiple threads aren't used.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner inputScan = new Scanner(System.in);
        System.out.println(
                "\n/-----------------------------------------------------------------------------------/"
                + "\n\tPlease enter your desired map (1 - 8) as the first argument, and then your\n\t"
                + "desired search algorithm (BFS, DFS, or A*) as the second argument.\n\n"
                + "\tOPTIONAL: Specify the path printout direction by adding a third argument to\n\tspecify "
                + "whether you want the path printed horizontally (h) or vertically (v).\n\tHorizontal "
                + "print will appear more concise, yet produce greater memory strain.\n\tThus,"
                + "it is advised to use vertical print on large maps, such as map6.\n"
                + "\n\t\tEX: '1 BFS' for MAP1.TXT and BREADTH FIRST SEARCH\n"
                + "/-----------------------------------------------------------------------------------/"
        );
        String destination;
        DataReader dataInput = null;
        while (inputScan.hasNext()) {
            try {
                String input = inputScan.nextLine();
                String[] line = input.toLowerCase().split(" ");
                String num = line[0];
                if (num.toLowerCase().equals("exit")) {
                    return;
                } else {
                    int mapNum = Integer.parseInt(num);
                    switch (mapNum) {
                        case 1:
                            destination = "/data/map1.txt";
                            break;
                        case 2:
                            destination = "/data/map2.txt";
                            break;
                        case 3:
                            destination = "/data/map3.txt";
                            break;
                        case 4:
                            destination = "/data/map4.txt";
                            break;
                        case 5:
                            destination = "/data/map5.txt";
                            break;
                        case 6:
                            destination = "/data/map6.txt";
                            break;
                        case 7:
                            destination = "/data/map7.txt";
                            break;
                        case 8:
                            destination = "/data/map8.txt";
                            break;
                        default:
                            destination = "/data/map1.txt";
                            break;
                    }

                    /**Reads and processes input data maps:*/
                    String workingDirectory = System.getProperty("user.dir");
                    workingDirectory = workingDirectory.replace("src", "/");
                    String absoluteFilePath = workingDirectory + destination;
                    dataInput = new DataReader(absoluteFilePath);
                    dataInput.readData();
                    mapImage = dataInput.toString();

                    /**Checks for optional specification of print method:*/
                    if (line.length > 2) {
                        String printPattern = line[2];
                        if (printPattern.equals("v")) {
                            vertical = true;
                        } else if (printPattern.equals("h")) {
                            vertical = false;
                        }
                    }

                    String search = line[1];
                    if (search.toUpperCase().equals("BFS")) {
                        /**Breadth First Search (BFS):*/
                        System.out.println("BREADTH FIRST SEARCH (BFS):");
                        dataInput.BFS(vertical);
                    } else if (search.toUpperCase().equals("DFS")) {
                        /**Depth First Search (DFS):*/
                        System.out.println("\nDEPTH FIRST SEARCH (DFS):");
                        dataInput.DFS(vertical);
                    } else if (search.toUpperCase().equals("A*")) {
                        /**A* Search (ASTAR):*/
                        System.out.println("\nA* SEARCH (ASTAR):");
                        dataInput.ASTAR(vertical);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println(
                        "\n/-----------------------------------------------------------------------------------/"
                                + "\n\tPlease enter your desired map (1 - 8) as the first argument, and then your\n\t"
                                + "desired search algorithm (BFS, DFS, or A*) as the second argument.\n\n"
                                + "\tOPTIONAL: Specify the path printout direction by adding a third argument to\n\tspecify "
                                + "whether you want the path printed horizontally (h) or vertically (v).\n\tHorizontal "
                                + "print will appear more concise, yet produce greater memory strain.\n\tThus,"
                                + "it is advised to use vertical print on large maps, such as map6.\n"
                                + "\n\t\tEX: '1 BFS' for MAP1.TXT and BREADTH FIRST SEARCH\n"
                                + "/-----------------------------------------------------------------------------------/"
                );
            }
        }
        dataInput.closeScanner(); /**Ends use of the map read earlier*/
        inputScan.close();
    }
}