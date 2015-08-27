package fourth.esper;
import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/25.
 */

//起始
class StartEvent {
}
//结束
class EndEvent {
}
//javaBean
class NonOverlappingContext {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NonOverlappingContext(int id) {
        this.id = id;
    }
}

//监听
class NonOverlappingContextListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if(newEvents != null) {
            for(EventBean eb : newEvents) {
                System.out.println("Class: " + eb.getUnderlying().getClass().getName() + " id: " + eb.get("id"));
            }
        }
    }
}

public class contextesper3 {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addEventType("NonOverlappingContext",NonOverlappingContext.class);
        config.addEventType("StartEvent",StartEvent.class);
        config.addEventType("EndEvent", EndEvent.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        String epl1 = "create context NoOverLapping start StartEvent end EndEvent";
        String epl2 = "context NoOverLapping select * from NonOverlappingContext";

        admin.createEPL(epl1);
        EPStatement statement = admin.createEPL(epl2);
        statement.addListener(new NonOverlappingContextListener());

        EPRuntime er = esProvider.getEPRuntime();


        //设置起始点
        StartEvent start = new StartEvent();
        System.out.println("sendEvent: start~");
        er.sendEvent(start);

        NonOverlappingContext inlappingContext = new NonOverlappingContext(1);
        System.out.println("sendEvent: in");
        er.sendEvent(inlappingContext);

        //设置终止点
        EndEvent end = new EndEvent();
        System.out.println("sendEvent: end~");
        er.sendEvent(end);

        //超出context范围
        NonOverlappingContext overlappingContext = new NonOverlappingContext(30);
        System.out.println("sendEvent: out");
        er.sendEvent(overlappingContext);
    }
}
