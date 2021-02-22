package micro.commons.task;

import java.util.concurrent.DelayQueue;

import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;

/**
 * 全局延迟任务异步处理
 * 
 * @author gewx
 **/
public final class GlobalDelayQueueTask {

	private GlobalDelayQueueTask() {
	}

	private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

	private static final GlobalDelayQueueTask INSTANCE = new GlobalDelayQueueTask();

	private static final GlobalThreadPoolTaskExecutor TASK_POOL = GlobalThreadPoolTaskExecutor.getInstance();

	private static final DelayQueue<TaskBeanDelayed> DELAY_QUEUE = new DelayQueue<>();

	private static final ScheduledExecutorFactoryBean FACTORY = new ScheduledExecutorFactoryBean();

	static {
		ScheduledExecutorTask task = new ScheduledExecutorTask();
		task.setDelay(0);
		task.setFixedRate(false);
		task.setPeriod(1000 * 15);
		task.setRunnable(new Runnable() {
			@Override
			public void run() {
				TaskBeanDelayed taskBean = null;
				do {
					taskBean = DELAY_QUEUE.poll();
					if (taskBean != null) {
						TASK_POOL.execute(taskBean.getTask());
					}
				} while (taskBean != null);
			}
		});

		FACTORY.setScheduledExecutorTasks(task);
		// 调度遇到异常后,调度计划继续执行
		FACTORY.setContinueScheduledExecutionAfterException(true);
		FACTORY.setThreadNamePrefix("YOGA_TASK_DELAY");
		FACTORY.setPoolSize(CORE_SIZE);
		FACTORY.initialize();
	}

	/**
	 * 获取单例对象
	 * 
	 * @author gewx
	 **/
	public static GlobalDelayQueueTask getInstance() {
		return INSTANCE;
	}

	/**
	 * 覆盖任务执行
	 * 
	 * @author gewx
	 **/
	public void compareAndSet(TaskBeanDelayed taskBean) {
		if (DELAY_QUEUE.contains(taskBean)) {
			DELAY_QUEUE.remove(taskBean);
			DELAY_QUEUE.add(taskBean);
		} else {
			DELAY_QUEUE.add(taskBean);
		}
	}
}
