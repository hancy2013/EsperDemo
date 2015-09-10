package epl.learning.eighthEPL;

import com.espertech.esper.client.*;

import java.io.Serializable;

/**
 * Created by wxmimperio on 2015/9/10.
 */

//事件JavaBean
class SelectEvents implements Serializable {
    private String name;
    private int size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String toString() {
        return "name=" + name + ", size=" + size;
    }

    public SelectEvents(String name, int size) {
        this.name = name;
        this.size = size;
    }
}

public class SelectWindow {
    public static void main(String[] args) {
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();

        String SelectEvents = SelectEvents.class.getName();

        String epl1 = "create window SelectWindow.win:keepall() as select * from " + SelectEvents;
        String epl2 = "insert into SelectWindow select * from " + SelectEvents;

        //注册EPL语句
        admin.createEPL(epl1);
        admin.createEPL(epl2);

        //事件1
        SelectEvents se1 = new SelectEvents("se1", 1);
        runtime.sendEvent(se1);
        System.out.println("Send SelectEvent 1: " + se1);

        //事件2
        SelectEvents se2 = new SelectEvents("se2", 2);
        runtime.sendEvent(se2);
        System.out.println("Send SelectEvent 2: " + se2);

        /* 操作语句
         * 1. 查找窗体中的所有事件
         * 2. 更新size = 2事件的name = "update1"
         * 3. 删除所有size < 2的事件
         */
        String select = "select * from SelectWindow";
        String update = "update SelectWindow set name='update1' where size = 2";
        String delete = "delete from SelectWindow where size < 2";

        System.out.println("\nSelect SelectWindow!");
        //类似于数据库的查询操作，返回结果集
        EPOnDemandQueryResult selectResult = epService.getEPRuntime().executeQuery(select);
        EventBean[] events = selectResult.getArray();
        for (EventBean eb : events) {
            System.out.println(eb.getUnderlying());
        }

        // 更新size=2的事件，将name改为'update1'
        System.out.println("\nUpdate SelectEvent(size = 2) in SelectWindow!");
        EPOnDemandQueryResult updateResult = epService.getEPRuntime().executeQuery(update);
        events = updateResult.getArray();
        for (EventBean eb : events) {
            System.out.println(eb.getUnderlying());
        }

        System.out.println("\nSelect SelectWindow!");
        selectResult = epService.getEPRuntime().executeQuery(select);
        events = selectResult.getArray();
        for (EventBean eb : events) {
            System.out.println(eb.getUnderlying());
        }

        // 删除size<2的事件
        System.out.println("\nDelete SelectEvent(size < 2) in SelectWindow!");
        EPOnDemandQueryResult deleteResult = epService.getEPRuntime().executeQuery(delete);
        events = deleteResult.getArray();
        for (EventBean eb : events) {
            System.out.println(eb.getUnderlying());
        }

        System.out.println("\nSelect SelectWindow!");
        selectResult = epService.getEPRuntime().executeQuery(select);
        events = selectResult.getArray();
        for (EventBean eb : events) {
            System.out.println(eb.getUnderlying());
        }
    }
}
