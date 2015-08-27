package second.esper;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/24.
 */
public class HelloEsper {

    public static void main(String[] args) {
        //1. 设置配置信息
        Configuration config = new Configuration();

        //2. 添加事件类型定义
        config.addEventType("myEvent", MyEvent.class);

        //3.创建引擎实例,将事件定义传入
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider(config);

        //4. 创建statement的管理类接口
        EPAdministrator admin = provider.getEPAdministrator();

        //4.1 通过create schema语法注册 myEvent事件。
        //admin.createEPL("create schema myEvent as com.esper.test.helloesper.MyEvent");

        //5. 创建EPL查询语句
        String epl = "select id,name from myEvent";

        //6. 创建查询实例，注册epl语句
        EPStatement statement = admin.createEPL(epl);

        //7.为statement添加监听事件
        statement.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                //for循环遍历newEvents
                for (EventBean eb : newEvents) {
                    System.out.println("id: " + eb.get("id") + " name: " + eb.get("name"));
                }
            }
        });

        //8. 引擎实例运行接口，负责为引擎实例接收数据并发送给引擎处理
        EPRuntime er = provider.getEPRuntime();
        er.sendEvent(new MyEvent(1, "aaa"));
        er.sendEvent(new MyEvent(2, "bbb"));
        er.sendEvent(new MyEvent(3, "ccc"));
    }
}
