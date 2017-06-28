package learning.solutions.advanced.matrix.utils;

import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

import java.awt.*;

/**
 * Created by jmalakalapalli on 6/27/17.
 */
public class RideShareUtil {
    private static final int CELL_WIDTH = 1000;
    private static final int DISTANCE = 5;
    private static final double min_lat = 39.887104;
    private static final double min_lon = 116.26265;

    public static Point getXYCoordinates(double lat, double lon) throws Exception {
        GeodesicData g = Geodesic.WGS84.Inverse(min_lat, min_lon, lat, lon,
                GeodesicMask.DISTANCE);
        int no_of_pixels = 0;

        double distance_in_miles = (g.s12 * 0.621371) / 1000;

        if (distance_in_miles > DISTANCE) {
            throw new Exception(String.format("The point %1f,%2f is outside the radar", lat, lon));
        } else {
            no_of_pixels = (int) ((distance_in_miles / DISTANCE) * CELL_WIDTH);
            return new Point(no_of_pixels, no_of_pixels);
        }
    }

    public static void main(String[] args) {
        try {
            Point p = getXYCoordinates(39.887104, 116.26241);
            System.out.print(p.getX() + ", " + p.getY());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
