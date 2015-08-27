package third.esper;

import java.util.Arrays;
import java.util.Map;

import com.espertech.esper.client.*;

/**
 * Created by wxmimperio on 2015/8/25.
 */
public class ObjArrayEvent {

    public static void main(String[] agrs) {
        //注册引擎
        EPServiceProvider epSProvider = EPServiceProviderManager.getDefaultProvider();

        //获取管理接口
        EPAdministrator admin = epSProvider.getEPAdministrator();

        //Address 定义
        String[] addressPropNames = {"road", "street", "houseNo"};
        Object[] addressPropTpyes = {String.class, String.class, int.class};

        //child定义
        String[] childPropNames = {"name", "age"};
        Object[] childPropTypes = {String.class, int.class};

        //Person定义
        String[] personPropNames = {"name", "age", "children", "phones", "address"};
        Object[] personPropTypes = {String.class, int.class, "Child[]", Map.class, "Address"};

        //注册address到esper
        admin.getConfiguration().addEventType("Address", addressPropNames, addressPropTpyes);
        //注册child到esper
        admin.getConfiguration().addEventType("Child", childPropNames, childPropTypes);
        //注册person到esper
        admin.getConfiguration().addEventType("ObjArrEvent", personPropNames, personPropTypes);

        //新增一个属性gender
        admin.getConfiguration().updateObjectArrayEventType("ObjArrEvent", new String[]{"gender"},
                new Object[]{String.class});


        EventType eventType = admin.getConfiguration().getEventType("ObjArrEvent");
        System.out.println("Person props: " + Arrays.asList(eventType.getPropertyNames()));

    }
}
