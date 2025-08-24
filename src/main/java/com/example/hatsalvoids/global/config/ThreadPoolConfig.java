package com.example.hatsalvoids.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ThreadPoolConfig {

    // 공용 ThreadFactory: 이름 접두사 + 데몬 여부 선택
    private ThreadFactory namedThreadFactory(String prefix, boolean daemon) {
        AtomicInteger counter = new AtomicInteger(1);
        return r -> {
            Thread t = new Thread(r, prefix + "-" + counter.getAndIncrement());
            t.setDaemon(daemon);
            return t;
        };
    }

    /**
     * 범용 업무용 ThreadPoolExecutor
     * - core: CPU 코어 수 * 2
     * - max: core * 2
     * - queue: 200 (유한 큐, 메모리/지연 보호)
     * - keepAlive: 90s (여분 스레드 수축)
     * - rejection: CallerRunsPolicy (자연스런 백프레셔)
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor generalTaskExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        int corePoolSize = Math.max(2, cores * 2);
        int maximumPoolSize = corePoolSize * 2;
        long keepAliveTime = 60L;

        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(200);

        ThreadFactory threadFactory = namedThreadFactory("general-worker", false);

        RejectedExecutionHandler rejectionHandler = new ThreadPoolExecutor.CallerRunsPolicy();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime, TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                rejectionHandler
        );

        // 필요 시 코어 스레드 미리 기동
        // executor.prestartAllCoreThreads();

        return executor;
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor openAiAsyncExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        int corePoolSize = Math.max(2, cores * 2);
        int maximumPoolSize = corePoolSize * 2;
        long keepAliveTime = 60L;

        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(200);

        ThreadFactory threadFactory = namedThreadFactory("general-worker", false);

        RejectedExecutionHandler rejectionHandler = new ThreadPoolExecutor.CallerRunsPolicy();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime, TimeUnit.SECONDS,
                workQueue,
                threadFactory,
                rejectionHandler
        );

        // 필요 시 코어 스레드 미리 기동
        // executor.prestartAllCoreThreads();

        return executor;
    }

    /**
     * 배치/CPU 바운드 태스크 전용
     * - core=max=코어 수 (컨텍스트 스위칭 최소화)
     * - queue: 유한(100)
     * - rejection: AbortPolicy (실패를 명확히 감지)
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor batchTaskExecutor() {
        int pool = Math.max(1, Runtime.getRuntime().availableProcessors());
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                pool, pool,
                0L, TimeUnit.SECONDS,
                workQueue,
                namedThreadFactory("batch-worker", false),
                new ThreadPoolExecutor.AbortPolicy()
        );
        return executor;
    }

    /**
     * IO 바운드/네트워크 호출 비중 큰 작업 전용
     * - core: 코어 수 * 3
     * - max: core * 2
     * - queue: 500
     * - rejection: CallerRunsPolicy
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolExecutor ioTaskExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        int core = Math.max(3, cores * 3);
        int max = core * 2;

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                core, max,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(500),
                namedThreadFactory("io-worker", false),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return executor;
    }
}
