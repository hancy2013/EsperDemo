package epl.learning.eighthEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/9/9.
 */

//触发JavaBean
class OnDeleteTrigger {
    private int trigger;

    public OnDeleteTrigger(int trigger) {
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
class OnDeleteEvent {
    private String name;
    private int size;

    @Override
    public String toString() {
        return "name=" + name + ", size=" + size;
    }

    public OnDeleteEvent(String name, int size) {
        this.name = name;
        this.size = size;
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
}

class OnDeleteWindowListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if(newEvents != null) {
            System.out.println();
            System.out.println("Trigger on delete: ");
            System.out.println("There is " + newEvents.length + " OnDeleteEvent to deleted in OnDeleteWindow");
            for(EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        }else {
            System.out.println("newEvent is null");
        }
    }
}

class OnSelectedWindowListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if(newEvents != null) {
            System.out.println();
            System.out.println("Trigger on select: ");
            System.out.println("There is " + newEvents.length + " OnselectEvent to selected in OnSelectWindow");
            for(EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        }else {
            System.out.println("newEvent is null");
        }
    }
}

public class OnDeleteWindow {
    public static void main(String[] args) {
        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = esProvider.getEPAdministrator();
        EPRuntime er = esProvider.getEPRuntime();

        String triggerEvent = OnDeleteTrigger.class.getName();
        String deleteEvent = OnDeleteEvent.class.getName();

        //创建窗口,保留所有事件
        String epl1 = "create window OnDeleteWindow.win:keepall() as select * from " + deleteEvent;
        String epl2 = "insert into OnDeleteWindow select * from " + deleteEvent;
        /*
        * 1. 触发条件trigger > 0
        * 2. 触发为odt，窗口为odw
        * 3. 当odt.trigger = odw.size时才获取
        */
        String epl3 = "on " + triggerEvent + "(trigger > 0) as odt delete from OnDeleteWindow as odw where odt.trigger = odw.size";
        /*
        * 1. 触发条件trigger = 0
        * 2. 获取窗口下所有事件
        */
        String epl4 = "on " + triggerEvent + "(trigger = 0) select odw.* from OnDeleteWindow as odw";

        System.out.println("Create Window: " + epl1);
        System.out.println("Delete Trigger: " + epl3);
        System.out.println("Select Trigger: " + epl4);
        System.out.println();

        //注册EPL语句
        admin.createEPL(epl1);
        admin.createEPL(epl2);

        //给触发添加监听
        EPStatement statement  = admin.createEPL(epl3);
        statement.addListener(new OnDeleteWindowListener());
        EPStatement statement1 = admin.createEPL(epl4);
        statement1.addListener(new OnSelectedWindowListener());

        //事件1
        OnDeleteEvent ose1 = new OnDeleteEvent("ose1",1);
        er.sendEvent(ose1);
        System.out.println("Send OnDeleteEvent 1: " + ose1);

        //事件2
        OnDeleteEvent ose2 = new OnDeleteEvent("ose2",2);
        er.sendEvent(ose2);
        System.out.println("Send OnDeleteEvent 2: " + ose2);

        //事件3
        OnDeleteEvent ose3 = new OnDeleteEvent("ose3",3);
        er.sendEvent(ose3);
        System.out.println("Send OnDeleteEvent 3: " + ose3);


        /* 触发1
         * trigger = 0
         * 对应epl4语句
         * select监听打印全部事件信息
         */
        OnDeleteTrigger ost1 = new OnDeleteTrigger(0);
        System.out.println("\nSend OnSelectTrigger " + ost1);
        er.sendEvent(ost1);

        /* 触发2
         * trigger = 2
         * 对应epl3语句
         * 删除odt.trigger = odw.size的事件
         * delete监听，输出删除事件信息
         */
        OnDeleteTrigger ost2 = new OnDeleteTrigger(2);
        System.out.println("\nSend OnDeleteTrigger " + ost2);
        er.sendEvent(ost2);

        /*
         * 触发3
         * trigger = 0
         * 对应epl4语句
         * select监听，打印全部事件信息，已经没有了删除的事件2信息
         */
        OnDeleteTrigger ost3 = new OnDeleteTrigger(0);
        System.out.println("\nSend OnSelectTrigger " + ost3);
        er.sendEvent(ost3);
    }
}
