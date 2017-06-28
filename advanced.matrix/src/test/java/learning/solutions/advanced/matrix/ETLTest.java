package learning.solutions.advanced.matrix;

import learning.solutions.advanced.matrix.domain.RCell;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanketkorgaonkar on 6/27/17.
 */
public class ETLTest {

    public static void main(String[] args) {
        int         i    = 0;
        List<RCell> grid = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File
                ("/Users/sanketkorgaonkar/Documents/workspace/robo.maze" +
                        ".class/advanced.matrix/src/main/resources/dataYOLO_output.txt")))) {
            String centroidString = bufferedReader.readLine();
            while (centroidString != null) {
                RCell rCell = new RCell(centroidString);
                grid.add(rCell);
                System.out.println(" Iteration number = " + i++);
                System.out.println(centroidString);
                System.out.println(rCell);
                centroidString = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String cleanString(String input) {
        String[] meta = {"\\:", "Centroid", "\\[", "\\]", "StdDev", "ClassId", " "};
        for (String s : meta) {
            input = input.replaceAll(s, "");
        }
        return input;
    }
}
