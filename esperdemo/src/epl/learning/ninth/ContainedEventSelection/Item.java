package epl.learning.ninth.ContainedEventSelection;

/**
 * Created by wxmimperio on 2015/9/10.
 */

public class Item {
    private int itemId;
    private int productId;
    private int amount;
    private double price;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String toString() {
        return "Item{" + "itemId=" + itemId + ", productId=" + productId + ", amount=" + amount + ", price=" + price + '}';
    }
}
