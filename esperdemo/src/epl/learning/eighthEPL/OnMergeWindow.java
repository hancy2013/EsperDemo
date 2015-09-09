package epl.learning.eighthEPL;

import com.espertech.esper.client.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxmimperio on 2015/9/9.
 */

//事件JavaBean
class MergeEvent implements Serializable {

    private int mergeId;
    private String mergeStr;
    private int mergeSize;
    private boolean deleteFlag;

    public int getMergeId() {
        return mergeId;
    }

    public void setMergeId(int mergeId) {
        this.mergeId = mergeId;
    }

    public String getMergeStr() {
        return mergeStr;
    }

    public void setMergeStr(String mergeStr) {
        this.mergeStr = mergeStr;
    }

    public int getMergeSize() {
        return mergeSize;
    }

    public void setMergeSize(int mergeSize) {
        this.mergeSize = mergeSize;
    }

    public boolean isDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String toString() {
        return "mergeId=" + mergeId + ", mergeStr=" + mergeStr + ", mergeSize=" + mergeSize + ", deleteFlag=" + deleteFlag;
    }

}

class OnMergeWindowlistener implements UpdateListener {

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            System.out.println("Trigger MergeWindow:");
            for (EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        }
    }
}

class SelectLogWindowlistener implements UpdateListener {

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        }
    }
}

class SelectMergeWindowlistener implements UpdateListener {

    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            for (EventBean eb : newEvents) {
                System.out.println(eb.getUnderlying());
            }
        }
    }
}

public class OnMergeWindow {
    public static void main(String[] args) {

        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = epService.getEPAdministrator();
        EPRuntime runtime = epService.getEPRuntime();

        String mergeEvent = MergeEvent.class.getName();

        String epl1 = "create window MergeWindow.win:keepall() select * from " + mergeEvent;
        String epl2 = "create schema LogEvent as (id int, name string)";
        String epl3 = "create window LogWindow.win:keepall() as LogEvent";
        /*
         * matched（满足where条件）和not matched（不满足where条件）
         * 1. 过滤条件（mergeSize > 0）
         * 2. me是trigger, mw是window
         * 3. where条件满足时触发matched，否则not matched
         * 4. 当matched并且me.deleteFlag = true时，将窗口删除
         * 5. 当matched时，更新当前窗口满足mergeSize > 3的mergeSize
         */
        String epl4 = "on " + mergeEvent + "(mergeSize > 0) me merge MergeWindow mw where me.mergeId = mw.mergeId "
                + "when matched and me.deleteFlag = true then delete "
                + "when matched then update set mergeSize = mergeSize + me.mergeSize where mw.mergeSize > 3 "
                // MergeWindow（not matched）中不存在的事件都会在触发merge时插入到window中，同时将部分属性插入到LogWindow中用于记录
                + "when not matched then insert select * then insert into LogWindow(id, name) select me.mergeId, me.mergeStr";


        String epl5 = "on LogEvent(id=0) select lw.* from LogWindow as lw";
        String epl6 = "on " + mergeEvent + "(mergeSize = 0) select mw.* from MergeWindow as mw";

        System.out.println("Create Window: " + epl1);
        System.out.println("Merge Trigger: " + epl4);
        System.out.println();

        admin.createEPL(epl1);
        admin.createEPL(epl2);
        admin.createEPL(epl3);
        EPStatement state1 = admin.createEPL(epl4);
        state1.addListener(new OnMergeWindowlistener());
        EPStatement state2 = admin.createEPL(epl5);
        state2.addListener(new SelectLogWindowlistener());
        EPStatement state3 = admin.createEPL(epl6);
        state3.addListener(new SelectMergeWindowlistener());

        Map<String, Object> selectLog = new HashMap<String, Object>();
        selectLog.put("id", 0);

        //初始化事件的MergeSize = 0
        MergeEvent selectMerge = new MergeEvent();
        selectMerge.setMergeSize(0);

        //事件1
        MergeEvent me1 = new MergeEvent();
        me1.setDeleteFlag(false);
        me1.setMergeId(1);
        me1.setMergeSize(2);
        me1.setMergeStr("me1");
        System.out.println("Send MergeEvent 1: " + me1);
        runtime.sendEvent(me1);

        //事件2
        MergeEvent me2 = new MergeEvent();
        me2.setDeleteFlag(false);
        me2.setMergeId(2);
        me2.setMergeSize(3);
        me2.setMergeStr("me2");
        System.out.println("Send MergeEvent 2: " + me2);
        runtime.sendEvent(me2);

        //事件3
        MergeEvent me3 = new MergeEvent();
        me3.setDeleteFlag(false);
        me3.setMergeId(3);
        me3.setMergeSize(4);
        me3.setMergeStr("me3");
        System.out.println("Send MergeEvent 3: " + me3);
        runtime.sendEvent(me3);

        /*
         * 事件1、2、3触发epl4监听事件
         * 打印相应内容
         * 以上三个事件全部触发的是第三个when
         */

        /**
         *  查询之前插入的三个事件
         */
        System.out.println("\nSend MergeEvent to Select MergeWindow!");
        runtime.sendEvent(selectMerge);

        /**
         *  查询LogWindow中记录的MergeEvent部分属性
         */
        System.out.println("\nSend LogEvent to Select LogWindow!");
        runtime.sendEvent(selectLog, "LogEvent");

        /**
         *  因为mergeId是3，所以MergeWindow中只有mergeId=3的事件有资格被更新。
         *  并且mergeSize>3，所以可以执行更新操作。
         *  触发第二个when
         *  mergeSize = 4 + 5 = 9
         */
        MergeEvent me4 = new MergeEvent();
        me4.setDeleteFlag(false);
        me4.setMergeId(3);
        me4.setMergeSize(5);
        me4.setMergeStr("me4");
        System.out.println("\nSend MergeEvent 4: " + me4);
        runtime.sendEvent(me4);

        System.out.println("\nSend MergeEvent to Select MergeWindow!");
        runtime.sendEvent(selectMerge);

        /**
         *  因为mergeId是1，所以MergeWindow中只有mergeId=1的事件有资格被更新。
         *  并且deleteFlag=true，所以mergeId=1的事件将从MergeWindow中移除
         *  触发第一个when
         */
        MergeEvent me5 = new MergeEvent();
        me5.setDeleteFlag(true);
        me5.setMergeId(1);
        me5.setMergeSize(6);
        me5.setMergeStr("me5");
        System.out.println("\nSend MergeEvent 5: " + me5);
        runtime.sendEvent(me5);

        System.out.println("\nSend MergeEvent to Select MergeWindow!");
        runtime.sendEvent(selectMerge);
    }
}
