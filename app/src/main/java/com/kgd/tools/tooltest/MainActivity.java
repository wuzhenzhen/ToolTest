package com.kgd.tools.tooltest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.netty.channel.ChannelFuture;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initServer();
    }

    public void initServer(){
        ChannelFuture future = null;
        NettyServer server = null;
        try{
            server = new NettyServer();
            //建立连接
            future = server.doAccept(8081, new ServerHandler());
            System.out.println("server started.");
            //关闭连接，回收资源
            future.channel().closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (null != future){
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (null!=server){
                server.release();
            }
        }
    }
}
