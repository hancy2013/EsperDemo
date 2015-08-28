package epl.learning.seventhEPL;

import com.espertech.esper.client.*;

import java.util.Iterator;

/**
 * Created by wxmimperio on 2015/8/28.
 */

class Time {
    private int time;

    public Time(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}

class InvocationMethodListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("listener: " + eb.getUnderlying());
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

public class InvocationMethodDemo {
    public static void main(String[] args) {
        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = esProvider.getEPAdministrator();

        String timesName = Time.class.getName();
        String ijName = InvocationMethodJava.class.getName();
        String epl1 = "select invocationjava.* from " + timesName + " as invojava, method:" + ijName +
                ".getJavaObject(time) as invocationjava";

        EPStatement statement = admin.createEPL(epl1);
        statement.addListener(new InvocationMethodListener());

        EPRuntime er = esProvider.getEPRuntime();
        Time times = new Time(2);
        er.sendEvent(times);

        /**
         * 调用外部方法返回map类型数据
         */
        String imName = InvocationMethodMap.class.getName();

        String epl2 = "select * from method:" + imName + ".getMapObject()";

        EPStatement statement1 = admin.createEPL(epl2);

        Iterator<EventBean> iter = statement1.iterator();

        while (iter.hasNext()) {
            EventBean eventBean = iter.next();
            System.out.println(eventBean.getUnderlying());
        }

    }
}
