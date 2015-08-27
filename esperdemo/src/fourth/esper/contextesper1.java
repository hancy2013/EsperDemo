package fourth.esper;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/25.
 */

//javaBean
class ESB {
    private int id;
    private int price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ESB(int id, int price) {
        this.id = id;
        this.price = price;
    }
}

//监听类
class ContextListener implements UpdateListener {
    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
/*		for(EventBean eb : newEvents) {
            *//*System.out.println("context.name: " + eb.get("name") + "context.id: " + eb.get("id") +
				" context.key1: " + eb.get("key1") + " context.key2: " + eb.get("key2"));*//*
			System.out.println("avg(price): " + eb.get("avg(price)"));
		}*/
        EventBean event = newEvents[0];
        System.out.println("avg(price): " + event.get("avg(price)"));
    }
}

public class contextesper1 {
    public static void main(String[] args) {

        //设置配置信息
        Configuration config = new Configuration();

        //添加事件类型定义
        config.addEventType("ESB", ESB.class);

        //注册引擎
        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        //获取管理接口
        EPAdministrator admin = esProvider.getEPAdministrator();

        //创建context
		/*
		context.id针对不同的esb的id和price建立不同的context
		若事件的id和price相同，则进入同一个context
		 */
        String epl1 = "create context esbtest partition by id,price from ESB";
        //String epl2 = "context esbtest select context.id,context.name,context.key1,context.key2 from ESB";

        //每当两个context.id相同的事件进入时，统计price平均值
        String epl3 = "context esbtest select avg(price) from ESB.win:length_batch(2)";

        //注册EPL语句
        admin.createEPL(epl1);

        //创建查询实例
        EPStatement statement = admin.createEPL(epl3);
        //添加事件监听
        statement.addListener(new ContextListener());

        //引擎实例化接口
        EPRuntime er = esProvider.getEPRuntime();


        //创建ESB实例
		/*
		此时，对于不同的id和price，都会新建一个context
		并且context.id会从0开始增加作为其标识
		如果id和 price一样，事件就会进入之前已经存在的context（例如：e1和e4就会属于同一个context）
		*/
        ESB e1 = new ESB(1, 20);
        System.out.println("sendEvent: id=1, price=20");
        er.sendEvent(e1);

        ESB e2 = new ESB(1, 30);
        System.out.println("sendEvent: id=1, price=30");
        er.sendEvent(e2);

        ESB e3 = new ESB(1, 40);
        System.out.println("sendEvent: id=1, price=40");
        er.sendEvent(e3);

        ESB e4 = new ESB(1, 20);
        System.out.println("sendEvent: id=1, price=20");
        er.sendEvent(e4);

    }
}
