package epl.learning.fifthEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/27.
 */

class Apple {
    private int price;
    private int size;

    public void setPrice(int price) {
        this.price = price;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPrice() {
        return price;
    }

    public int getSize() {
        return size;
    }

    public Apple(int price, int size) {
        this.price = price;
        this.size = size;
    }
}

class Fruit {
    private int sumPrice;
    private int sumSize;

    public Fruit(int sumPrice, int sumSize) {
        this.sumPrice = sumPrice;
        this.sumSize = sumSize;
    }

    public int getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(int sumPrice) {
        this.sumPrice = sumPrice;
    }

    public int getSumSize() {
        return sumSize;
    }

    public void setSumSize(int sumSize) {
        this.sumSize = sumSize;
    }
}

class SubQueryExpressionListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("sumPrice: " + eb.get("sumPrice"));
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

public class SubQueryExpression {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addEventType("Apple",Apple.class);
        config.addEventType("Fruit",Fruit.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        String epl1 = "select sumPrice from Fruit where sumPrice=(select price from Apple.std:lastevent())";

        EPStatement statement = admin.createEPL(epl1);
        statement.addListener(new SubQueryExpressionListener());

        EPRuntime er = esProvider.getEPRuntime();

        Apple apple1 = new Apple(20,1);
        er.sendEvent(apple1);

        Fruit fruit = new Fruit(20,1);
        er.sendEvent(fruit);
    }
}
