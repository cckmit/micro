package micro.commons.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import micro.commons.exception.ConcurrentMergerException;

/**
 * 归并计算工具类
 * 
 * @author gewx
 **/
public final class ConcurrentMergerUtils {

	private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

	private static final ThreadPoolTaskExecutor POOLTASKEXECUTOR = new ThreadPoolTaskExecutor();

	static {
		POOLTASKEXECUTOR.setQueueCapacity(Short.MAX_VALUE);
		POOLTASKEXECUTOR.setCorePoolSize(CORE_SIZE * 2);
		POOLTASKEXECUTOR.setMaxPoolSize(CORE_SIZE * 2);
		POOLTASKEXECUTOR.setThreadNamePrefix("CONCURRENT_MERGER_TASK_");
		POOLTASKEXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
		POOLTASKEXECUTOR.initialize();

		POOLTASKEXECUTOR.getThreadPoolExecutor().prestartAllCoreThreads();
	}

	/**
	 * 归并计算
	 * 
	 * @author gewx
	 * @param execute   执行器
	 * @param result    待计算数据.只支持List集合
	 * @param taskDepth 任务深度(即一个任务分片分配多少数据)
	 * @param unit      间隙停顿时间单位
	 * @param time      间隙停顿时间
	 * @return 归并结果
	 **/
	public static <T, R> List<R> calculate(Function<List<T>, R> execute, List<T> result, int taskDepth, TimeUnit unit,
			int time) {
		if (CollectionUtils.isEmpty(result)) {
			return Collections.emptyList();
		}

		int allDepth = result.size();
		int allTask = allDepth / taskDepth;
		if (allDepth % taskDepth != 0) {
			allTask = allTask + 1;
		}

		AtomicInteger count = new AtomicInteger(allTask);
		AtomicBoolean state = new AtomicBoolean(true);
		StringBuilder ex = new StringBuilder(512);
		List<R> mergerList = new ArrayList<>(allTask * 2);

		IntStream.range(0, allTask).forEach(val -> {
			ListenableFuture<R> future = (ListenableFuture<R>) POOLTASKEXECUTOR.submitListenable(() -> {
				int next = (taskDepth + (taskDepth * val));
				List<T> subList = result.subList(taskDepth * val, next > result.size() ? result.size() : next);
				return execute.apply(subList);
			});

			future.addCallback(call -> {
				count.decrementAndGet();
				try {
					mergerList.add(future.get());
				} catch (Exception e) {
					ex.append(e.getMessage());
					state.set(false);
				}
			}, thx -> {
				ex.append(thx.getMessage());
				state.set(false);
			});
		});

		while (count.intValue() != 0) {
			if (!state.get()) {
				throw new ConcurrentMergerException("归并计算异常, ex: " + ex.toString());
			}
			try {
				unit.sleep(time);
			} catch (InterruptedException e) {
				throw new ConcurrentMergerException("归并计算异常, ex: " + e.getMessage());
			}
		}
		return mergerList;
	}

	/**
	 * 归并计算
	 * 
	 * @author gewx
	 * @param execute   执行器
	 * @param result    待计算数据.只支持List集合
	 * @param taskDepth 任务深度(即一个任务分片分配多少数据)
	 * @return 归并结果
	 **/
	public static <T, R> List<R> calculate(Function<List<T>, R> execute, List<T> result, int taskDepth) {
		return calculate(execute, result, taskDepth, TimeUnit.MILLISECONDS, 15);
	}
}
