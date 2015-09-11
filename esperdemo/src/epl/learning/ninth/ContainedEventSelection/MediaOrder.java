package epl.learning.ninth.ContainedEventSelection;

/**
 * Created by wxmimperio on 2015/9/10.
 */
public class MediaOrder {
    private int orderId;
    private Items items;
    private Books books;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    public Books getBooks() {
        return books;
    }

    public void setBooks(Books books) {
        this.books = books;
    }

    public String toString() {
        return "MediaOrder{" + "orderId=" + orderId + ", items=" + items + ", books=" + books + '}';
    }
}