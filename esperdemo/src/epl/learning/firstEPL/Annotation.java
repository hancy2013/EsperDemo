package epl.learning.firstEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/26.
 */
class Orange {
    private int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Orange(int price) {
        this.price = price;
    }
}

//添加监听
class OrangeAnnotationListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                // 当加上注解 @EventRepresentation(array=true)时，结果事件类型为数组而不是Map
                // array=false时，也就是默认情况，结果事件类型为数组是Map
                System.out.println("Sum Price: " + eb.get("orangeSum") + " Event Tpye is: " + eb.getEventType().getUnderlyingType());
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

public class Annotation {
    public static void main(String[] args) {

        Configuration config = new Configuration();
        config.addEventType("orangeEvent", Orange.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        String epl1 = "@Priority(10)@EventRepresentation(array=true) select sum(price) as orangeSum from orangeEvent.win:length_batch(2)";
        String epl2 = "@Name(\"EPL2\")select sum(price) as orangeSum from orangeEvent.win:length_batch(2)";
        String epl3 = "@Drop select sum(price) as orangeSum from orangeEvent.win:length_batch(2)";

        EPStatement statement1 = admin.createEPL(epl1);
        statement1.addListener(new OrangeAnnotationListener());

        EPStatement statement2 = admin.createEPL(epl2);
        statement2.addListener(new OrangeAnnotationListener());
        System.out.println("epl2's name: " + statement2.getName());

        EPStatement statement3 = admin.createEPL(epl3);
        statement3.addListener(new OrangeAnnotationListener());

        EPRuntime er = esProvider.getEPRuntime();

        //实例化事件对象
        Orange orange1 = new Orange(12);
        er.sendEvent(orange1);

        Orange orange2 = new Orange(25);
        er.sendEvent(orange2);
    }
}
