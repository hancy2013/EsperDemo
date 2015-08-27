package third.esper;

import com.espertech.esper.client.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wxmimperio on 2015/8/24.
 */
public class Esper {

    public static void main(String[] args) {
        //1. 设置配置信息
        Configuration config = new Configuration();

        //2. 添加事件类型定义
        config.addEventType("POJOEvent", POJOEvent.class);

        //3.创建引擎实例,将事件定义传入
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider(config);

        //4. 创建statement的管理类接口
        EPAdministrator admin = provider.getEPAdministrator();

        //4.1 通过create schema语法注册 myEvent事件。
        //admin.createEPL("create schema myEvent as com.esper.test.helloesper.MyEvent");

        //5. 创建EPL查询语句
        //String epl = "select id,name from myEvent";
        //String POJOepl = "select age,children,address from POJOEvent where name='Jordan'";

//		当Person类型的事件中name为Jordan时，Esper能得到对应的第二个孩子,家里的电话和家庭住址在哪条路上
        String POJOepl = "select children[0].name,address.road,phones('home') from POJOEvent where name='Jordan'";

//		设置对象事件
        //String Updateepl = "update POJOEvent set phones('home')=123456789 where name='Jordan'";

        //6. 创建查询实例，注册epl语句
        EPStatement statement = admin.createEPL(POJOepl);

        //7.为statement添加监听事件
        statement.addListener(new UpdateListener() {
            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents) {
                //for循环遍历newEvents
                for (EventBean eb : newEvents) {
                    //System.out.println("age: " + eb.get("age") + " children: " + eb.get("children") + " address:" + eb.get("address"));
                    System.out.println("children's name: " + eb.get("children[0].name") + " road: " + eb.get("address.road")
                            + " phones: " + eb.get("phones('home')"));
                }
            }
        });

        //实例化childs列表
        List Childs = new ArrayList();
        Childs.add(new Child("imperio", "男"));

        //实例化phones Map
        HashMap phones = new HashMap();
        phones.put("home", 156369);

        //实例化address对象
        Address address = new Address("shanghai", "23号", 100);


        //8. 引擎实例运行接口，负责为引擎实例接收数据并发送给引擎处理
        EPRuntime er = provider.getEPRuntime();
        er.sendEvent(new POJOEvent("Jordan", 23, Childs, phones, address));
    }
}
