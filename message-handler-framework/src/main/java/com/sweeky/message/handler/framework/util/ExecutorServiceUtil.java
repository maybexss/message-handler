package com.sweeky.message.handler.framework.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ExecutorService util, generate ExecutorService
 */
public class ExecutorServiceUtil {

    /**
     * generate a single executor service
     *
     * @param threadName thread name
     * @param daemon     if the thread should be daemon
     * @return executorService
     */
    public static ExecutorService generateSingleExecutorService(String threadName, boolean daemon) {
        return Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(threadName);

            thread.setDaemon(daemon);

            return thread;
        });
    }
}
