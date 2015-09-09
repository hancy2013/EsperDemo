package epl.learning.eighthEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/28.
 */

class SelectEvent {
    private int price;
    private String name;

    public SelectEvent(int price, String name) {
        this.price = price;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "name=" + name + ", price=" + price;
    }
}

class SelectNameWindowListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            System.out.println("\nThere is " + newEvents.length + " events to be return!");
            for (EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

public class SelectNamedWindow {
    public static void main(String[] args) {
        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = esProvider.getEPAdministrator();
        EPRuntime er = esProvider.getEPRuntime();

        String selectEvent = SelectEvent.class.getName();

        //EPL注册窗口，设置窗口大小length_batch(3)，即满三个事件向外输出
        String epl1 = "create window SelectNameWindow.win:length_batch(3) as " + selectEvent;
        System.out.println("Create named window: create window SelectNamedWindow.win:length_batch(3) as " + selectEvent);
        admin.createEPL(epl1);

        //EPL注册插入窗口
        String epl2 = "insert into SelectNameWindow select * from " + selectEvent;
        admin.createEPL(epl2);

        //事件1
        SelectEvent selectEvent1 = new SelectEvent(1, "seEvent1");
        System.out.println("Send SelecEvent1 " + selectEvent1);
        er.sendEvent(selectEvent1);

        //事件2
        SelectEvent selectEvent2 = new SelectEvent(2, "seEvent2");
        System.out.println("Send SelecEvent2 " + selectEvent2);
        er.sendEvent(selectEvent2);

        /*//事件3
        SelectEvent selectEvent3 = new SelectEvent(2, "seEvent3");
        System.out.println("Send SelecEvent3 " + selectEvent3);
        er.sendEvent(selectEvent3);*/

        /*//事件4
        SelectEvent selectEvent4 = new SelectEvent(3, "seEvent4");
        System.out.println("Send SelecEvent1 " + selectEvent4);
        er.sendEvent(selectEvent4);*/


        String epl3 = "select * from SelectNameWindow(price>=2)";
        EPStatement statement = admin.createEPL(epl3);
        statement.addListener(new SelectNameWindowListener());
        System.out.println("Register select sentence: select * from SelectNamedWindow(price>=2)");


        /*
        * 1. 前两个事件进入时没有设置监听，也不够length_batch(3)条件，所以不输出
        * 2. 当事件3进入时，达到了length_batch(3)条件，将3个事件输出，但此时没有添加监听，所以无法打印输出监听
        *    后面添加监听，再添加事件5时已经不会有输出，因为newEvent只有事件5，不满足条件不输出
        * 3. 若在添加监听之前没有事件3进入，则原本有两个事件
        *    此时添加监听，再添加事件5，则会输出这三个事件，因为事件5导致满足length_batch(3)条件
        */

        //事件5
        SelectEvent selectEvent5 = new SelectEvent(1, "seEvent5");
        System.out.println("Send SelecEvent5 " + selectEvent5);
        er.sendEvent(selectEvent5);

    }
}
