package patterns.learing.ControllingEventConsumption;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/9/11.
 *
 * 因为Pattern可以由多个原子事件组成，那么Filter自然也会有多个
 * 正常情况下，所有的Filter都会对进入引擎的事件进行判定
 * 但是我们也有只需要判定一次的时候，只要满足了某个Filter，那么其他的Filter就不用管这个事件了
 * Esper考虑到了这个需求，我们只需要在Filter表达式后面加个@consume注解即可
 * 此注解可以跟随数字，表示过滤的优先级。默认优先级为1，数值越大优先级越高
 */

class ConsumeEvent {
    private int id;
    private String name;
    private int age;

    @Override
    public String toString() {
        return "ConsumeEvent{" + "id=" + id + ", name='" + name + '\'' + ", age=" + age + '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

class PatternConsumeListener1 implements UpdateListener {

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("a: " + eb.get("a"));
                System.out.println("b: " + eb.get("b"));
            }
        }
    }
}

class PatternConsumeListener2 implements UpdateListener {

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("a: " + eb.get("a"));
                System.out.println("b: " + eb.get("b"));
                System.out.println("c: " + eb.get("c"));
            }
        }
    }
}

public class PatternConsume {
    public static void main(String[] args) {
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime er = epService.getEPRuntime();

        String consume = ConsumeEvent.class.getName();

        /*
         * 过滤出id = 1 且 name = 'wxm'的事件
         * @consume判定一次filter就不用在判定
         */
        String epl1 = "every (a=" + consume + "(id = 1)@consume and b=" + consume + "(name = 'wxm'))";
        System.out.println("EPL1: " + epl1 + "\n");

        EPStatement stat1 = admin.createPattern(epl1);
        stat1.addListener(new PatternConsumeListener1());

        //事件1
        ConsumeEvent ce1 = new ConsumeEvent();
        ce1.setId(1);
        ce1.setName("wxm");
        System.out.println("Send Event: " + ce1);
        er.sendEvent(ce1);

        //事件2
        ConsumeEvent ce2 = new ConsumeEvent();
        ce2.setId(2);
        ce2.setName("wxm");
        System.out.println("Send Event: " + ce2);
        er.sendEvent(ce2);

        stat1.destroy();
        System.out.println();

        /*
         * 过滤出id = 1 或者 name = 'imperio'的事件 或者 age > 2
         * @consume判定一次filter就不用在判定，且有优先级3 > 2
         */
        String epl2 = "every (a=" + consume + "(id = 1)@consume(2) or b=" + consume + "(name = 'imperio')@consume(3) or c="
                + consume + "(age > 2))";
        System.out.println("EPL2: " + epl2 + "\n");
        EPStatement stat2 = admin.createPattern(epl2);
        stat2.addListener(new PatternConsumeListener2());

        //事件3，b的优先级最高
        ConsumeEvent ce3 = new ConsumeEvent();
        ce3.setId(1);
        ce3.setName("imperio");
        ce3.setAge(3);
        System.out.println("Send Event: " + ce3);
        er.sendEvent(ce3);

        //事件4，不满足b，但a的优先级高
        ConsumeEvent ce4 = new ConsumeEvent();
        ce4.setId(1);
        ce4.setName("wxm");
        ce4.setAge(1);
        System.out.println("Send Event: " + ce4);
        er.sendEvent(ce4);

        //事件5，不满足a、b，满足c
        ConsumeEvent ce5 = new ConsumeEvent();
        ce5.setId(3);
        ce5.setName("wxm");
        ce5.setAge(5);
        System.out.println("Send Event: " + ce5);
        er.sendEvent(ce5);
    }
}
