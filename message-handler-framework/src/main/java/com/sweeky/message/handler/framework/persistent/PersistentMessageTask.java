package com.sweeky.message.handler.framework.persistent;

import com.sweeky.message.handler.framework.conf.MessageConfig;
import com.sweeky.message.handler.framework.monitor.RunningStatus;
import com.sweeky.message.handler.framework.util.ExecutorServiceUtil;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PersistentMessageTask implements RunningStatus {
    private String topicName;

    public PersistentMessageTask(String topicName, MessageConfig config) {
        this.topicName = topicName;
        String threadName = topicName + "-PersistentMessageTask";
        ExecutorServiceUtil.generateSingleExecutorService(threadName, true).submit(new PersistentMessageThread(threadName, config));
    }

    @Override
    public String getStatus() {
        return null;
    }
}
