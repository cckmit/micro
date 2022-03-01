package micro.commons.util;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;

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
	 * 归并计算调整核心线程与最大线程
	 * 
	 * @author gewx
	 * @param coreNum 核心线程数
	 * @param maxNum  最大线程数
	 **/
	public static void init(int coreNum, int maxNum) {
		POOLTASKEXECUTOR.setCorePoolSize(coreNum);
		POOLTASKEXECUTOR.setMaxPoolSize(maxNum);
		POOLTASKEXECUTOR.getThreadPoolExecutor().prestartAllCoreThreads();
	}

	/**
	 * 归并计算
	 * 
	 * @author gewx
	 * @param execute 执行器
	 * @param result  待计算数据.只支持List集合
	 * @param depth   任务深度(即一个任务分片分配多少数据)
	 * @return 归并结果
	 **/
	public static <T, R> List<R> calculate(Function<List<T>, R> execute, List<T> result, int depth) {
		if (isEmpty(result)) {
			return Collections.emptyList();
		}

		int taskCount = result.size() / depth;
		if (result.size() % depth != 0) {
			taskCount = taskCount + 1;
		}

		AtomicInteger downCount = new AtomicInteger(taskCount);
		AtomicBoolean mark = new AtomicBoolean(true);
		StringBuilder ex = new StringBuilder(512);
		List<R> mergerList = new ArrayList<>(taskCount * 2);

		IntStream.range(0, taskCount).forEach(val -> {
			ListenableFuture<R> future = (ListenableFuture<R>) POOLTASKEXECUTOR.submitListenable(() -> {
				if (!mark.get()) {
					return null;
				}
				int next = (depth + (depth * val));
				List<T> subList = result.subList(depth * val, next > result.size() ? result.size() : next);
				return execute.apply(subList);
			});

			future.addCallback(r -> {
				mergerList.add(r);
				downCount.decrementAndGet();
			}, thx -> {
				ex.append(thx.getMessage());
				mark.set(false);
			});
		});

		while (true) {
			if (downCount.intValue() == 0) {
				break;
			}
			if (!mark.get()) {
				throw new ConcurrentMergerException("归并计算异常, ex: " + ex.toString());
			}
		}
		return mergerList;
	}

	/**
	 * 归并计算
	 * 
	 * @author gewx
	 * @param task 任务集合
	 * @return 归并结果
	 **/
	@SuppressWarnings("unchecked")
	public static <R> List<R> calculate(Callable<R>... task) {
		AtomicInteger downCount = new AtomicInteger(task.length);
		AtomicBoolean mark = new AtomicBoolean(true);
		StringBuilder ex = new StringBuilder(512);
		List<R> mergerList = new ArrayList<>(task.length * 2);

		IntStream.range(0, task.length).forEach(index -> {
			ListenableFuture<R> future = (ListenableFuture<R>) POOLTASKEXECUTOR.submitListenable(() -> {
				if (!mark.get()) {
					return null;
				}
				return task[index].call();
			});

			future.addCallback(r -> {
				mergerList.add(r);
				downCount.decrementAndGet();
			}, thx -> {
				ex.append(thx.getMessage());
				mark.set(false);
			});
		});

		while (true) {
			if (downCount.intValue() == 0) {
				break;
			}
			if (!mark.get()) {
				throw new ConcurrentMergerException("归并计算异常, ex: " + ex.toString());
			}
		}
		return mergerList;
	}

	/**
	 * 并发执行
	 * 
	 * @author gewx
	 * @param task 任务集合
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void calculate(Runnable... task) {
		AtomicInteger downCount = new AtomicInteger(task.length);
		AtomicBoolean mark = new AtomicBoolean(true);
		StringBuilder ex = new StringBuilder(512);

		IntStream.range(0, task.length).forEach(index -> {
			ListenableFuture future = (ListenableFuture) POOLTASKEXECUTOR.submitListenable(() -> {
				if (mark.get()) {
					task[index].run();
				}
			});

			future.addCallback(r -> {
				downCount.decrementAndGet();
			}, thx -> {
				ex.append(thx.getMessage());
				mark.set(false);
			});
		});

		while (true) {
			if (downCount.intValue() == 0) {
				break;
			}
			if (!mark.get()) {
				throw new ConcurrentMergerException("并发执行异常, ex: " + ex.toString());
			}
		}
	}

	/**
	 * 计算归并结果集
	 * 
	 * @author gewx
	 * @param result 归并结果集
	 * @param func   结果集处理函数
	 * @return 归并结果
	 **/
	public static <T, R> List<R> getResult(List<T> result, Function<T, R> func) {
		List<R> list = new ArrayList<>(64);
		result.forEach(val -> {
			list.add(func.apply(val));
		});
		return list;
	}
}
