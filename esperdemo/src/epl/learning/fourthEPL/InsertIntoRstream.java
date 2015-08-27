package epl.learning.fourthEPL;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/27.
 */
class Shoes
{
    private int id;
    private int size;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }


    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public String toString()
    {
        return "id: " + id + ", size: " + size;
    }

    public Shoes(int id, int size) {
        this.id = id;
        this.size = size;
    }
}

class ShoesRstreamListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        //newEvent
        if (newEvents != null) {
            for (EventBean newEb : newEvents) {
                System.out.println("Insert Shoes: cid=" + newEb.get("cid"));
            }
        } else {
            System.out.println("newEvents is null");
        }
        //oldEvent
        if (newEvents != null) {
            for (EventBean oldEb : oldEvents) {
                System.out.println("Remove Shoes: cid=" + oldEb.get("cid"));
            }
        } else {
            System.out.println("newEvents is null");
        }
    }
}
public class InsertIntoRstream {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.addEventType("ShoesRstream",Shoes.class);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();

        String insertEPL = "insert rstream into Computer(cid,csize) select id,size from ShoesRstream.win:length(1)";
        String insertSelectEPL = "select cid from Computer.win:length_batch(2)";

        EPStatement statement = admin.createEPL(insertEPL);
        EPStatement statement1 = admin.createEPL(insertSelectEPL);

        statement1.addListener(new ShoesRstreamListener());

        EPRuntime er = esProvider.getEPRuntime();

        Shoes shoes1 = new Shoes(1,1);
        System.out.println("sendEvent: " + shoes1);
        er.sendEvent(shoes1);

        Shoes shoes2 = new Shoes(2,1);
        System.out.println("sendEvent: " + shoes2);
        er.sendEvent(shoes2);

        Shoes shoes3 = new Shoes(3,3);
        System.out.println("sendEvent: " + shoes3);
        er.sendEvent(shoes3);

        Shoes shoes4 = new Shoes(4,4);
        System.out.println("sendEvent: " + shoes4);
        er.sendEvent(shoes4);

        Shoes shoes5 = new Shoes(5,3);
        System.out.println("sendEvent: " + shoes5);
        er.sendEvent(shoes5);

        Shoes shoes6 = new Shoes(6,4);
        System.out.println("sendEvent: " + shoes6);
        er.sendEvent(shoes6);
    }
}

