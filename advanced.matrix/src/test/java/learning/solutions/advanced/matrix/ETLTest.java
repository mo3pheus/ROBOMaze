package learning.solutions.advanced.matrix;

import learning.solutions.advanced.matrix.domain.RCell;
import learning.solutions.advanced.matrix.utils.ReinforcementLearnerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanketkorgaonkar on 6/27/17.
 */
public class ETLTest {

    static List<RCell> rCellGrid = new ArrayList<>();
    static final String path = "/Users/schampakaram/DataScience_POCS/Uber_POC/Trajectory_DataSet/Clusters/dataYOLO_output.txt";

    public static void main(String[] args) {
        RCell[][] navGrid = ReinforcementLearnerUtil.loadData(path, 20, 20);

        try {
           int id =  ReinforcementLearnerUtil.findNearestRCell(navGrid, 39.9075000, 116.3972300);
            System.out.println("Nearest cell is: "+ id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static RCell[][] loadData() {
//        int i = 0;
//        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File
//                ("/Users/schampakaram/DataScience_POCS/Uber_POC/Trajectory_DataSet/Clusters/dataYOLO_output.txt")))) {
//            String centroidString = bufferedReader.readLine();
//            while (centroidString != null) {
//                RCell rCell = new RCell(centroidString);
//                rCellGrid.add(rCell);
//                System.out.println(" Iteration number = " + i++);
//                System.out.println(centroidString);
//                System.out.println(rCell);
//                centroidString = bufferedReader.readLine();
//            }
//
//            return buildRMap(rCellGrid, 20, 20);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        throw new RuntimeException("This is not supposed to happen!! ");
//    }
//
//    private static String cleanString(String input) {
//        String[] meta = {"\\:", "Centroid", "\\[", "\\]", "StdDev", "ClassId", " "};
//        for (String s : meta) {
//            input = input.replaceAll(s, "");
//        }
//        return input;
//    }
//
//    public static RCell[][] buildRMap(List<RCell> rmapList, final int RCELL_ROWS, final int RCELL_COLUMNS) {
//
//        // [0] - NORTH, [1] - EAST, [2] - SOUTH, [3] - WEST
//        final Double NORTH = 0.0;
//        final Double EAST = 90.0;
//        final Double WEST = 270.0;
//        final Double SOUTH = 180.0;
//
//        final Double PER_MILE = 1609.344;
//
//        RCell[] closestNodesArray = new RCell[4];
//
//        Double minLat = Double.MAX_VALUE, minLong = Double.MAX_VALUE;
//        Double maxLat = Double.MIN_VALUE, maxLong = Double.MIN_VALUE;
//
//
//        for (int i = 0; i < rmapList.size(); i++) {
//            RCell currentCell = rmapList.get(i);
//
//            if (currentCell.getMeanLat() < minLat) {
//                minLat = currentCell.getMeanLat();
//            }
//
//            if (currentCell.getMeanLong() < minLong) {
//                minLong = currentCell.getMeanLong();
//            }
//
//            if (currentCell.getMeanLat() > maxLat) {
//                maxLat = currentCell.getMeanLat();
//            }
//
//            if (currentCell.getMeanLong() > maxLong) {
//                maxLong = currentCell.getMeanLong();
//            }
//
//        }
//
//        System.out.println(String.format("[minLat = %f, minLong = %f] [maxLat = %f, maxLong = %f]", minLat, minLong, maxLat, maxLong));
//        GeodesicData widthGeodesic = Geodesic.WGS84.Inverse(minLat, minLong, minLat, maxLong,
//                GeodesicMask.DISTANCE);
//
//        GeodesicData heightGeodesic = Geodesic.WGS84.Inverse(minLat, minLong, maxLat, minLong,
//                GeodesicMask.DISTANCE);
//
//        Double width = widthGeodesic.s12;
//        Double height = heightGeodesic.s12;
//        System.out.println("Width: " + width / PER_MILE);
//        System.out.println("Height: " + height / PER_MILE);
//
//        Double unitWidth = Math.floor(width / RCELL_ROWS);
//        Double unitHeight = Math.floor(height / RCELL_COLUMNS);
//
//        // Build the width section of the matrix
//        List<LatLongID> widthSections = new LinkedList<>();
//        widthSections.add(new LatLongID(minLat, minLong, 'x', 0));
//
//        GeodesicData nextUnitWidth = Geodesic.WGS84.Direct(minLat, minLong, EAST, unitWidth);
//        widthSections.add(new LatLongID(nextUnitWidth.lat2, nextUnitWidth.lon2, 'x', 1));
//
//        int x = 2;
//        while (nextUnitWidth.lon2 < maxLong) {
//            nextUnitWidth = Geodesic.WGS84.Direct(nextUnitWidth.lat2, nextUnitWidth.lon2, EAST, unitWidth);
//            if (nextUnitWidth.lon2 < maxLong) {
//                widthSections.add(new LatLongID(nextUnitWidth.lat2, nextUnitWidth.lon2, 'x', x));
//                x++;
//            }
//        }
//
//        // Build height section of the matrix
//        List<LatLongID> heightSections = new LinkedList<>();
//        heightSections.add(new LatLongID(minLat, minLong, 'y', 0));
//
//        GeodesicData nextUnitHeight = Geodesic.WGS84.Direct(minLat, minLong, NORTH, unitHeight);
//        heightSections.add(new LatLongID(nextUnitHeight.lat2, nextUnitHeight.lon2, 'y', 1));
//
//        int y = 2;
//        while (nextUnitHeight.lat2 < maxLat) {
//            nextUnitHeight = Geodesic.WGS84.Direct(nextUnitHeight.lat2, nextUnitHeight.lon2, NORTH, unitHeight);
//            if (nextUnitHeight.lat2 < maxLat) {
//                heightSections.add(new LatLongID(nextUnitHeight.lat2, nextUnitHeight.lon2, 'y', y));
//                y++;
//            }
//        }
//
//        return generateMatrix(widthSections, heightSections);
//    }
//
//    private static RCell[][] generateMatrix(List<LatLongID> xList, List<LatLongID> yList) {
//        if (xList.size() != yList.size()) {
//            throw new RuntimeException("Sizes cannot be different");
//        }
//
//
//        int size = xList.size();
//        RCell[][] toReturn = new RCell[size][size];
//        Map<String, RCell> idrCellMap = new HashMap<>();
//
//        Random random = new Random();
//        int randomNumber = random.nextInt(13423);
//        int id = randomNumber;
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                LatLongID xCoOrdinate = xList.get(j);
//                LatLongID yCoOrdinate = yList.get(i);
//                Double longitude = xCoOrdinate.getLongitude();
//                Double latitude = yCoOrdinate.getLatitude();
//                String ID = yCoOrdinate.getIdentifier() + xCoOrdinate.getIdentifier();
//
//                LatLongID currentLatLongID = new LatLongID(latitude, longitude, xCoOrdinate.getPrefix(), xCoOrdinate.getIndex()
//                        , yCoOrdinate.getPrefix(), yCoOrdinate.getIndex(), ID);
//
//                RCell currentRcell = new RCell(latitude, longitude, currentLatLongID, id);
//
//                idrCellMap.put(currentLatLongID.getIdentifier(), currentRcell);
//                toReturn[i][j] = currentRcell;
//
//                id = id + randomNumber;
//            }
//        }
//
//        System.out.println("------------------------------------------------ PRINTING THE MATRIX ------------------------------------------------");
//        for (int i = 0; i < size; i++) {
//            System.out.println();
//            for (int j = 0; j < size; j++) {
//                System.out.print("|");
//                System.out.print(" ");
//                System.out.print(toReturn[i][j].getLatLongIdentifier().getIdentifier() + ", " + toReturn[i][j].getId());
//                System.out.print(" ");
//            }
//            System.out.print("|");
//            System.out.println();
//        }
//        System.out.println("------------------------------------------------ END OF MATRIX ------------------------------------------------");
//
//        // Compute adjacency!
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                RCell currentRcell = toReturn[i][j];
//                LatLongID currentIdentifier = currentRcell.getLatLongIdentifier();
//
//                String newIdentifier = null;
//
//                // Identify the node EAST of the rCell -- Increment x-index by 1
//                int newPositiveXIndex = currentIdentifier.getxIndex();
//                newPositiveXIndex++;
//                newIdentifier = ""+ currentIdentifier.getyPrefix() + currentIdentifier.getyIndex() + currentIdentifier.getxPrefix() + newPositiveXIndex;
//                RCell eastRcellNode = idrCellMap.get(newIdentifier);
//                currentRcell.getAdjacentNodes()[RCell.Direction.EAST.getValue()] = eastRcellNode;
//
//
//                // Identify the node EAST of the rCell -- Increment x-index by 1
//                int newNegativeXIndex = currentIdentifier.getxIndex();
//                newNegativeXIndex--;
//                newIdentifier = ""+ currentIdentifier.getyPrefix() + currentIdentifier.getyIndex() + currentIdentifier.getxPrefix() + newNegativeXIndex;
//                RCell westRcellNode = idrCellMap.get(newIdentifier);
//                currentRcell.getAdjacentNodes()[RCell.Direction.WEST.getValue()] = westRcellNode;
//
//
//                // Identify the node NORTH of the rCell -- Increment x-index by 1
//                int newPositiveYIndex = currentIdentifier.getyIndex();
//                newPositiveYIndex++;
//                newIdentifier = ""+ currentIdentifier.getyPrefix() + newPositiveYIndex + currentIdentifier.getxPrefix() + currentIdentifier.getxIndex();
//                RCell northRcellNode = idrCellMap.get(newIdentifier);
//                currentRcell.getAdjacentNodes()[RCell.Direction.NORTH.getValue()] = northRcellNode;
//
//
//                // Identify the node NORTH of the rCell -- Increment x-index by 1
//                int newNegativeYIndex = currentIdentifier.getyIndex();
//                newNegativeYIndex--;
//                newIdentifier = ""+ currentIdentifier.getyPrefix() + newNegativeYIndex + currentIdentifier.getxPrefix() + currentIdentifier.getxIndex();
//                RCell southRcellNode = idrCellMap.get(newIdentifier);
//                currentRcell.getAdjacentNodes()[RCell.Direction.SOUTH.getValue()] = southRcellNode;
//
//            }
//        }
//
//        // Print all the lat long values
//
//        int blah = 0;
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                if (blah < 24) {
//                    System.out.println(toReturn[i][j].getMeanLat() + "," + toReturn[i][j].getMeanLong());
//                }
//                blah++;
//            }
//        }
//
//        return toReturn;
//    }
//
//
//    private static void print(List<LatLongID> inputList) {
//        inputList.forEach(input -> System.out.println(input.getLatitude() + "," + input.getLongitude()));
//    }
}
