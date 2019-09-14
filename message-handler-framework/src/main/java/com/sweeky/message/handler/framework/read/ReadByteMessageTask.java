package com.sweeky.message.handler.framework.read;

import com.sweeky.message.handler.framework.conf.MessageConfig;
import com.sweeky.message.handler.framework.monitor.RunningStatus;
import com.sweeky.message.handler.framework.util.ExecutorServiceUtil;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReadByteMessageTask implements RunningStatus {
    private String topicName;

    public ReadByteMessageTask(String topicName, MessageConfig config) {
        this.topicName = topicName;
        String threadName = topicName + "-ReadIndexMessageTask";
        ExecutorServiceUtil.generateSingleExecutorService(threadName, true).submit(new ReadByteMessageThread(topicName, config));
    }

    @Override
    public String getStatus() {
        return null;
    }
}
