package fourth.esper;

import com.espertech.esper.client.*;


/**
 * Created by wxmimperio on 2015/8/25.
 * 从结果可以看得出来，每发送一个InitialEvent，都会新建一个context
 * 并且当发送TerminateEvent后，再发送OverLapping监听器也不会被触发了
 */
class InitialEvent {
}

class TerminateEvent {
}

//javaBean
class OverLapping {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OverLapping(int id) {
        this.id = id;
    }
}

//监听1
class overLappingListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("context.id: " + eb.get("id") + " id: " + eb.get("id"));
            }
        }
    }
}

//监听2
class overLappingListener2 implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("Class: " + eb.getUnderlying().getClass().getName() + " id: " + eb.get("id"));
            }
        }
    }
}

public class contextesper4 {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addEventType("InitialEvent", InitialEvent.class);
        config.addEventType("TerminateEvent", TerminateEvent.class);
        config.addEventType("OverLapping", OverLapping.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        String epl1 = "create context OverLapping initiated InitialEvent terminated TerminateEvent";
        String epl2 = "context OverLapping select context.id from InitialEvent";
        String epl3 = "context OverLapping select * from OverLapping";

        admin.createEPL(epl1);
        EPStatement statement1 = admin.createEPL(epl2);
        statement1.addListener(new overLappingListener());
        EPStatement statement2 = admin.createEPL(epl3);
        statement2.addListener(new overLappingListener2());

        EPRuntime er = esProvider.getEPRuntime();

        InitialEvent initial1 = new InitialEvent();
        System.out.println("sendEvent: initialEvent1");
        er.sendEvent(initial1);

        OverLapping overLapping = new OverLapping(20);
        System.out.println("sendEvent: overLapping");
        er.sendEvent(overLapping);

        InitialEvent initial2 = new InitialEvent();
        System.out.println("sendEvent:  initialEvent2");
        er.sendEvent(initial2);

        TerminateEvent terminateEvent = new TerminateEvent();
        System.out.println("sendEvent: terminateEvent");
        er.sendEvent(terminateEvent);

        OverLapping overLapping1 = new OverLapping(4);
        System.out.println("sendEvent: overLapping");
        er.sendEvent(overLapping1);
    }
}
