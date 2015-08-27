package epl.learning.firstEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/26.
 */

//TODO 未解决
class Peach1 {

    private int price;

    public Peach1(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

class Peach2 {
    private int price;

    public Peach2(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

class PeachsExpressionListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("sumPrice: " + eb.get("sumPrice"));
            }
        } else {
            System.out.println("newEvents is null");
        }
    }
}

public class MultiAnnotation {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addEventType("Peach1Event", Peach1.class);
        config.addEventType("Peach2Event", Peach2.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();


        //多个参数的expression
        String epl2 = "expression sumPrice{ (x,y) => (x.price+y.price) } select sumPrice(peach1,peach2) as sumPrice from Peach1Event as peach1, Peach2Event as peach2";

        EPStatement statement1 = admin.createEPL(epl2);
        statement1.addListener(new PeachsExpressionListener());

        EPRuntime er = esProvider.getEPRuntime();

        Peach1 peach1 = new Peach1(10);
        er.sendEvent(peach1);

        Peach2 peach2 = new Peach2(20);
        er.sendEvent(peach2);
    }
}
