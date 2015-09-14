package patterns.learing.FollowedBy;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/9/14.
 */

class LimitEvent {
    private int age;

    public LimitEvent(int age) {
        this.age = age;
    }

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

class LimitFollowedListener implements UpdateListener {

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            System.out.println("\nResult: ");
            for (EventBean eb : newEvents) {
                System.out.println("a=" + eb.get("a") + " b=" + eb.get("b"));
            }
            System.out.println();
        }
    }
}

public class LimitFollowed {
    public static void main(String[] args) {
        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = esProvider.getEPAdministrator();
        EPRuntime er = esProvider.getEPRuntime();

        String limit = LimitEvent.class.getName();
        String follow = FollowedEvent.class.getName();

        /* 在每次触发完成前最多只保留2个a事件，触发条件为b的size值大于a的age */
        String epl = "every a=" + limit + " -[2]> b=" + follow + "(size > a.age)";
        System.out.println("EPL: " + epl + "\n");

        //注册EPL语句
        EPStatement statement = admin.createPattern(epl);
        statement.addListener(new LimitFollowedListener());

        System.out.println("First Send!\n");

        //事件1，age = 1
        LimitEvent l1 = new LimitEvent(1);
        System.out.println("Send Event: " + l1);
        er.sendEvent(l1);

        //事件2，age = 2
        LimitEvent l2 = new LimitEvent(2);
        System.out.println("Send Event: " + l2);
        er.sendEvent(l2);

        //事件3，age = 0
        LimitEvent l3 = new LimitEvent(0);
        System.out.println("Send Event: " + l3);
        er.sendEvent(l3);

        //事件4，size = 3
        /*
         * 满足触发条件，但由于[2]的限制
         * 只能有age = 1，age = 2，两个事件
         */
        FollowedEvent f1 = new FollowedEvent();
        f1.setSize(3);
        System.out.println("Send Event: " + f1);
        er.sendEvent(f1);

        FollowedEvent f2 = new FollowedEvent();
        f2.setSize(4);
        System.out.println("Send Event: " + f2);
        er.sendEvent(f2);

        System.out.println();
        System.out.println("Second Send!\n");
        /*
        System.out.println("Send Event: " + l1);
        er.sendEvent(l1);
        System.out.println("Send Event: " + l2);
        er.sendEvent(l2);
        */
        /*
         * 由于当第一次触发时，[2]已经满足
         * 所以当第二次触发时，不用等待直接触发
         */
        System.out.println("Send Event: " + l3);
        er.sendEvent(l3);
        System.out.println("Send Event: " + f1);
        er.sendEvent(f1);
    }
}
