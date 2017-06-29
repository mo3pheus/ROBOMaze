package learning.solutions.advanced.matrix;

import com.spatial4j.core.distance.DistanceUtils;
import com.spatial4j.core.distance.GeodesicSphereDistCalc;
import learning.solutions.advanced.matrix.domain.RCell;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.spatial4j.core.distance.DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM;
import static com.spatial4j.core.distance.DistanceUtils.distVincentyRAD;

/**
 * Created by sanketkorgaonkar on 6/27/17.
 */
public class ETLTest {

    public static void main(String[] args) {
        GeodesicSphereDistCalc.Vincenty vincenty = new GeodesicSphereDistCalc.Vincenty();
        int                             i        = 0;
        List<RCell>                     grid     = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File
                ("/Users/sanketkorgaonkar/Documents/workspace/robo.maze" +
                        ".class/advanced.matrix/src/main/resources/dataYOLO_output.txt")))) {
            String centroidString = bufferedReader.readLine();
            while (centroidString != null) {
                RCell rCell = new RCell(centroidString);
                grid.add(rCell);
                /*System.out.println(" Iteration number = " + i++);
                System.out.println(centroidString);
                System.out.println(rCell);*/
                centroidString = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int j = 0; j < grid.size() - 1; j++) {
            RCell a = grid.get(j);
            RCell b = grid.get(j + 1);

            double dist = distVincentyRAD(Math.toRadians(a.getMeanLat()), Math.toRadians(a.getMeanLong()),
                    Math.toRadians(b.getMeanLat()), Math.toRadians(b.getMeanLong()));

            dist = DistanceUtils.radians2Dist(dist,EARTH_EQUATORIAL_RADIUS_KM );
            System.out.println("From " + a.toString() + " to " + b.toString() + " = " + dist);
            System.out.println("====================================================");
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
