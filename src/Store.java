
public class Store {

    public String storeId;
    public double storeLatitude;
    public double storeLongitude;
    public String storeName;
    public String storeAddress;

    public Store(double latitude, double longitude, String name, String address){
        this.storeLatitude = latitude;
        this.storeLongitude = longitude;
        this.storeName = name;
        this.storeAddress =address;
    }

    Store(String id, double latitude, double longitude, String name, String address) {
        this.storeLatitude = latitude;
        this.storeLongitude = longitude;
        this.storeName = name;
        this.storeId = id;
        this.storeAddress = address;
    }
}

