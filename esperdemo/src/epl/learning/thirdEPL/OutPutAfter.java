package epl.learning.thirdEPL;
import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/27.
 */

class AppleOutPutAfter {
    private int id;
    private int price;

    public AppleOutPutAfter(int id, int price) {
        this.id = id;
        this.price = price;
    }

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
        return "id: " + id + " price: " + price;
    }
}

class OutPutAfterListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if(newEvents != null) {
            for(EventBean eb : newEvents) {
                int price = (Integer)eb.get("sumPrice");
                System.out.println("sumPrice: " + price);
            }
        }else {
            System.out.println("newEvent is null");
        }
    }
}

/*
    after之后的every子句要等到after后面的表达式满足后才生效
    所以第一个事件进入后，every 2 events生效，即等待两个事件进入后才输出结果
    对于时间也是要等到after的子句满足后才开始计时
*/
public class OutPutAfter {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addEventType("OutPutAfter",AppleOutPutAfter.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        // 统计最新3个事件的sumPrice，并且从EPL可用起，等待第一个事件进入后，以每两个事件进入的频率输出统计结果
        String epl1 = "select sum(price) as sumPrice from OutPutAfter.win:length(3) output after 1 events snapshot every 2 events";

        EPStatement statement = admin.createEPL(epl1);
        statement.addListener(new OutPutAfterListener());

        EPRuntime er = esProvider.getEPRuntime();

        AppleOutPutAfter appleOutPut1 = new AppleOutPutAfter(1,6);
        System.out.println("sendEvent: " + appleOutPut1);
        er.sendEvent(appleOutPut1);

        AppleOutPutAfter appleOutPut2 = new AppleOutPutAfter(2,3);
        System.out.println("sendEvent: " + appleOutPut2);
        er.sendEvent(appleOutPut2);

        AppleOutPutAfter appleOutPut3 = new AppleOutPutAfter(3,1);
        System.out.println("sendEvent: " + appleOutPut3);
        er.sendEvent(appleOutPut3);

        AppleOutPutAfter appleOutPut4 = new AppleOutPutAfter(4,2);
        System.out.println("sendEvent: " + appleOutPut4);
        er.sendEvent(appleOutPut4);

        AppleOutPutAfter appleOutPut5 = new AppleOutPutAfter(5,4);
        System.out.println("sendEvent: " + appleOutPut5);
        er.sendEvent(appleOutPut5);
    }
}
