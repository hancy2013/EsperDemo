package epl.learning.ninth.ContainedEventSelection;

import com.espertech.esper.client.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by wxmimperio on 2015/9/10.
 */

class SelectContainedListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

public class SelectContainedEvent {
    public static void main(String[] args) {
        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = esProvider.getEPAdministrator();
        EPRuntime er = esProvider.getEPRuntime();

        Review r1 = new Review();
        r1.setReviewId(1);
        r1.setComment("r1");

        Book b1 = new Book();
        b1.setAuthor("b1");
        b1.setBookId(1);
        b1.setReview(r1);

        Book b2 = new Book();
        b2.setAuthor("b2");
        b2.setBookId(2);

        Item i1 = new Item();
        i1.setItemId(1);
        i1.setProductId(1);
        i1.setPrice(1.11);
        i1.setAmount(2);

        //添加Bean对象
        MediaOrder mo1 = new MediaOrder();
        Books bs = new Books();
        Items is = new Items();
        List<Item> items = new ArrayList<Item>();
        List<Book> books = new ArrayList<Book>();
        items.add(i1);
        books.add(b1);
        books.add(b2);
        mo1.setOrderId(1);
        bs.setBook(books);
        is.setItem(items);
        mo1.setItems(is);
        mo1.setBooks(bs);

        String mediaOrder = MediaOrder.class.getName();
        String epl = "select * from " + mediaOrder + "[books.book]";
        EPStatement stat1 = admin.createEPL(epl);
        stat1.addListener(new SelectContainedListener());

        er.sendEvent(mo1);
    }
}
