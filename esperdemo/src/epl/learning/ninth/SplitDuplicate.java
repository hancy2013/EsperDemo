package epl.learning.ninth;

import com.espertech.esper.client.*;

import java.io.Serializable;

/**
 * Created by wxmimperio on 2015/9/10.
 */

class SplitEvent1 implements Serializable {
    private int size;
    private int price;

    public SplitEvent1(int size, int price) {
        this.size = size;
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "SplitEvent{" + "size=" + size + ", price=" + price + '}';
    }
}

public class SplitDuplicate {
    public static void main(String[] args) {
        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider();
        EPAdministrator admin = esProvider.getEPAdministrator();
        EPRuntime er = esProvider.getEPRuntime();

        String splitEvent = SplitEvent1.class.getName();

        //创建窗口
        String epl1 = "create window LargeOrder.win:keepall() (price int) ";
        String epl2 = "create window MediumOrder.win:keepall() (price int)";
        String epl3 = "create window SmallOrder.win:keepall() (price int)";
        String epl4 = "create window LargeSize.win:keepall() (size int)";
        String epl5 = "create window MediumSize.win:keepall() (size int)";
        String epl6 = "create window SmallSize.win:keepall() (size int)";


        /*
         * output first
         * output first表示从第一个insert句子开始到最后一个
         * 基础事件依次匹配其where条件，只要满足了某一个，那么插入到对应的事件流以后
         * 这个基础事件就不再有机会和之后的where进行匹配，即不会再进入之后的事件流中
         */
        String insert1 = "insert into LargeOrder select price where price > 8";
        String insert2 = "insert into MediumOrder select price where price between 3 and 10";
        String insert3 = "insert into SmallOrder select price where price <= 3";
        String epl7 = "on " + splitEvent + " " + insert1 + " " + insert2 + " " + insert3;

        /*
         * output all
         * output all则表示基础事件和所有的insert句子的where进行匹配
         * 能满足的就进入对应的事件流
         * 即不会像first那样限制基础事件只能被输入到某一个流中
         */
        String insert4 = "insert into LargeSize select size where size > 5";
        String insert5 = "insert into MediumSize select size where size between 2 and 8";
        String insert6 = "insert into SmallSize select size where size <= 2";
        String epl8 = "on " + splitEvent + " " + insert4 + " " + insert5 + " " + insert6 + " output all";

        System.out.println("Output first(default): ");
        System.out.println(insert1);
        System.out.println(insert2);
        System.out.println(insert3);

        System.out.println();

        System.out.println("Output all: ");
        System.out.println(insert4);
        System.out.println(insert5);
        System.out.println(insert6);

        String selectLargeOrder = "select * from LargeOrder";
        String selectMediumOrder = "select * from MediumOrder";
        String selectSmallOrder = "select * from SmallOrder";

        String selectLargeSize = "select * from LargeSize";
        String selectMediumSize = "select * from MediumSize";
        String selectSmallSize = "select * from SmallSize";

        System.out.println();

        admin.createEPL(epl1);
        admin.createEPL(epl2);
        admin.createEPL(epl3);
        admin.createEPL(epl4);
        admin.createEPL(epl5);
        admin.createEPL(epl6);

        admin.createEPL(epl7);
        admin.createEPL(epl8);

        //事件（size，price）
        //SmallOrder,SmallSize
        SplitEvent1 se1 = new SplitEvent1(1, 2);
        //MediumOrder,LargeSize
        SplitEvent1 se2 = new SplitEvent1(9, 4);
        //SmallOrder,MediumSize
        SplitEvent1 se3 = new SplitEvent1(3, 1);
        //MediumOrder,MediumSize
        SplitEvent1 se4 = new SplitEvent1(5, 6);
        //LargeOrder,LargeSize,MediumSize
        SplitEvent1 se5 = new SplitEvent1(7, 9);

        System.out.println(se1);
        er.sendEvent(se1);
        System.out.println(se2);
        er.sendEvent(se2);
        System.out.println(se3);
        er.sendEvent(se3);
        System.out.println(se4);
        er.sendEvent(se4);
        System.out.println(se5);
        er.sendEvent(se5);

        //结果集
        EPOnDemandQueryResult selectLOrder = er.executeQuery(selectLargeOrder);
        EPOnDemandQueryResult selectMOrder = er.executeQuery(selectMediumOrder);
        EPOnDemandQueryResult selectSOrder = er.executeQuery(selectSmallOrder);
        EPOnDemandQueryResult selectLSize = er.executeQuery(selectLargeSize);
        EPOnDemandQueryResult selectMSize = er.executeQuery(selectMediumSize);
        EPOnDemandQueryResult selectSSize = er.executeQuery(selectSmallSize);

        //输出
        outputResult(selectLargeOrder, selectLOrder);
        outputResult(selectMediumOrder, selectMOrder);
        outputResult(selectSmallOrder, selectSOrder);
        outputResult(selectLargeSize, selectLSize);
        outputResult(selectMediumSize, selectMSize);
        outputResult(selectSmallSize, selectSSize);
    }

    private static void outputResult(String selectSentence, EPOnDemandQueryResult result) {
        System.out.println("\n" + selectSentence);
        EventBean[] events = result.getArray();
        for(EventBean eb : events) {
            System.out.println(eb.getUnderlying());
        }
    }
}
