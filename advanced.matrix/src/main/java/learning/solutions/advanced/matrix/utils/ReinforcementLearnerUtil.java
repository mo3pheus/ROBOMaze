package learning.solutions.advanced.matrix.utils;

/**
 * Created by sanketkorgaonkar on 6/2/17.
 */

import learning.solutions.advanced.matrix.domain.RCell;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicMask;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by sanketkorgaonkar on 5/23/17.
 */
public class ReinforcementLearnerUtil {

    public static RCell getRandomSource(RCell[][] grid) {
        int i = ThreadLocalRandom.current().nextInt(0, grid[0].length);
        int j = ThreadLocalRandom.current().nextInt(0, grid[0].length);

        return grid[i][j];
    }

    public static void setDestinationReward(RCell[][] grid, RCell destination, double reward) {
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j].equals(destination)) {
                    grid[i][j].setqValue(reward);
                    grid[i][j].setReward(reward);
                }
                for (RCell adNode : grid[i][j].getAdjacentNodes()) {
                    if (adNode != null && adNode.getCenter().equals(destination.getCenter())) {
                        adNode.setqValue(reward);
                        adNode.setReward(reward);
                    }
                }
            }
        }
    }

    public static RCell findPoint(Point point, RCell[][] grid) {
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j].getCenter().equals(point)) {
                    return grid[i][j];
                }
            }
        }
        return null;
    }

    public static RCell getLeft(RCell rCell, RCell[][] grid, int cellWidth) {
        Point center = rCell.getCenter();
        int   newX   = center.x - cellWidth;

        return (newX < 0) ? null : findPoint(new Point(newX, center.y), grid);
    }

    public static RCell getRight(RCell rCell, RCell[][] grid, int cellWidth, int frameWidth) {
        Point center = rCell.getCenter();
        int   newX   = center.x + cellWidth;

        return (newX > frameWidth) ? null : findPoint(new Point(newX, center.y), grid);
    }

    public static RCell getTop(RCell rCell, RCell[][] grid, int cellWidth) {
        Point center = rCell.getCenter();
        int   newY   = center.y - cellWidth;

        return (newY < 0) ? null : findPoint(new Point(center.x, newY), grid);
    }

    public static RCell getBottom(RCell rCell, RCell[][] grid, int cellWidth, int frameWidth) {
        Point center = rCell.getCenter();
        int   newY   = center.y + cellWidth;

        return (newY > frameWidth) ? null : findPoint(new Point(center.x, newY), grid);
    }

    @Deprecated
    public static RCell getBest(RCell rCell, RCell[][] grid, int cellWidth, int frameWidth) {
        RCell best = null;

        java.util.List<RCell> adjacentNodes = new ArrayList<>();
        RCell                 left          = ReinforcementLearnerUtil.getLeft(rCell, grid, cellWidth);
        if (left != null) {
            adjacentNodes.add(left);
        }

        RCell right = ReinforcementLearnerUtil.getRight(rCell, grid, cellWidth, frameWidth);
        if (right != null) {
            adjacentNodes.add(right);
        }

        RCell top = ReinforcementLearnerUtil.getTop(rCell, grid, cellWidth);
        if (top != null) {
            adjacentNodes.add(top);
        }

        RCell bottom = ReinforcementLearnerUtil.getBottom(rCell, grid, cellWidth, frameWidth);
        if (bottom != null) {
            adjacentNodes.add(bottom);
        }

        double maxGVal = -1000.0d;
        for (RCell temp : adjacentNodes) {
            if (temp.getqValue() > maxGVal) {
                maxGVal = temp.getqValue();
                best = temp;
            }
        }

        return best;
    }

    public static RCell[][] loadData(final String pathToClusterFile, final int RCELL_ROWS, final int RCELL_COLUMNS) {
        int i = 0;
        java.util.List<RCell> rCellGrid = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(pathToClusterFile)))) {
            String centroidString = bufferedReader.readLine();
            while (centroidString != null) {
                RCell rCell = new RCell(centroidString);
                rCellGrid.add(rCell);
                System.out.println(" Iteration number = " + i++);
                System.out.println(centroidString);
                System.out.println(rCell);
                centroidString = bufferedReader.readLine();
            }

            return buildRMap(rCellGrid, RCELL_ROWS, RCELL_COLUMNS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new RuntimeException("This is not supposed to happen!! ");
    }

    private static RCell[][] buildRMap(java.util.List<RCell> rmapList, final int RCELL_ROWS, final int RCELL_COLUMNS) {

        // [0] - NORTH, [1] - EAST, [2] - SOUTH, [3] - WEST
        final Double NORTH = 0.0;
        final Double EAST = 90.0;
        final Double WEST = 270.0;
        final Double SOUTH = 180.0;

        final Double PER_MILE = 1609.344;

        Double minLat = Double.MAX_VALUE, minLong = Double.MAX_VALUE;
        Double maxLat = Double.MIN_VALUE, maxLong = Double.MIN_VALUE;


        for (int i = 0; i < rmapList.size(); i++) {
            RCell currentCell = rmapList.get(i);

            if (currentCell.getMeanLat() < minLat) {
                minLat = currentCell.getMeanLat();
            }

            if (currentCell.getMeanLong() < minLong) {
                minLong = currentCell.getMeanLong();
            }

            if (currentCell.getMeanLat() > maxLat) {
                maxLat = currentCell.getMeanLat();
            }

            if (currentCell.getMeanLong() > maxLong) {
                maxLong = currentCell.getMeanLong();
            }

        }

        System.out.println(String.format("[minLat = %f, minLong = %f] [maxLat = %f, maxLong = %f]", minLat, minLong, maxLat, maxLong));
        GeodesicData widthGeodesic = Geodesic.WGS84.Inverse(minLat, minLong, minLat, maxLong,
                GeodesicMask.DISTANCE);

        GeodesicData heightGeodesic = Geodesic.WGS84.Inverse(minLat, minLong, maxLat, minLong,
                GeodesicMask.DISTANCE);

        Double width = widthGeodesic.s12;
        Double height = heightGeodesic.s12;
        System.out.println("Width: " + width / PER_MILE);
        System.out.println("Height: " + height / PER_MILE);

        Double unitWidth = Math.floor(width / RCELL_ROWS);
        Double unitHeight = Math.floor(height / RCELL_COLUMNS);

        // Build the width section of the matrix
        java.util.List<LatLongID> widthSections = new LinkedList<>();
        widthSections.add(new LatLongID(minLat, minLong, 'x', 0));

        GeodesicData nextUnitWidth = Geodesic.WGS84.Direct(minLat, minLong, EAST, unitWidth);
        widthSections.add(new LatLongID(nextUnitWidth.lat2, nextUnitWidth.lon2, 'x', 1));

        int x = 2;
        while (nextUnitWidth.lon2 < maxLong) {
            nextUnitWidth = Geodesic.WGS84.Direct(nextUnitWidth.lat2, nextUnitWidth.lon2, EAST, unitWidth);
            if (nextUnitWidth.lon2 < maxLong) {
                widthSections.add(new LatLongID(nextUnitWidth.lat2, nextUnitWidth.lon2, 'x', x));
                x++;
            }
        }

        // Build height section of the matrix
        java.util.List<LatLongID> heightSections = new LinkedList<>();
        heightSections.add(new LatLongID(minLat, minLong, 'y', 0));

        GeodesicData nextUnitHeight = Geodesic.WGS84.Direct(minLat, minLong, NORTH, unitHeight);
        heightSections.add(new LatLongID(nextUnitHeight.lat2, nextUnitHeight.lon2, 'y', 1));

        int y = 2;
        while (nextUnitHeight.lat2 < maxLat) {
            nextUnitHeight = Geodesic.WGS84.Direct(nextUnitHeight.lat2, nextUnitHeight.lon2, NORTH, unitHeight);
            if (nextUnitHeight.lat2 < maxLat) {
                heightSections.add(new LatLongID(nextUnitHeight.lat2, nextUnitHeight.lon2, 'y', y));
                y++;
            }
        }

        return generateMatrix(widthSections, heightSections);
    }

    private static RCell[][] generateMatrix(java.util.List<LatLongID> xList, java.util.List<LatLongID> yList) {
        if (xList.size() != yList.size()) {
            throw new RuntimeException("Sizes cannot be different");
        }


        int size = xList.size();
        RCell[][] toReturn = new RCell[size][size];
        Map<String, RCell> idrCellMap = new HashMap<>();

        Random random = new Random();
        int randomNumber = 10000;
        int id = randomNumber;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                LatLongID xCoOrdinate = xList.get(j);
                LatLongID yCoOrdinate = yList.get(i);

                Double longitude = xCoOrdinate.getLongitude();
                Double latitude = yCoOrdinate.getLatitude();
                String ID = yCoOrdinate.getIdentifier() + xCoOrdinate.getIdentifier();

                LatLongID currentLatLongID = new LatLongID(latitude, longitude, xCoOrdinate.getPrefix(), xCoOrdinate.getIndex()
                        , yCoOrdinate.getPrefix(), yCoOrdinate.getIndex(), ID);

                RCell currentRcell = new RCell(latitude, longitude, currentLatLongID, id);

                idrCellMap.put(currentLatLongID.getIdentifier(), currentRcell);
                toReturn[i][j] = currentRcell;

                id = id + randomNumber;
            }
        }

        System.out.println("------------------------------------------------ PRINTING THE MATRIX ------------------------------------------------");
        for (int i = size-1; i >= 0; i--) {
            System.out.println();
            for (int j = 0; j < size; j++) {
                System.out.print("|");
                System.out.print(" ");
                System.out.print(toReturn[i][j].getLatLongIdentifier().getIdentifier() + ", " + toReturn[i][j].getId()+","+i+"-"+j);
                System.out.print(" ");
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println("------------------------------------------------ END OF MATRIX ------------------------------------------------");

        // Compute adjacency!
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                RCell currentRcell = toReturn[i][j];
                LatLongID currentIdentifier = currentRcell.getLatLongIdentifier();

                String newIdentifier = null;

                // Identify the node EAST of the rCell -- Increment x-index by 1
                int newPositiveXIndex = currentIdentifier.getxIndex();
                newPositiveXIndex++;
                newIdentifier = ""+ currentIdentifier.getyPrefix() + currentIdentifier.getyIndex() + currentIdentifier.getxPrefix() + newPositiveXIndex;
                RCell eastRcellNode = idrCellMap.get(newIdentifier);
                currentRcell.getAdjacentNodes()[RCell.Direction.EAST.getValue()] = eastRcellNode;


                // Identify the node WEST of the rCell -- Increment x-index by 1
                int newNegativeXIndex = currentIdentifier.getxIndex();
                newNegativeXIndex--;
                newIdentifier = ""+ currentIdentifier.getyPrefix() + currentIdentifier.getyIndex() + currentIdentifier.getxPrefix() + newNegativeXIndex;
                RCell westRcellNode = idrCellMap.get(newIdentifier);
                currentRcell.getAdjacentNodes()[RCell.Direction.WEST.getValue()] = westRcellNode;


                // Identify the node NORTH of the rCell -- Increment x-index by 1
                int newPositiveYIndex = currentIdentifier.getyIndex();
                newPositiveYIndex++;
                newIdentifier = ""+ currentIdentifier.getyPrefix() + newPositiveYIndex + currentIdentifier.getxPrefix() + currentIdentifier.getxIndex();
                RCell northRcellNode = idrCellMap.get(newIdentifier);
                currentRcell.getAdjacentNodes()[RCell.Direction.NORTH.getValue()] = northRcellNode;


                // Identify the node SOUTH of the rCell -- Increment x-index by 1
                int newNegativeYIndex = currentIdentifier.getyIndex();
                newNegativeYIndex--;
                newIdentifier = ""+ currentIdentifier.getyPrefix() + newNegativeYIndex + currentIdentifier.getxPrefix() + currentIdentifier.getxIndex();
                RCell southRcellNode = idrCellMap.get(newIdentifier);
                currentRcell.getAdjacentNodes()[RCell.Direction.SOUTH.getValue()] = southRcellNode;

            }
        }

        // Print all the lat long values

        int blah = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (blah < 240000000) {
                    System.out.println(toReturn[i][j].getMeanLat() + "," + toReturn[i][j].getMeanLong());
                }
                blah++;
            }
        }

        return toReturn;
    }

    private static void print(java.util.List<LatLongID> inputList) {
        inputList.forEach(input -> System.out.println(input.getLatitude() + "," + input.getLongitude()));
    }

    public static int findNearestRCell(RCell[][] navGrid, Double inputLat, Double inputLong) throws Exception {
        int size = navGrid.length;
        Double minimumDistance = Double.MAX_VALUE;
        RCell nearestCell = null;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                RCell currentRCell =  navGrid[i][j];

                GeodesicData geodesicData = Geodesic.WGS84.Inverse(currentRCell.getMeanLat(), currentRCell.getMeanLong(),
                        inputLat, inputLong, GeodesicMask.DISTANCE);

                Double currentDistance = geodesicData.s12;

                if(currentDistance < minimumDistance) {
                    minimumDistance = currentDistance;
                    nearestCell = currentRCell;
                }
            }
        }

        Objects.requireNonNull(nearestCell, "MINIMUM DISTANCE NOT FOUND");

        // If miimum distance is greater than 20 miles then throw exception
        if (minimumDistance > 32186.9) {
            throw new Exception(String.format("Input lat-long values: " +
                    "[lat: %f, long: %f] are not in the bounds of the nav grid", inputLat, inputLong));
        }

        return nearestCell.getId();
    }
}
