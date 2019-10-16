package micro.commons.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author gewx 全局任务异步处理
 **/
public final class GlobalThreadPoolTaskExecutor {

	private GlobalThreadPoolTaskExecutor() {
	}

	private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

	private static final ThreadPoolTaskExecutor POOLTASKEXECUTOR = new ThreadPoolTaskExecutor();

	static {
		POOLTASKEXECUTOR.setQueueCapacity(Integer.MAX_VALUE); // 队列深度.
		POOLTASKEXECUTOR.setCorePoolSize(CORE_SIZE); // 核心线程数.
		POOLTASKEXECUTOR.setMaxPoolSize(CORE_SIZE); // 最大线程数.
		POOLTASKEXECUTOR.setThreadNamePrefix("YOGA_TASK_"); // 线程名前缀.
		POOLTASKEXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy()); // discard
		POOLTASKEXECUTOR.initialize();

		POOLTASKEXECUTOR.getThreadPoolExecutor().prestartAllCoreThreads();
	}

	private static final GlobalThreadPoolTaskExecutor INSTANCE = new GlobalThreadPoolTaskExecutor();

	public static GlobalThreadPoolTaskExecutor getInstance() {
		return INSTANCE;
	}

	public void execute(TaskBean taskBean) {
		POOLTASKEXECUTOR.execute(taskBean);
	}

	public void execute(TaskBeanDelayed taskBean) {
		POOLTASKEXECUTOR.execute(taskBean);
	}

	public void execute(Runnable runTask) {
		POOLTASKEXECUTOR.execute(runTask);
	}

	public Future<?> execute(Callable<?> runTask) {
		Future<?> future = POOLTASKEXECUTOR.submit(runTask);
		return future;
	}
}
