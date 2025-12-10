package org.skala.crewvoice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 비동기 처리 설정
 * FastAPI 평가 요청을 비동기로 처리하기 위한 Thread Pool 설정
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 평가 작업용 Thread Pool Executor 빈 생성
     *
     * 설정값:
     * - Core Pool Size: 10 (기본 유지 스레드 수)
     * - Max Pool Size: 50 (최대 스레드 수)
     * - Queue Capacity: 100 (대기 큐 크기)
     * - Rejection Policy: CallerRunsPolicy (큐 초과 시 호출 스레드에서 실행)
     *
     * @return Executor 인스턴스
     */
    @Bean(name = "evaluationTaskExecutor")
    public Executor evaluationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core Thread Pool Size (기본 유지 스레드 수)
        executor.setCorePoolSize(10);

        // Maximum Thread Pool Size (최대 스레드 수)
        executor.setMaxPoolSize(50);

        // Queue Capacity (작업 대기 큐 크기)
        executor.setQueueCapacity(100);

        // Thread Name Prefix (로깅 시 스레드 식별용)
        executor.setThreadNamePrefix("evaluation-async-");

        // Rejection Policy (큐가 가득 찬 경우 처리 방식)
        // CallerRunsPolicy: 큐 초과 시 호출한 스레드에서 직접 실행
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // Await termination time (초 단위)
        executor.setAwaitTerminationSeconds(60);

        // 초기화
        executor.initialize();

        log.info("평가 작업용 Thread Pool 초기화 완료 - Core: {}, Max: {}, Queue: {}",
                executor.getCorePoolSize(),
                executor.getMaxPoolSize(),
                executor.getQueueCapacity());

        return executor;
    }
}
