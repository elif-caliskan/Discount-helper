
public class Retailer {
    public String retailerName;
    public String retailerEmail;
    public String retailerPassword;
    public int retailerId;

    public Retailer(String email, String userName, String password) {
        this.retailerEmail = email;
        this.retailerName = userName;
        this.retailerPassword = password;
    }

    public Retailer(String userName, String password) {
        this.retailerName = userName;
        this.retailerPassword = password;
    }

    public Retailer(String email, String userName, String password, int id) {
        this.retailerEmail = email;
        this.retailerName = userName;
        this.retailerPassword = password;
        this.retailerId = id;
    }
}
