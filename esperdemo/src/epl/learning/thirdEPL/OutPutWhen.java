package epl.learning.thirdEPL;

import com.espertech.esper.client.*;

import java.util.Random;

/**
 * Created by wxmimperio on 2015/8/27.
 *
 * 在使用when的时候，有两点需要注意：
 * 1.当trigger_expression返回true时，Esper会输出从上一次输出之后到这次输出之间所有的insert stream和remove stream。
 * 2.若trigger_expression不断被触发并返回true时，则Esper最短的输出间隔为100毫秒。
 * 3.expression不能包含事件流的属性，聚合函数以及prev函数和prior函数
 */

class AppleOutPutWhen {
    private int id;
    private int price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String toString() {
        return "id: " + id + ", price: " + price;
    }

    public AppleOutPutWhen(int id, int price) {
        this.id = id;
        this.price = price;
    }
}

class WhenOutPutListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("OutPut: " + eb.getUnderlying());
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}
/*
当price大于5的时候，设置exceed变量为true，即可输出之前进入的所有事件
之后set子句将exceed设置为false，等待下一次exceed=true时触发输出
*/
public class OutPutWhen {
    public static void main(String[] args) throws InterruptedException {
        Configuration config = new Configuration();
        config.addEventType("OutPutWhen", AppleOutPutWhen.class);
        //给EPL注册一个exceed布尔类型变量
        config.addVariable("exceed", boolean.class, false);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        // 当exceed为true时，输出所有进入EPL的事件，然后设置exceed为false
        String epl1 = "select * from OutPutWhen output when exceed then set exceed=false";

        EPStatement statement = admin.createEPL(epl1);
        statement.addListener(new WhenOutPutListener());

        EPRuntime er = esProvider.getEPRuntime();

        Random r = new Random();

        for (int i = 0; i < 10; i++) {
            int price = r.nextInt(10);
            AppleOutPutWhen appleOutPutWhen = new AppleOutPutWhen(i, price);
            System.out.println("sendEvent: " + appleOutPutWhen);
            er.sendEvent(appleOutPutWhen);

            // 当price大于5时，exceed变量为true
            if (price > 5) {
                er.setVariableValue("exceed", true);
                // 因为主线程和输出线程不是同一个，所以这里休息1秒保证输出线程将事件全部输出，方便演示。
                Thread.sleep(1000);
            }
        }
    }
}
