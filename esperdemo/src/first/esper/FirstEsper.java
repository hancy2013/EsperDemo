package first.esper;

import com.espertech.esper.client.*;

import java.util.Random;
import java.util.Date;

/**
 * Created by wxmimperio on 2015/8/24.
 */
public class FirstEsper {

    //事件的定义
    public static class Tick {
        String symbol;
        Double price;
        Date timeStamp;

        //构造函数进行初始化
        public Tick(String s, double p, long t) {
            symbol = s;
            price = p;
            timeStamp = new Date(t);
        }

        //封装函数，获取信息
        public String getSymbol() {
            return symbol;
        }

        public Double getPrice() {
            return price;
        }

        public Date getTimeStamp() {
            return timeStamp;
        }

        //重写toString方法
        @Override
        public String toString() {
            return "Price: " + price.toString() + " time: " + timeStamp.toString();
        }
    }

    //创建随机数
    private static Random generator = new Random();

    /*生成随机“心跳”*/
    //模拟事件流
    public static void GenerateRandomTick(EPRuntime cepRT) {

        //nextInt()方法会读取下一个int型标志的token.但是焦点不会移动到下一行
        double price = (double) generator.nextInt(10);

        //获取当前毫秒级时间戳
        long timeStamp = System.currentTimeMillis();

        String symbol = "AAPL";

        //实例化tick对象
        Tick tick = new Tick(symbol, price, timeStamp);

        System.out.println("Sending tick:" + tick);

        //引擎实例运行接口，负责为引擎实例接收数据并发送给引擎处理
        cepRT.sendEvent(tick);
    }

    //当statement的结果集发生变化时，引擎会调用监听作为一个或者多个事件的响应。
    //监听类需要实现UpdateListener接口，并在EventBean实例上执行操作
    public static class CEPListener implements UpdateListener {
        @Override
        public void update(EventBean[] newData, EventBean[] oldData) {
            //接收到事件监听的响应
            System.out.println("Event received: " + newData[0].getUnderlying());
        }
    }


    public static void main(String[] args) {
        //设置配置信息
        Configuration cepConfig = new Configuration();
        //添加事件类型定义
        cepConfig.addEventType("StockTick", Tick.class.getName());

        //创建引擎实例
        EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine", cepConfig);

        //引擎实例运行接口，负责为引擎实例接收数据并发送给引擎处理
        EPRuntime cepRT = cep.getEPRuntime();

        //创建statement的管理接口实例
        EPAdministrator cepAdm = cep.getEPAdministrator();

        //创建EPL查询语句实例
        //.win:length(N)长度窗口，表示引擎会将过去的N条事件保存在事件流中
        String ELP = "select * from StockTick(symbol='AAPL').win:length(2) having avg(price) > 6.0";
        EPStatement cepStatement = cepAdm.createEPL(ELP);

        //为statement实例添加监听
        cepStatement.addListener(new CEPListener());

        //生成5个模拟事件流
        for (int i = 0; i < 5; i++) {
            GenerateRandomTick(cepRT);
        }
    }
}
