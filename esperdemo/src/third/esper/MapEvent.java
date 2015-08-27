package third.esper;

import java.util.*;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/24.
 */
public class MapEvent {

    //监听类
    public static class EPLClass implements UpdateListener {
        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents) {
            if(newEvents != null) {
                for (EventBean eb : newEvents) {
                    System.out.println("children: " + eb.get("children") + " age: " + eb.get("age"));
                }
            } else {
                System.out.println("newEvents is null");
            }
        }
    }

    public static void main(String[] args) {

//		//1. 设置配置信息
//		Configuration config = new Configuration();
//		//2. 添加事件类型定义
//		config.addEventType("MapEvent",POJOEvent.class);

        //3. 创建引擎实例
        EPServiceProvider provider = EPServiceProviderManager.getDefaultProvider();
        //4. 创建statement管理接口
        EPAdministrator admin = provider.getEPAdministrator();

        //地址Map事件定义
        HashMap<String, Object> addressMap = new HashMap<String, Object>();
        addressMap.put("road", String.class);
        addressMap.put("street", String.class);
        addressMap.put("houseNo", int.class);

        //Map事件定义
        HashMap<String, Object> event = new HashMap<String, Object>();
        event.put("name", String.class);
        event.put("age", int.class);
        event.put("children", List.class);
        event.put("phones", Map.class);
        //添加地址
        event.put("address", "Address");

        //注册event到Esper
        admin.getConfiguration().addEventType("Address", addressMap);
        admin.getConfiguration().addEventType("MapEvent", event);


        //5.创建epl语句
        String mapEpl = "select children, age from MapEvent where name='Jordan'";

        //6.创建查询实例，传入epl语句
        EPStatement statement = admin.createEPL(mapEpl);

        //7. 添加监听
        statement.addListener(new EPLClass());


        //实例化对象参数
        List childsList = new ArrayList();
        childsList.add(new Child("wxm", "男"));
        Map phones = new HashMap();
        phones.put("home", 188);

        HashMap<String, Object> addressObj = new HashMap<String, Object>();
        addressMap.put("road", "shanghai");
        addressMap.put("street", "23号");
        addressMap.put("houseNo", 100);

        HashMap<String, Object> personObj = new HashMap<String, Object>();
        personObj.put("name", "Jordan");
        personObj.put("children", childsList);
        personObj.put("age",25);
        personObj.put("phones", phones);
        personObj.put("address",addressObj);

        //8. 引擎实例化接口，将信息发给引擎
        EPRuntime er = provider.getEPRuntime();
        er.sendEvent(personObj,"MapEvent");
    }
}
