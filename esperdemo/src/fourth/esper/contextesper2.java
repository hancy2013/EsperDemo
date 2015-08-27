package fourth.esper;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/25.
 */
//javaBean
class CategoryContext {
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

	public CategoryContext(int id, int price) {
		this.id = id;
		this.price = price;
	}
}

//监听
class ContextListenerCategory implements UpdateListener {
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		for (EventBean eb : newEvents) {
			System.out.println("context.name: " + eb.get("name") + " context.id: " + eb.get("id") + " context.label: " + eb.get("label"));
		}
	}
}

public class contextesper2 {

	public static void main(String[] args) {

		Configuration config = new Configuration();
		config.addEventType("CategoryContext", CategoryContext.class);

		EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);

		EPAdministrator admin = esProvider.getEPAdministrator();

		String epl1 = "create context esbtest group by id<0 as low,group by id>0 and id<10 as middle," +
				"group by id>10 as high from CategoryContext";

		String epl2 = "context esbtest select context.id,context.name,context.label,price from CategoryContext";

		//注册epl1语句
		admin.createEPL(epl1);

		EPStatement statement = admin.createEPL(epl2);
		EPRuntime er = esProvider.getEPRuntime();

		statement.addListener(new ContextListenerCategory());

		CategoryContext e1 = new CategoryContext(1, 20);
		System.out.println("sendEvdent: id=1, price=20");
		er.sendEvent(e1);

		CategoryContext e2 = new CategoryContext(0, 30);
		System.out.println("sendEvdent: id=0, price=30");
		er.sendEvent(e2);

		CategoryContext e3 = new CategoryContext(11, 20);
		System.out.println("sendEvdent: id=11, price=20");
		er.sendEvent(e3);

		CategoryContext e4 = new CategoryContext(-1, 40);
		System.out.println("sendEvdent: id=-1, price=40");
		er.sendEvent(e4);

	}
}
