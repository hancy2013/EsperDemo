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
        return "name="+name+", price="+price;
    }
}

class SelectNameWindowListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if(newEvents != null) {
            System.out.println("\nThere is "+newEvents.length+" events to be return!");
            for (EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        }else {
            System.out.println("newEvent is null");
        }
    }
}

public class SelectNamedWindow {
    public static void main(String[] args) {
        EPServiceProvider esProvider  = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = esProvider.getEPAdministrator();
        EPRuntime er = esProvider.getEPRuntime();

        String selectEvent = SelectEvent.class.getName();

        //EPL注册窗口
        String epl1 = "create window SelectNameWindow.win:length_batch(3) as " + selectEvent;
        System.out.println("Create named window: create window SelectNamedWindow.win:length_batch(3) as "+selectEvent);
        admin.createEPL(epl1);

        //EPL注册插入窗口
        String epl2 = "insert into SelectNameWindow select * from " + selectEvent;
        admin.createEPL(epl2);

        SelectEvent selectEvent1 = new SelectEvent(1,"seEvent1");
        System.out.println("Send SelecEvent1 " + selectEvent1);
        er.sendEvent(selectEvent1);

        SelectEvent selectEvent2 = new SelectEvent(2,"seEvent2");
        System.out.println("Send SelecEvent1 " + selectEvent2);
        er.sendEvent(selectEvent2);

        String epl3 = "select * from SelectNameWindow(price >= 2)";
        EPStatement statement = admin.createEPL(epl3);
        statement.addListener(new SelectNameWindowListener());
        System.out.println("Register select sentence: select * from SelectNamedWindow(price>=2)");


        SelectEvent selectEvent3 = new SelectEvent(3,"seEvent3");
        System.out.println("Send SelecEvent1 " + selectEvent3);
        er.sendEvent(selectEvent3);

    }
}
