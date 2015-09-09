package epl.learning.eighthEPL;

import com.espertech.esper.client.*;

import java.io.Serializable;

/**
 * Created by wxmimperio on 2015/9/9.
 * <p/>
 * OnUpdateEvent和平时我们定义的事件不太一样，它实现了Serializable接口
 * 这是因为update更新属性前会复制一份同样的事件暂存，比如initial这种操作就需要更新前的值，所以就需要我们实现序列化接口
 * 如果不想通过代码完成这个序列化要求，也可以通过配置完成，这个就不在这里说了。另外还有以下几点需要注意：
 * 1. 需要更新的属性一定要是可写的
 * 2. XML格式的事件不能通过此语句更新
 * 3. 嵌套属性不支持更新
 */

//触发JavaBean
class OnUpdateTrigger {
    private int trigger;

    public int getTrigger() {
        return trigger;
    }

    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }

    public String toString() {
        return "trigger=" + trigger;
    }

    public OnUpdateTrigger(int trigger) {
        this.trigger = trigger;
    }
}

//事件JavaBean
class OnUpdateEvent implements Serializable {
    private String name;
    private int size;
    private int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String toString() {
        return "name=" + name + ", size=" + size + ", price=" + price;
    }

    public OnUpdateEvent(String name, int size, int price) {
        this.name = name;
        this.size = size;
        this.price = price;
    }
}

//更新监听
class OnUpdateWindowListener implements UpdateListener {
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            System.out.println();
            System.out.println("Trigger On Update:");
            System.out.println("There is " + newEvents.length + " to be updated in OnUpdateWindow!");
            for (EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        }
    }
}

//查询监听
class OnUpdateSelectWindowListener implements UpdateListener {
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            System.out.println();
            System.out.println("Trigger On Select:");
            System.out.println("There is " + newEvents.length + " OnUpdateEvent in OnUpdateWindow!");
            for (EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        }
    }
}

public class OnUpdateWindow {
    public static void main(String[] args) {

        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();

        String triggerEvent = OnUpdateTrigger.class.getName();
        String updateEvent = OnUpdateEvent.class.getName();

        //创建窗口
        String epl1 = "create window OnUpdateWindow.win:keepall() as select * from " + updateEvent;
        String epl2 = "insert into OnUpdateWindow select * from " + updateEvent;

        /* 触发条件 trigger>0
         * 获取更新前数值，initial是关键字，所以不能省略
         */
        String epl3 = "on " + triggerEvent +
                "(trigger>0) as out update OnUpdateWindow as ouw set size=out.trigger, price=initial.size where out.trigger<ouw.price";
        String epl4 = "on " + triggerEvent + "(trigger=0) select ouw.* from OnUpdateWindow as ouw";

        System.out.println("Create Window: " + epl1);
        System.out.println("Update Trigger sentence: " + epl3);
        System.out.println();

        admin.createEPL(epl1);
        admin.createEPL(epl2);
        EPStatement state3 = admin.createEPL(epl3);
        state3.addListener(new OnUpdateWindowListener());

        EPStatement state4 = admin.createEPL(epl4);
        state4.addListener(new OnUpdateSelectWindowListener());

        /* 事件1
         * size=1，price=2
         */
        OnUpdateEvent oue1 = new OnUpdateEvent("oue1", 1, 2);
        runtime.sendEvent(oue1);
        System.out.println("Send OnUpdateEvent 1: " + oue1);

        /* 事件2
         * size=2，price=3
         */
        OnUpdateEvent oue2 = new OnUpdateEvent("oue2", 2, 3);
        runtime.sendEvent(oue2);
        System.out.println("Send OnUpdateEvent 2: " + oue2);

        /* 事件3
         * size=3，price=4
         */
        OnUpdateEvent oue3 = new OnUpdateEvent("oue3", 3, 4);
        runtime.sendEvent(oue3);
        System.out.println("Send OnUpdateEvent 3: " + oue3);

        /*
         * 触发1
         * trigger = 0
         * 对应epl4
         * 查看所有事件
         */
        OnUpdateTrigger ost1 = new OnUpdateTrigger(0);
        System.out.println("\nSend OnUpdateTrigger " + ost1);
        runtime.sendEvent(ost1);

        /*
         * 触发2
         * trigger = 2
         * 对应epl3
         * 只输出out.trigger<ouw.price的事件2、3
         * 然后被更新
         *     事件2: size = 2, price = 2
         *     事件3: size = 2, price = 3
         */
        OnUpdateTrigger ost2 = new OnUpdateTrigger(2);
        System.out.println("\nSend OnUpdateTrigger " + ost2);
        runtime.sendEvent(ost2);

        /*
         * 触发3
         * trigger = 2
         * 对应epl4
         * 输出所有事件
         *     事件1: size = 1, price = 2(没有更新过)
         *     事件2: size = 2, price = 2
         *     事件3: size = 2, price = 3
         */
        OnUpdateTrigger ost3 = new OnUpdateTrigger(0);
        System.out.println("\nSend OnUpdateTrigger " + ost3);
        runtime.sendEvent(ost3);
    }
}
