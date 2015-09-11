package epl.learning.ninth;

import com.espertech.esper.client.*;

import java.io.Serializable;

/**
 * Created by wxmimperio on 2015/9/11.
 */

/*
 * 如果事件是POJO，那么要实现java.io.Serializable接口
 * 因为引擎内部的update操作实际上是要先深复制原事件再更新拷贝后的事件
 * 不会对原事件作出任何修改。
 */
class UpdateEventBean implements Serializable {
    private int id;
    private String name;

    @Override
    public String toString() {
        return "UpdateEvent{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

    public UpdateEventBean(int id, String name) {
        this.id = id;
        this.name = name;
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
}

class UpdateEventListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println("newEvent: " + eb.getUnderlying());
            }
        }

        if (oldEvents != null) {
            for (EventBean eb : oldEvents) {
                System.out.println("oldEvents: " + eb.getUnderlying());
            }
        }
    }
}

public class UpdateEvent {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.configure("esper.examples.cfg.xml");
        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = esProvider.getEPAdministrator();
        EPRuntime er = esProvider.getEPRuntime();
        UpdateEventListener listener = new UpdateEventListener();

        //事件1
        UpdateEventBean ue = new UpdateEventBean(1, "wxm");

        //事件2
        UpdateEventBean ue2 = new UpdateEventBean(2, "imperio");

        /*
         * 如果在update句首用@Priority这个注解，那么更新事件的顺序是以优先级最高的最先更新
         * 所以我建议大家在同时使用多个update句子的时候，最好使用优先级
         * 因为你很可能搞不清楚所有update句子的创建顺序
         */
        String updateEvent = UpdateEventBean.class.getName();
        String select = "select * from " + updateEvent;
        String update1 = "@Priority(2)update istream " + updateEvent + " set name = 'updateName1' where id = 1";
        String update2 = "@Priority(1)update istream " + updateEvent + " set name = 'updtaeName2' where id = 2";

        //查找所有事件
        EPStatement statement = admin.createEPL(select);
        statement.addListener(listener);
        System.out.println("select EPL: " + select);
        er.sendEvent(ue);
        er.sendEvent(ue2);
        System.out.println();

        /*
         * 1. 更新wxm为updateName1为newEvent
         * 2. wxm为oldEvent
         * 3. selet出事件
         *    id = 1,name = updateName1
         *    id = 2,name = imperio
         */
        EPStatement statement1 = admin.createEPL(update1);
        statement1.addListener(listener);
        System.out.println("update1 EPL: " + update1);
        er.sendEvent(ue);
        er.sendEvent(ue2);
        System.out.println();

        /*
         * 1. update1的结果
         * 更新imperio为updateName2
         * imperio为oldEvent
         * select结果
         * id = 1,name = updateName1
         * id = 2,name = updateName2
         */
        EPStatement statement2 = admin.createEPL(update2);
        statement2.addListener(listener);
        System.out.println("update2 EPL: " + update2);
        er.sendEvent(ue);
        er.sendEvent(ue2);
    }
}
