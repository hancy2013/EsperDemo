package epl.learning.sixthEPL;
import com.espertech.esper.client.*;
import java.util.Iterator;

/**
 * Created by wxmimperio on 2015/8/28.
 */

public class MySQLColumnTypeConversion {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.configure("esper.mysql.config.xml");
        config.addVariable("vari", Integer.class, 10);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();
        String epl1 = "@Hook(type=HookType.SQLCOL, hook='" + MySQLColumnTypeConvertor.class.getName() +
                "')select ID,Name from sql:world['select ID,Name from city where ID < ${vari}']";

        EPStatement statement = admin.createEPL(epl1);

        Iterator<EventBean> iter = statement.iterator();
        while (iter.hasNext()) {
            EventBean eventBean = iter.next();
            System.out.println(eventBean.get("ID") + ", " + eventBean.get("Name"));
        }
    }
}
