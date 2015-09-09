package epl.learning.eighthEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/9/9.
 */
//触发JavaBean
class OnSelectDeleteTrigger {
    private int trigger;

    public int getTrigger() {
        return trigger;
    }

    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }

    public OnSelectDeleteTrigger(int trigger) {
        this.trigger = trigger;
    }

    public String toString() {
        return "trigger=" + trigger;
    }
}

//事件JavaBean
class OnSelectDeleteEvent {
    private String name;
    private int size;

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
        return "name=" + name + ", size=" + size;
    }

    public OnSelectDeleteEvent(String name, int size) {
        this.name = name;
        this.size = size;
    }
}

class OnSelectDeleteWindowListener implements UpdateListener {
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            System.out.println();
            System.out.println("Trigger On Select and Delete:");
            System.out.println("There is " + newEvents.length + " OnSelectDeleteEvent in OnSelectDeleteWindow!");
            for (EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        }
    }
}

public class OnSelectDeleteWindow {
    public static void main(String[] args) {
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();


        String triggerEvent = OnSelectDeleteTrigger.class.getName();
        String selectDeleteEvent = OnSelectDeleteEvent.class.getName();

        String epl1 = "create window OnSelectDeleteWindow.win:keepall() as select * from " + selectDeleteEvent;
        String epl2 = "insert into OnSelectDeleteWindow select * from " + selectDeleteEvent;
        //查询出来并删除
        String epl3 = "on " + triggerEvent + " select and delete osw.* from OnSelectDeleteWindow as osw";

        System.out.println("Create Window: " + epl1);
        System.out.println("Select and Delete Trigger: " + epl3);
        System.out.println();

        //注册EPL语句
        admin.createEPL(epl1);
        admin.createEPL(epl2);
        //添加监听
        EPStatement state3 = admin.createEPL(epl3);
        state3.addListener(new OnSelectDeleteWindowListener());

        //事件1
        OnSelectDeleteEvent osde1 = new OnSelectDeleteEvent("osde1", 1);
        runtime.sendEvent(osde1);
        System.out.println("Send OnSelectDeleteEvent 1: " + osde1);

        //事件2
        OnSelectDeleteEvent osde2 = new OnSelectDeleteEvent("osde2", 2);
        runtime.sendEvent(osde2);
        System.out.println("Send OnSelectDeleteEvent 2: " + osde2);

        //事件3
        OnSelectDeleteEvent osde3 = new OnSelectDeleteEvent("osde3", 3);
        runtime.sendEvent(osde3);
        System.out.println("Send OnSelectDeleteEvent 3: " + osde3);

        /*
         * 触发1
         * 对应epl3语句，查询出来事件1、2、3，并全部删除
         */
        OnSelectDeleteTrigger osdt1 = new OnSelectDeleteTrigger(1);
        System.out.println("Send OnSelectDeleteTrigger " + osdt1);
        runtime.sendEvent(osdt1);

        System.out.println();

        //事件4
        OnSelectDeleteEvent osde4 = new OnSelectDeleteEvent("osde4", 4);
        System.out.println("Send OnSelectDeleteEvent 4: " + osde4);
        runtime.sendEvent(osde4);

        /* 触发2
         * 此时事件1、2、3已经被删除
         * 查询出来并删除的是刚才添加的事件4
         */
        OnSelectDeleteTrigger osdt2 = new OnSelectDeleteTrigger(1);
        System.out.println("Send OnSelectDeleteTrigger " + osdt2);
        runtime.sendEvent(osdt2);
    }
}
