package epl.learning.firstEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/26.
 */
class Pear {
    private int max;
    private int min;
    private int price;

    public Pear(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Pear(int max, int min) {
        this.max = max;
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}

class PearExpressionListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("middle price: " + eb.get("middle"));
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}


public class Expression {
    public static void main(String[] args) {

        Configuration config = new Configuration();
        config.addEventType("PearEvent", Pear.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        /*
        x表示输入参数，而x.max和x.min都是x代表的事件流的属性，如果事件流没这个属性，expression的定义就是错误的
        expression的定义必须在使用它的句子之前完成。使用时直接写expression的名字和用圆括号包含要计算的参数即可
        再次提醒，expression的参数只能是事件流别名，即pear，别名的定义类似MySql里的as
        */
        String epl1 = "expression middle { x => (x.max+x.min)/2 } select middle(pear) as middle from PearEvent as pear";

        EPStatement statement = admin.createEPL(epl1);
        statement.addListener(new PearExpressionListener());

        EPRuntime er = esProvider.getEPRuntime();

        Pear pear1 = new Pear(5,20);
        er.sendEvent(pear1);
    }
}
