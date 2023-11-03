package org.example;

import org.example.netty.NettyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@MapperScan("org.example.mapper")
public class Main {

    @Autowired
    private NettyServer nettyServer;
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @PostConstruct
    public void init() throws Exception {
        // 在应用程序启动时启动 Netty 服务器
        Thread thread = new Thread(()->{
            try {
                nettyServer.start();
            } catch (Exception e) {
                System.out.println("socket 启动失败 。。。。。");
//                log.error("socket 启动失败 。。。。。");
            }
        });
        thread.start();
    }


}