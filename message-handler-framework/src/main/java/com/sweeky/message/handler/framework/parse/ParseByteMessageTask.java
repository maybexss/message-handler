package com.sweeky.message.handler.framework.parse;

import com.sweeky.message.handler.framework.conf.MessageConfig;
import com.sweeky.message.handler.framework.monitor.RunningStatus;
import com.sweeky.message.handler.framework.util.ExecutorServiceUtil;
import com.sweeky.message.handler.framework.util.SpringServiceUtil;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ParseByteMessageTask implements RunningStatus {
    private String topicName;

    public ParseByteMessageTask(String topicName, MessageConfig config, SpringServiceUtil serviceUtil) {
        this.topicName = topicName;
        String threadName = topicName + "-ParseByteMessageTask";
        ExecutorServiceUtil.generateSingleExecutorService(threadName, true)
                .submit(new ParseByteMessageThread(topicName, config, serviceUtil));
    }

    @Override
    public String getStatus() {
        return null;
    }
}
