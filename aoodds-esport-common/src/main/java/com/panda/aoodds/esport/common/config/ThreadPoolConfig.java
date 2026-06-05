package com.panda.aoodds.esport.common.config;

import com.panda.aoodds.esport.common.thread.VisiableThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class ThreadPoolConfig {

    /**
     * AO系统级别赛事开关
     */
    @Bean(name = "aoMatchSystemSwitchThreadPool")
    public TaskExecutor aoMatchSystemSwitchThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-aoMatchSystemSwitch-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name="aoMatchCashOutProbThreadPool")
    public TaskExecutor aoMatchCashOutProbThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-aoMatchCashOutProbThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    //重要 :: 定时刷新赔率线程，非该业务请勿使用
    @Bean(name = "aoMarketOddsTimerThread")
    public TaskExecutor multipleDSMarketThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-Timer-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    @Bean(name = "aoBkMarketOddsTimerThread")
    public TaskExecutor multipleDSMarketThreadPoolBk() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-Timer-Bk-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "notifySendMarketMessage")
    public TaskExecutor notifySendMarketMessageThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-notifySendMarketMessage-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "checkApplyParamThreadPool")
    public TaskExecutor checkApplyParamThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-checkApplyParamThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "preAutoApplyThreadPool")
    public TaskExecutor preAutoApplyThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-preAutoApplyThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "inplayAutoApplyThreadPool1")
    public TaskExecutor inplayAutoApplyThreadPool1() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-inplayAutoApplyThreadPool1-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "inplayAutoApplyThreadPool2")
    public TaskExecutor inplayAutoApplyThreadPool2() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-inplayAutoApplyThreadPool2-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "inplayAutoApplyThreadPool3")
    public TaskExecutor inplayAutoApplyThreadPool3() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-inplayAutoApplyThreadPool3-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "inplayAutoApplyThreadPool4")
    public TaskExecutor inplayAutoApplyThreadPool4() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-inplayAutoApplyThreadPool4-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    @Bean(name = "inplayAutoApplyThreadPool5")
    public TaskExecutor inplayAutoApplyThreadPool5() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-inplayAutoApplyThreadPool5-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    @Bean(name = "inplayMarketOddsProcessThreadPool1")
    public TaskExecutor inplayMarketOddsProcessThreadPool1() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-inplayMarketOddsProcessThreadPool1-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "inplayMarketOddsProcessThreadPool2")
    public TaskExecutor inplayMarketOddsProcessThreadPool2() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-inplayMarketOddsProcessThreadPool2-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "inplayMarketOddsProcessThreadPool3")
    public TaskExecutor inplayMarketOddsProcessThreadPool3() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-inplayMarketOddsProcessThreadPool3-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "inplayMarketOddsProcessThreadPool4")
    public TaskExecutor inplayMarketOddsProcessThreadPool4() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-inplayMarketOddsProcessThreadPool4-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    @Bean(name = "inplayMarketOddsProcessThreadPool5")
    public TaskExecutor inplayMarketOddsProcessThreadPool5() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-inplayMarketOddsProcessThreadPool5-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    @Bean(name = "preAutoApplyThreadPool1")
    public TaskExecutor preAutoApplyThreadPool1() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-preAutoApplyThreadPool1-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "preAutoApplyThreadPool2")
    public TaskExecutor preAutoApplyThreadPool2() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-preAutoApplyThreadPool2-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "preAutoApplyThreadPool3")
    public TaskExecutor preAutoApplyThreadPool3() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-preAutoApplyThreadPool3-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "preAutoApplyThreadPool4")
    public TaskExecutor preAutoApplyThreadPool4() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-preAutoApplyThreadPool4-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    @Bean(name = "preAutoApplyThreadPool5")
    public TaskExecutor preAutoApplyThreadPool5() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-preAutoApplyThreadPool5-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 异步赔率下发
     */
    @Bean("notifyOddsSendMarketMessage")
    public TaskExecutor notifyOddsSendMarketMessageThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-notifyOddsSendMarketMessage-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 风控模版配置 模板margin/最大最小赔率范围配置
     */
    @Bean("tradeMarketItemConfigMessageProcessor")
    public TaskExecutor tradeMarketItemConfigProcessorThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-tradeMarketItemConfigMessageProcessor-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 风控模版配置 模板margin/最大最小赔率范围配置
     */
    @Bean("esportTradeMarketItemConfigMessageProcessor")
    public TaskExecutor esportTradeMarketItemConfigMessageProcessorThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-esportTradeMarketItemConfigMessageProcessorThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    /**
     * 风控玩法模版配置
     */
    @Bean("matchCategoryConfigurationsProcessorMessage")
    public TaskExecutor matchCategoryConfigurationsProcessorMessageThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-matchCategoryConfigurationsProcessorMessageThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 异步APPLY赔率下发
     */
    @Bean("notifyApplyOddsSendMarketMessage")
    public TaskExecutor notifyApplyOddsSendMarketMessageThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(128);
        //配置队列大小
        executor.setQueueCapacity(1000);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-notifyApplyOddsSendMarketMessage-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    /**
     * MY异步抽水
     */
    @Bean("notifyMyCalculateMarketOddsMessage")
    public TaskExecutor notifyMyCalculateMarketOddsMessage() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-notifyMyCalculateMarketOddsMessage-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * MY异步抽水 c01
     */
    @Bean("notifyMyCalculateMarketOddsEsportMessage")
    public TaskExecutor notifyMyCalculateMarketOddsEsportMessage() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-notifyMyCalculateMarketOddsEsportMessage-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    /**
     * EU异步抽水
     */
    @Bean("notifyEuCalculateMarketOddsMessage")
    public TaskExecutor notifyEuCalculateMarketOddsMessage() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-notifyEuCalculateMarketOddsMessage-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    @Bean(name = "wsSendMarketMessageThread")
    public TaskExecutor wsSendMarketMessageThread() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(32);
        //配置最大线程数
        executor.setMaxPoolSize(64);
        //配置队列大小
        executor.setQueueCapacity(128);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-wsSendMarketMessageThread-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 三方球头
     * @return
     */
    @Bean(name = "thirdMarketOddsThreadPool")
    public TaskExecutor thirdMarketOddsThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-thirdMarketOddsThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 三方球头 ls
     * @return
     */
    @Bean(name = "thirdMarketOddsLsThreadPool")
    public TaskExecutor thirdMarketOddsLsThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-thirdMarketOddsLsThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 三方球头 bg
     * @return
     */
    @Bean(name = "thirdMarketOddsBgThreadPool")
    public TaskExecutor thirdMarketOddsBgThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-thirdMarketOddsBgThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 三方球头 tx
     * @return
     */
    @Bean(name = "thirdMarketOddsTxThreadPool")
    public TaskExecutor thirdMarketOddsTxThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-thirdMarketOddsTxThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    /**
     * 三方球头 sr
     * @return
     */
    @Bean(name = "thirdMarketOddsSrThreadPool")
    public TaskExecutor thirdMarketOddsSrThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-thirdMarketOddsSrThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }

    /**
     * 三方球头 bc
     * @return
     */
    @Bean(name = "thirdMarketOddsBcThreadPool")
    public TaskExecutor thirdMarketOddsBcThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-thirdMarketOddsBcThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    /**
     * 三方篮球球头
     * @return
     */
    @Bean(name = "thirdBaskBallMarketOddsThreadPool")
    public TaskExecutor thirdBaskBallMarketOddsThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-thirdBaskBallMarketOddsThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    /**
     * 三方篮球球头,次要玩法
     * @return
     */
    @Bean(name = "thirdBaskBallMarketMainlyNotOddsThreadPool")
    public TaskExecutor thirdBaskBallMarketMainlyNotOddsThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(256);
        //配置最大线程数
        executor.setMaxPoolSize(512);
        //配置队列大小
        executor.setQueueCapacity(1024);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-thirdBaskBallMarketMainlyNotOddsThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
    /**
     * 标准球头
     * @return
     */
    @Bean(name = "standardMarketOddsThreadPool")
    public TaskExecutor standardMarketOddsThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-standardMarketOddsThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }


    /**
     * 模版修改后，下发赔率
     * @return
     */
    @Bean(name = "sendChangeTemplateCategoryThreadPool")
    public TaskExecutor getSendChangeTemplateCategoryThreadPool() {
        ThreadPoolTaskExecutor executor = new VisiableThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(128);
        //配置最大线程数
        executor.setMaxPoolSize(256);
        //配置队列大小
        executor.setQueueCapacity(512);
        //配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("ThreadPool-sendChangeTemplateCategoryThreadPool-");
        //当任务数量超过MaxPoolSize和QueueCapacity时使用的策略，该策略指不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //线程池初始化
        executor.initialize();
        return executor;
    }
}
