package com.imperio;


import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Created by Administrator on 2015/9/18.
 */
public class connectionES01 {
    public static void main(String[] args) {

    }

    private void connES() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport",true)
                .build();

        //启动一个client
        Client client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("hostA",9300));
    }
}
