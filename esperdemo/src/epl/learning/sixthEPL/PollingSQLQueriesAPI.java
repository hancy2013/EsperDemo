package epl.learning.sixthEPL;
import com.espertech.esper.client.*;

import java.util.Iterator;

/**
 * Created by wxmimperio on 2015/8/28.
 */


public class PollingSQLQueriesAPI {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.configure("esper.mysql.config.xml");
        config.addVariable("vari", Integer.class, 1);

        EPServiceProvider esProvider = EPServiceProviderManager.getDefaultProvider(config);
        EPAdministrator admin = esProvider.getEPAdministrator();
        EPRuntime er = esProvider.getEPRuntime();

        String sql = "select ID, Name from sql:world['select ID, Name from city where ID > ${vari}']";

        EPStatement statement = admin.createEPL(sql);

        Iterator<EventBean> iter = statement.iterator();
        while (iter.hasNext()) {
            EventBean newEvents = iter.next();
            System.out.println(newEvents.get("ID") + " " + newEvents.get("Name"));
        }

    }
}
