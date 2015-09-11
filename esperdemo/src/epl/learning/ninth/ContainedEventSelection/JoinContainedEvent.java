package epl.learning.ninth.ContainedEventSelection;

import com.espertech.esper.client.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxmimperio on 2015/9/11.
 */

class JoinContainedListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oloEvents) {
        if (newEvents != null) {
            for (int i = 0; i < newEvents.length; i++) {
                if (newEvents[i] == null) {
                    continue;
                }
                System.out.println(newEvents[i].getUnderlying());
            }
        }
    }
}

public class JoinContainedEvent {
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
        b2.setReview(new Review());

        Item i1 = new Item();
        i1.setItemId(1);
        i1.setProductId(1);
        i1.setPrice(1.11);
        i1.setAmount(2);

        Item i2 = new Item();
        i1.setItemId(3);
        i1.setProductId(3);
        i1.setPrice(3.11);
        i1.setAmount(5);

        MediaOrder mo1 = new MediaOrder();
        Books bs = new Books();
        Items is = new Items();

        List<Item> itemList = new ArrayList<Item>();
        List<Book> books = new ArrayList<Book>();

        //添加两个item
        itemList.add(i1);
        itemList.add(i2);

        //添加两个book
        books.add(b1);
        books.add(b2);

        //添加一个mediaorder
        mo1.setOrderId(1);
        bs.setBook(books);
        is.setItem(itemList);
        mo1.setItems(is);
        mo1.setBooks(bs);

        String mediaOrder = MediaOrder.class.getName();

        String join1 = "select book.bookId, item.itemId from " + mediaOrder + "[books.book] as book, " + mediaOrder
                + "[items.item] as item where productId = bookId";

        EPStatement statement = admin.createEPL(join1);
        statement.addListener(new JoinContainedListener());

        System.out.println("EPL1: " + join1);
        er.sendEvent(mo1);
        statement.destroy();
        System.out.println();

        String join2 = "select book.bookId, item.itemId from " + mediaOrder + "[books.book] as book left outer join " + mediaOrder
                + "[items.item] as item on productId = bookId";

        EPStatement statement1 = admin.createEPL(join2);
        statement1.addListener(new JoinContainedListener());
        System.out.println("EPL2: " + join2);
        er.sendEvent(mo1);
        System.out.println();

        String join3 = "select book.bookId, item.itemId from " + mediaOrder + "[books.book] as book full outer join " + mediaOrder
                + "[items.item] as item on productId = bookId";

        EPStatement statement2 = admin.createEPL(join3);
        statement2.addListener(new JoinContainedListener());

        System.out.println("EPL3: " + join3);
        er.sendEvent(mo1);
        statement2.destroy();
        System.out.println();

        String join4 = "select count(*) from " + mediaOrder + "[books.book] as book, " + mediaOrder
                + "[items.item] as item where productId = bookId";

        EPStatement statement3 = admin.createEPL(join4);
        statement3.addListener(new JoinContainedListener());

        System.out.println("EPL4: " + join4);
        er.sendEvent(mo1);
        statement3.destroy();
        System.out.println();

        String join5 = "select count(*) from " + mediaOrder + "[books.book] as book unidirectional, " + mediaOrder
                + "[items.item] as item where productId = bookId";

        EPStatement statement4 = admin.createEPL(join5);
        statement4.addListener(new JoinContainedListener());

        System.out.println("EPL5: " + join5);
        er.sendEvent(mo1);
        statement4.destroy();
        System.out.println();
    }
}
