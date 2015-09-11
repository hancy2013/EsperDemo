package epl.learning.ninth;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/9/11.
 */

class ForEvent {

    private String name;
    private int age;

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

    public String toString() {
        return "ForEvent{" + "name='" + name + '\'' + ", age=" + age + '}';
    }

    public ForEvent(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

class ForListener implements UpdateListener {

    // 用于记录调用次数
    private int num = 1;

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        System.out.println("invocation: " + num++);
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        } else {
            System.out.println("newEvent is null");
        }
    }
}

public class For {
    public static void main(String[] args) {
        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = esProvider.getEPAdministrator();
        EPRuntime er = esProvider.getEPRuntime();

        String forEvent = ForEvent.class.getName();

        String select = "select * from " + forEvent + ".win:length_batch(3)";
        //查询事件，按age分组
        String for1 = "select * from " + forEvent + ".win:length_batch(3) for grouped_delivery (age)";
        //查询事件，discrete_delivery每个事件都触发
        String for2 = "select * from " + forEvent + ".win:length_batch(3) for discrete_delivery";

        ForEvent forEvent1 = new ForEvent("wxm", 1);
        ForEvent forEvent2 = new ForEvent("imperio", 2);
        ForEvent forEvent3 = new ForEvent("wxmimperio", 1);

        //第一次打印输出
        EPStatement statement = admin.createEPL(select);
        statement.addListener(new ForListener());
        System.out.printf("select EPL1: " + select);
        System.out.println();
        er.sendEvent(forEvent1);
        er.sendEvent(forEvent2);
        er.sendEvent(forEvent3);
        statement.destroy();
        System.out.println();

        //按年龄分组，只触发两次
        EPStatement statement1 = admin.createEPL(for1);
        statement1.addListener(new ForListener());
        System.out.printf("for EPL2: " + for1);
        System.out.println();
        er.sendEvent(forEvent1);
        er.sendEvent(forEvent2);
        er.sendEvent(forEvent3);
        statement1.destroy();
        System.out.println();

        //每个事件都触发，所以分三组
        EPStatement statement2 = admin.createEPL(for2);
        statement2.addListener(new ForListener());
        System.out.println("for EPL3: " + for2);
        System.out.println();
        er.sendEvent(forEvent1);
        er.sendEvent(forEvent2);
        er.sendEvent(forEvent3);
    }
}
