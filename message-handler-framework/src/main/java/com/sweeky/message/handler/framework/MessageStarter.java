package com.sweeky.message.handler.framework;

import com.sweeky.message.handler.framework.init.InitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MessageStarter implements CommandLineRunner {
    @Autowired
    private InitializationService initializationService;

    @Override
    public void run(String... args) throws Exception {

    }
}
