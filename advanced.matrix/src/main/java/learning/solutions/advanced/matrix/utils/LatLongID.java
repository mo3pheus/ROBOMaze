package learning.solutions.advanced.matrix.utils;

public class LatLongID {
    private final Double latitude;
    private final Double longitude;
    private Integer index;
    private char prefix;
    private final String identifier;

    private char xPrefix;
    private int xIndex;
    private char yPrefix;
    private int yIndex;

    public LatLongID(Double latitude, Double longitude, char xPrefix, int xIndex, char yPrefix, int yIndex, String identifier) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.xPrefix = xPrefix;
        this.xIndex = xIndex;
        this.yPrefix = yPrefix;
        this.yIndex = yIndex;
        this.identifier = identifier;
    }

    public LatLongID(Double latitude, Double longitude, char prefix, int index) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.index = index;
        this.prefix = prefix;
        this.identifier = "" + prefix + index;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Integer getIndex() {
        return index;
    }

    public char getPrefix() {
        return prefix;
    }

    public String getIdentifier() {
        return identifier;
    }

    public char getxPrefix() {
        return xPrefix;
    }

    public int getxIndex() {
        return xIndex;
    }

    public char getyPrefix() {
        return yPrefix;
    }

    public int getyIndex() {
        return yIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LatLongID)) return false;

        LatLongID latLongID = (LatLongID) o;

        return getIdentifier().equals(latLongID.getIdentifier());
    }

    @Override
    public int hashCode() {
        return getIdentifier().hashCode();
    }

    @Override
    public String toString() {
        return "LatLongID{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", identifier='" + identifier + '\'' +
                '}';
    }
}
