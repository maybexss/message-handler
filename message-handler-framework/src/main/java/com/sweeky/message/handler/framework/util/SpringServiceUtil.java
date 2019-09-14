package com.sweeky.message.handler.framework.util;

import com.sweeky.message.handler.framework.convert.Converter;
import com.sweeky.message.handler.framework.process.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SpringServiceUtil {
    @Autowired
    private Map<String, Converter> converterMap;

    @Autowired
    private Map<String, Processor> processorMap;

    public Converter getConverter(String name) {
        return converterMap.get(name);
    }

    public Processor getProcessor(String name) {
        return processorMap.get(name);
    }
}
