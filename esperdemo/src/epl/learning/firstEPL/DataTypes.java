package epl.learning.firstEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/26.
 * 可用cast函数将其他的数值数据类型转化为BigDecimal
 * 用来对超过16位有效位的数进行精确的运算
 */

//javaBean封装事件
class Apple {
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

    public Apple(int id, int price) {
        this.id = id;
        this.price = price;
    }
}

//监听1
class CastDateTpyeListenerFirst implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("Average Price: " + eb.get("avgPrice") + " DataTpye is: " +
                        eb.get("avgPrice").getClass().getName());
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

//监听2
class CastDateTpyeListenerSecond implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("Average Price: " + eb.get("avgPrice") + " DataType is: " + eb.get("avgPrice").getClass().getName());
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

public class DataTypes {
    public static void main(String[] args) throws InterruptedException {

        Configuration config = new Configuration();
        config.addEventType("AppleEvent", Apple.class);
        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        /*
        EPL支持Java所有的数值数据类型，包括基本类型及其包装类，同时还支持java.math.BigInteger和java.math.BigDecimal
        并且能自动转换数据类型不丢失精度（比如short转int，int转short则不行）
        如果想在EPL内进行数据转换，可以使用cast函数
        */

        String epl1 = "select cast(avg(price), int) as avgPrice from AppleEvent.win:length_batch(2)";
        String epl2 = "select avg(price) as avgPrice from AppleEvent.win:length_batch(2)";

        //添加监听
        EPStatement statement1 = admin.createEPL(epl1);
        statement1.addListener(new CastDateTpyeListenerFirst());
        EPStatement statement2 = admin.createEPL(epl2);
        statement2.addListener(new CastDateTpyeListenerSecond());

        EPRuntime er = esProvider.getEPRuntime();

        //实例化事件
        Apple apple1 = new Apple(1, 20);
        er.sendEvent(apple1);
        Apple apple2 = new Apple(2, 50);
        er.sendEvent(apple2);
    }
}
