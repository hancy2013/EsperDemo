package patterns.learing.everys;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/9/14.
 */

class EveryDistinctEvent {

    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String toString() {
        return "EveryDistinctEvent{" + "num=" + num + '}';
    }

    public EveryDistinctEvent(int num) {
        this.num = num;
    }
}

class LimitEvent {
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "LimitEvent{" + "age=" + age + '}';
    }
}

class EveryDistinctListener implements UpdateListener {

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            System.out.println("\nResult: ");
            for (EventBean eb : newEvents) {
                System.out.println(eb.get("a"));
            }
        }
    }
}

public class EveryDistinct {
    public static void main(String[] args) {
        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = esProvider.getEPAdministrator();
        EPRuntime er = esProvider.getEPRuntime();

        String everyDistinct = EveryDistinctEvent.class.getName();
        String limit = LimitEvent.class.getName();

        //针对每一个everyDistinct事件，过滤a.num，且不重复
        String epl1 = "every-distinct(a.num) a=" + everyDistinct;
        System.out.println("EPL1:" + epl1);
        EPStatement statement = admin.createPattern(epl1);
        statement.addListener(new EveryDistinctListener());

        //事件1
        EveryDistinctEvent ed1 = new EveryDistinctEvent(1);

        //事件2
        EveryDistinctEvent ed2 = new EveryDistinctEvent(2);

        //事件3
        /*
         * 由于在此设置了去重，所以事件3就不会被触发
         */
        EveryDistinctEvent ed3 = new EveryDistinctEvent(1);

        System.out.println("\nSend Event: " + ed1);
        er.sendEvent(ed1);
        System.out.println("\nSend Event: " + ed2);
        er.sendEvent(ed2);
        System.out.println("\nSend Event: " + ed3);
        er.sendEvent(ed3);

        statement.destroy();

        /*
         * every-distinct后面的子表达式是EveryDistinctEvent and not LimitEvent
         * 所以在发送EveryDistinctEvent之后发送LimitEvent，就导致子表达式false
         * 所以在此发送num=1的EveryDistinctEvent时监听器被触发
         */
        String epl2 = "every-distinct(a.num) (a=" + everyDistinct + " and not " + limit + ")";
        System.out.println("\nEPL2: " + epl2);
        EPStatement statement1 = admin.createPattern(epl2);
        statement1.addListener(new EveryDistinctListener());

        LimitEvent l1 = new LimitEvent();

        System.out.println("\nSend Event: " + ed1);
        er.sendEvent(ed1);
        System.out.println("\nSend Event: " + ed2);
        er.sendEvent(ed2);
        System.out.println("\nSend Event: " + l1);
        er.sendEvent(l1);
        System.out.println("\nSend Event: " + ed3);
        er.sendEvent(ed3);

        statement1.destroy();

        //预设时间3s后，该事件可以继续重复触发
        String epl3 = "every-distinct(a.num, 3 sec) a=" + everyDistinct;
        System.out.println("\nEPL3: " + epl3);
        EPStatement stat3 = admin.createPattern(epl3);
        stat3.addListener(new EveryDistinctListener());

        System.out.println("\nSend Event: " + ed1);
        er.sendEvent(ed1);
        System.out.println("\nSend Event: " + ed2);
        er.sendEvent(ed2);
        System.out.println("\nSleep 3 seconds!");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("\nSend Event: " + ed3);
        er.sendEvent(ed3);
    }
}
