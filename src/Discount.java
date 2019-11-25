
public class Discount {
    public String discountProduct;
    public int discountPercent;
    public int discountId;
    public int storeId;

    public Discount(String product, int discountPercent) {
        this.discountProduct = product;
        this.discountPercent = discountPercent;
    }

    public Discount(String product, int discountPercent, int id) {
        this.discountProduct = product;
        this.discountPercent = discountPercent;
        this.discountId = id;
    }

    public Discount(int discountId) {
        this.discountId = discountId;
    }
}
