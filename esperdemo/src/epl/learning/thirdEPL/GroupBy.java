package epl.learning.thirdEPL;
import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/27.
 */

class AppleGroupByEvent {
    private int price;
    private String color;

    public AppleGroupByEvent(String color, int price) {
        this.color = color;
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

class AppleGroupByListener implements  UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if(newEvents != null) {
            for(EventBean eb : newEvents) {
                System.out.println("avgPrice: " + eb.get("avgPrice") + " color: " + eb.get("color") + " count: " + eb.get("count(*)"));
            }
        }else {
            System.out.println("newEvent is null");
        }
    }
}

/*
    1. reclaim_group_aged
    该属性后面跟着的是正整数，以秒为单位，表示在n秒内，若分组的数据没有进行更新，则分组数据被Esper回收
    2. reclaim_group_freq
    该属性后面跟着的是正整数，以秒为单位，表示每n秒清理一次分组，可清理的分组是reclaim_group_aged决定的
    也就是说要使用该参数，就要配合reclaim_group_aged一起使用
*/
public class GroupBy {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addEventType("GroupByEvent",AppleGroupByEvent.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        // 根据color对10秒内进入的GroupByEvent事件进行分组计算平均price。对5秒内没有数据更新的分组进行回收,每1秒回收一次
        String epl1 = "@Hint('reclaim_group_aged=5,reclaim_group_freq=1')select avg(price) as avgPrice, color, count(*) from GroupByEvent.win:time(5) group by color";

        EPStatement statement = admin.createEPL(epl1);
        statement.addListener(new AppleGroupByListener());

        EPRuntime er = esProvider.getEPRuntime();

        AppleGroupByEvent appleGB = new AppleGroupByEvent("blue",20);
        er.sendEvent(appleGB);
        AppleGroupByEvent appleGB1 = new AppleGroupByEvent("blue",50);
        er.sendEvent(appleGB1);
        AppleGroupByEvent appleGB2 = new AppleGroupByEvent("red",50);
        er.sendEvent(appleGB2);
    }
}
