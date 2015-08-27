package epl.learning.secondEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/26.
 */

class Area {
    private float length;
    private float width;

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float computArea() {
        return length * width;
    }

    public Area(float length, float width) {
        this.length = length;
        this.width = width;
    }
}

class CumputAreaListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("Area: " + eb.get("area"));
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

public class SelectUseFunction {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addEventType("computArea", Area.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        String epl1 = "select area.computArea() as area from computArea as area";

        EPStatement statement = admin.createEPL(epl1);

        statement.addListener(new CumputAreaListener());

        EPRuntime er = esProvider.getEPRuntime();

        Area area1 = new Area(100,200);
        er.sendEvent(area1);
    }
}
