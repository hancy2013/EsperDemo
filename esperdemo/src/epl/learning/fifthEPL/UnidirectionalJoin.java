package epl.learning.fifthEPL;
import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/27.
 *
 * 1. orange1和banana1的price相等，orange2和banana2的price相等
 * 2. orange1先于banana1进入引擎，由于orange1进入时发现没有满足条件的Banana事件，所以什么输出也没有
 * 3. 之后banana1进入了，因为满足条件的orange1已经移除了，所以也是没有输出
 * 4. banana2先于orange2进入引擎，被引擎暂存了起来，然后orange2进入后，立刻进行join条件判断
 * 5. 发现暂存的banana2的price相等，所以触发了listener并输出满足条件的这两个对象
 */

class Orange {
    private int price;

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {

        return price;
    }

    @Override
    public String toString() {
        return "Orange price=" + price;
    }

    public Orange(int price) {
        this.price = price;
    }
}

class Banana {
    private int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Banana price=" + price;
    }

    public Banana(int price) {
        this.price = price;
    }
}

class JoinUnidirectionalListener implements UpdateListener {
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for(EventBean eb : newEvents) {
                System.out.println("ornage: " + eb.get("orange") + ", " + "banana: " + eb.get("banana"));
            }
        }else {
            System.out.println("newEvent is null");
        }
    }
}

public class UnidirectionalJoin {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addEventType("Orange",Orange.class);
        config.addEventType("Banana",Banana.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        String epl1 = "select * from Orange as orange unidirectional, Banana.std:lastevent() as banana where orange.price = banana.price";

        EPStatement statement = admin.createEPL(epl1);
        statement.addListener(new JoinUnidirectionalListener());

        EPRuntime er = esProvider.getEPRuntime();

        Orange orange1 = new Orange(1);
        System.out.println("sendEvent: " + orange1);
        er.sendEvent(orange1);

        Banana banana1 = new Banana(1);
        System.out.println("sendEvent: " + banana1);
        er.sendEvent(banana1);

        Banana banana2 = new Banana(2);
        System.out.println("sendEvent: " + banana2);
        er.sendEvent(banana2);

        Orange orange2 = new Orange(2);
        System.out.println("sendEvent: " + orange2);
        er.sendEvent(orange2);
    }
}
