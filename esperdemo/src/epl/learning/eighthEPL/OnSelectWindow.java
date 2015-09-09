package epl.learning.eighthEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/9/9.
 * 如果select子句里是*，则返回的不仅仅是window中的事件，还会返回触发查询的事件
 * 并且返回的多行结果中每行都会包含这个触发事件
 */

//触发JavaBean
class OnSelectTrigger {
    private int trigger;

    public OnSelectTrigger(int trigger) {
        this.trigger = trigger;
    }

    public int getTrigger() {
        return trigger;
    }

    public void setTrigger(int trigger) {
        this.trigger = trigger;
    }

    @Override
    public String toString() {
        return "trigger=" + trigger;
    }
}

//事件JavaBean
class OnSelectEvent {
    private String name;
    private int size;

    @Override
    public String toString() {
        return "name=" + name + ", size=" + size;
    }

    public OnSelectEvent(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

//监听
class OnSelectWindowListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            System.out.println("Trigger on select");
            System.out.println("There is " + newEvents.length + " OnSelectEvent in OnSelectWindow");
            for (EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

public class OnSelectWindow {
    public static void main(String[] args) {
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime er = epService.getEPRuntime();

        String triggerEvent = OnSelectTrigger.class.getName();
        String selectEvent = OnSelectEvent.class.getName();

        //创建窗口
        String epl1 = "create window OnSelectWindow.win:length(2) as select * from " + selectEvent;
        //插入查询
        String epl2 = "insert into OnSelectWindow select * from " + selectEvent;
        //添加触发
        /*
            triggerEvent作为触发事件，查询OnSelectWindow中的所有事件作为结果返回
            过滤条件trigger >= 2
        */
        String epl3 = "on " + triggerEvent + "(trigger >= 2) select osw.* from OnSelectWindow as osw";

        System.out.println("Create Window: " + epl1);
        System.out.println("Trigger sentence: " + epl3);

        //注册EPL语句
        admin.createEPL(epl1);
        admin.createEPL(epl2);
        /*
        * 若过滤条件trigger >= 2不满足，则不会触发，从而也不会监听
        */
        EPStatement statement = admin.createEPL(epl3);
        //添加监听
        //添加3个事件，但OnSelectWindow.win:length(2)，此时事件1已经出去了
        statement.addListener(new OnSelectWindowListener());

        OnSelectEvent ose1 = new OnSelectEvent("ose1", 1);
        er.sendEvent(ose1);
        System.out.println("Send OnSelectEvent 1: " + ose1);

        OnSelectEvent ose2 = new OnSelectEvent("ose2", 2);
        er.sendEvent(ose2);
        System.out.println("Send OnSelectEvent 2: " + ose2);

        OnSelectEvent ose3 = new OnSelectEvent("ose3", 3);
        er.sendEvent(ose3);
        System.out.println("Send OnSelectEvent 3: " + ose3);


        OnSelectTrigger ost1 = new OnSelectTrigger(1);
        System.out.println("Send OnSelectTrigger " + ost1);
        er.sendEvent(ost1);
        OnSelectTrigger ost2 = new OnSelectTrigger(2);
        System.out.println("Send OnSelectTrigger " + ost2);
        er.sendEvent(ost2);


        System.out.println();

        // 每一次OnSelectWindow有变化并满足限制条件即可触发监听器返回window中的事件。
        String epl4 = "on OnSelectWindow select osw.* from OnSelectWindow as osw";
        EPStatement state4 = admin.createEPL(epl4);
        state4.addListener(new OnSelectWindowListener());
        System.out.println("Trigger sentence: " + epl4 + "\n");

        //事件4进入，事件2出去
        OnSelectEvent ose4 = new OnSelectEvent("ose4", 4);
        System.out.println("Send OnSelectEvent 4(also a Trigger): " + ose4 + "\n");
        er.sendEvent(ose4);

        //事件5进入，事件3出去
        OnSelectEvent ose5 = new OnSelectEvent("ose5", 5);
        System.out.println("Send OnSelectEvent 5(also a Trigger): " + ose5 + "\n");
        er.sendEvent(ose5);
    }
}
