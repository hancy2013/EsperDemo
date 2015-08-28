package epl.learning.sixthEPL;

import com.espertech.esper.client.*;

import java.util.Iterator;

/**
 * Created by wxmimperio on 2015/8/28.
 */
public class SQLOutputRowConversion {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.configure("esper.mysql.config.xml");
        config.addVariable("vari", Integer.class, 10);
        EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider(config);

        EPAdministrator admin = epService.getEPAdministrator();
        // epl中返回的流必须用“*”表示，不能是之前那样写成id或者name
        // id=1, name="luonq"
        String epl1 = "@Hook(type=HookType.SQLROW, hook='" + MySQLOutputRowConvertor.class.getName()
                + "')select * from sql:world['select ID, Name from city where id<${vari}']";

        EPStatement state1 = admin.createEPL(epl1);

        Iterator<EventBean> iter = state1.iterator();
        while (iter.hasNext()) {
            EventBean eventBean = iter.next();
            System.out.println(eventBean.getUnderlying());
        }
    }
}
