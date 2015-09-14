package patterns.learing.FollowedBy;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/9/11.
 * 例子中的pattern是由every和followed-by两个结构组合而成
 * 所实现的效果是针对每一个事件，都监听其follow后同类型的事件的size值小于follow前的事件
 * 只要满足pattern定义，通过get对应的别名就可以获得触发时的具体事件
 */

class FollowedEvent {

    private int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String toString() {
        return "FollowedEvent{" + "size=" + size + '}';
    }
}

class PatternFollowedListener implements UpdateListener {

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println();
                System.out.println("Result:");
                System.out.println(eb.get("a") + " " + eb.get("b"));
            }
        }
    }
}

public class PatternFollowed {
    public static void main(String[] args) {
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime er = epService.getEPRuntime();

        String followed = FollowedEvent.class.getName();

        //监听a事件之后，所有size比a.size大的事件
        String epl = "select * from pattern [every a=" + followed + " -> b=" + followed + "(size < a.size)]";
        System.out.println("EPL: " + epl + "\n");
        EPStatement stat = admin.createEPL(epl);
        stat.addListener(new PatternFollowedListener());

        //事件1进入a.size = 1
        FollowedEvent f1 = new FollowedEvent();
        f1.setSize(1);
        System.out.println("Send Event1: " + f1);
        er.sendEvent(f1);
        System.out.println();

        //事件2进入，b.size = 3 > a.size = 1,不满足pattern，事件不被监听
        FollowedEvent f2 = new FollowedEvent();
        f2.setSize(3);
        System.out.println("Send Event2: " + f2);
        er.sendEvent(f2);
        System.out.println();

        //事件3进入，b.size = 2 < a.size = 3,事件被监听，输出事件3
        FollowedEvent f3 = new FollowedEvent();
        f3.setSize(2);
        System.out.println("Send Event3: " + f3);
        er.sendEvent(f3);
    }
}
