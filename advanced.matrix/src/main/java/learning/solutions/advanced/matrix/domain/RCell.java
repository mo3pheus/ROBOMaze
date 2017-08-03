package learning.solutions.advanced.matrix.domain;

/**
 * Created by sanketkorgaonkar on 6/2/17.
 */

import learning.solutions.advanced.matrix.utils.LatLongID;
import learning.solutions.advanced.matrix.utils.RideShareUtil;

import java.awt.*;
import java.lang.Double;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by sanket on 5/23/17.
 */
public class RCell {

    public enum etlSchema {
        MEAN_LAT(0), MEAN_LONG(1), STD_DEV_LAT(2), STD_DEV_LONG(3), CLASS_ID(4);
        private final int value;

        private etlSchema(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Direction {
        NORTH(0), SOUTH(1), WEST(2), EAST(3);
        private final int value;

        private Direction(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private static final double REALLY_LOW_VALUE = -99999.0d;
    private static final int    MAX_TRIES        = 100;

    private Point  center    = null;
    private int    cellWidth = 0;
    private int    id        = -1;
    private double qValue    = 0.0d;
    private double reward    = 0.0d;

    private double meanLat    = 0.0d;
    private double meanLong   = 0.0d;
    private Long threshold = TimeUnit.MINUTES.toMillis(5);
    public double getMeanLat() {
        return meanLat;
    }

    public double getMeanLong() {
        return meanLong;
    }

    private List<Long> rewardTimestamps = new ArrayList<>();
    private List<Long> penaltyTimestamps = new ArrayList<>();
    private double customerCallReward = 10.0d;
    private double trafficDelayReward = -10.0d;

    public double recalculateRewardValue() {
        purgeExpiredPenaltyTimes();
        purgeExpiredRewardTimes();
        return reward + rewardTimestamps.size() * customerCallReward - penaltyTimestamps.size() * trafficDelayReward;
    }

    public void purgeExpiredRewardTimes() {
        Long currentTime = System.currentTimeMillis();
        java.util.List<Long> newRewardTimes = new ArrayList<>();

        for (Long time: rewardTimestamps) {
            if ((currentTime - time) <= threshold) {
                newRewardTimes.add(time);
            }
        }

        rewardTimestamps = newRewardTimes;
    }

    public void purgeExpiredPenaltyTimes() {
        Long currentTime = System.currentTimeMillis();
        java.util.List<Long> newPenaltyTimes = new ArrayList<>();

        for (Long time: penaltyTimestamps) {
            if ((currentTime - time) <= threshold) {
                newPenaltyTimes.add(time);
            }
        }

        penaltyTimestamps = newPenaltyTimes;
    }

    private double stdDevLat  = 0.0d;
    private double stdDevLong = 0.0d;
    private LatLongID latLongIdentifier = null;

    private RCell[] adjacentNodes = new RCell[Direction.values().length];

    private static String cleanString(String input) {
        String[] meta = {"\\:", "Centroid", "\\[", "\\]", "StdDev", "ClassId", " "};
        for (String s : meta) {
            input = input.replaceAll(s, "");
        }
        return input;
    }

    public RCell(Point center, int id, int cellWidth) {
        this.center = center;
        this.id = id;
        this.cellWidth = cellWidth;
    }

    public RCell(String cellString) throws Exception{
        String   cleanedString = cleanString(cellString);
        String[] parts         = cleanedString.split(",");

        this.meanLat = Double.parseDouble(parts[etlSchema.MEAN_LAT.getValue()]);
        this.meanLong = Double.parseDouble(parts[etlSchema.MEAN_LONG.getValue()]);
        this.stdDevLat = Double.parseDouble(parts[etlSchema.STD_DEV_LAT.getValue()]);
        this.stdDevLong = Double.parseDouble(parts[etlSchema.STD_DEV_LONG.getValue()]);
        this.id = Integer.parseInt(parts[etlSchema.CLASS_ID.getValue()]);

        this.center = new Point(0,0);
    }


    public void setMeanLat(double meanLat) {
        this.meanLat = meanLat;
    }

    public void setMeanLong(double meanLong) {
        this.meanLong = meanLong;
    }

    public double getStdDevLat() {
        return stdDevLat;
    }

    public void setStdDevLat(double stdDevLat) {
        this.stdDevLat = stdDevLat;
    }

    public double getStdDevLong() {
        return stdDevLong;
    }

    public void setStdDevLong(double stdDevLong) {
        this.stdDevLong = stdDevLong;
    }

    public RCell(Point point, Double latitude, Double longitude, LatLongID latLongIdentifier, int id) throws Exception {
        this.meanLat = latitude;
        this.meanLong = longitude;
        this.latLongIdentifier = latLongIdentifier;
        this.id = id;
        this.center = point;
    }

    public double getqValue() {
        return qValue;
    }

    public void setqValue(double qValue) {
        this.qValue = qValue;
    }

    public RCell[] getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(RCell[] adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    public Point getCenter() {
        return center;
    }

    public int getId() {
        return id;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public LatLongID getLatLongIdentifier() {
        return latLongIdentifier;
    }

    public void setLatLongIdentifier(LatLongID latLongIdentifier) {
        this.latLongIdentifier = latLongIdentifier;
    }

    public String toString() {
        String cellString = center.toString();
        cellString += " Id = " + id + " qValue = " + qValue + " Reward = " + reward + " MeanLat = " + meanLat + " " +
                "MeanLong = " + meanLong
                + " stdDevLat = " + stdDevLat + " stdDevLong = " + stdDevLong;
        return cellString;
    }

    public RCell getBestAction() {
        RCell best = null;

        double maxQ = RCell.REALLY_LOW_VALUE;
        for (RCell temp : adjacentNodes) {
            if (temp != null && temp.getqValue() > maxQ) {
                best = temp;
                maxQ = best.getqValue();
            }
        }

        return best;
    }

    public RCell getRandomAction() {
        int id    = ThreadLocalRandom.current().nextInt(0, Direction.values().length);
        int tries = 0;
        while (adjacentNodes[id] == null && tries < MAX_TRIES) {
            id = ThreadLocalRandom.current().nextInt(0, Direction.values().length);
            tries++;
        }
        return adjacentNodes[id];
    }

    public void addCustomerCall() {
        this.rewardTimestamps.add(System.currentTimeMillis());
    }

    public void addTrafficDelay() {
        this.penaltyTimestamps.add(System.currentTimeMillis());
    }
}

