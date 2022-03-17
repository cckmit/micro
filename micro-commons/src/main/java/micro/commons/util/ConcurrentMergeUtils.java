package micro.commons.util;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static java.util.Collections.emptyList;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import micro.commons.exception.ConcurrentMergeException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

public final class ConcurrentMergeUtils {

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
     * @param coreNum 核心线程数
     * @param maxNum  最大线程数
     * @author gewx
     **/
    public static void resize(int coreNum, int maxNum) {
        POOLTASKEXECUTOR.setCorePoolSize(coreNum);
        POOLTASKEXECUTOR.setMaxPoolSize(maxNum);
        POOLTASKEXECUTOR.getThreadPoolExecutor().prestartAllCoreThreads();
    }

    /**
     * 归并计算
     *
     * @param execute 执行器
     * @param result  待计算数据.只支持List集合
     * @param depth   任务深度(即一个任务分片分配多少数据)
     * @return 归并结果
     * @author gewx
     **/
    public static <T, R> List<R> calculate(Function<List<T>, R> execute, List<T> result, int depth) {
        if (isEmpty(result)) {
            return emptyList();
        }

        int taskCount = result.size() / depth;
        if (result.size() % depth != 0) {
            taskCount = taskCount + 1;
        }

        AtomicInteger downCount = new AtomicInteger(taskCount);
        AtomicBoolean mark = new AtomicBoolean(true);
        AtomicReference<String> ex = new AtomicReference<>();
        List<R> mergeList = new ArrayList<>(taskCount * 2);

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
                mergeList.add(r);
                downCount.decrementAndGet();
            }, thx -> {
                ex.set(thx.getMessage());
                mark.set(false);
            });
        });

        while (true) {
            if (downCount.intValue() == 0) {
                break;
            }
            if (!mark.get()) {
                throw new ConcurrentMergeException("归并计算异常, ex: " + ex.get());
            }
        }
        return mergeList;
    }

    /**
     * 归并计算
     *
     * @param task 任务集合
     * @return 归并结果
     * @author gewx
     **/
    @SuppressWarnings("unchecked")
    public static <R> List<R> calculate(Callable<R>... task) {
        AtomicInteger downCount = new AtomicInteger(task.length);
        AtomicBoolean mark = new AtomicBoolean(true);
        AtomicReference<String> ex = new AtomicReference<>();
        List<R> mergeList = new ArrayList<>(task.length * 2);

        IntStream.range(0, task.length).forEach(index -> {
            ListenableFuture<R> future = (ListenableFuture<R>) POOLTASKEXECUTOR.submitListenable(() -> {
                if (!mark.get()) {
                    return null;
                }
                return task[index].call();
            });

            future.addCallback(r -> {
                mergeList.add(r);
                downCount.decrementAndGet();
            }, thx -> {
                ex.set(thx.getMessage());
                mark.set(false);
            });
        });

        while (true) {
            if (downCount.intValue() == 0) {
                break;
            }
            if (!mark.get()) {
                throw new ConcurrentMergeException("归并计算异常, ex: " + ex.get());
            }
        }
        return mergeList;
    }

    /**
     * 基于任务顺序输出的归并计算
     *
     * @param task 任务集合
     * @return 归并结果
     * @author gewx
     **/
    @SuppressWarnings("unchecked")
    public static <R> List<R> naturalOrderCalculate(Callable<R>... task) {
        AtomicInteger downCount = new AtomicInteger(task.length);
        AtomicBoolean mark = new AtomicBoolean(true);
        AtomicReference<String> ex = new AtomicReference<>();
        List<InnerResult<R>> mergeList = new ArrayList<>(task.length * 2);

        IntStream.range(0, task.length).forEach(index -> {
            ListenableFuture<InnerResult<R>> future = (ListenableFuture<InnerResult<R>>) POOLTASKEXECUTOR
                    .submitListenable(() -> {
                        if (!mark.get()) {
                            return null;
                        }
                        InnerResult<R> result = new InnerResult<>();
                        result.setIndex(index);
                        result.setResult(task[index].call());
                        return result;
                    });

            future.addCallback(r -> {
                mergeList.add(r);
                downCount.decrementAndGet();
            }, thx -> {
                ex.set(thx.getMessage());
                mark.set(false);
            });
        });

        while (true) {
            if (downCount.intValue() == 0) {
                break;
            }
            if (!mark.get()) {
                throw new ConcurrentMergeException("归并计算异常, ex: " + ex.get());
            }
        }

        return mergeList.stream().sorted(Comparator.comparing(InnerResult::getIndex)).map(val -> val.getResult())
                .collect(Collectors.toList());
    }

    /**
     * 并发执行
     *
     * @param task 任务集合
     * @author gewx
     **/
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void calculate(Runnable... task) {
        AtomicInteger downCount = new AtomicInteger(task.length);
        AtomicBoolean mark = new AtomicBoolean(true);
        AtomicReference<String> ex = new AtomicReference<>();

        IntStream.range(0, task.length).forEach(index -> {
            ListenableFuture future = (ListenableFuture) POOLTASKEXECUTOR.submitListenable(() -> {
                if (mark.get()) {
                    task[index].run();
                }
            });

            future.addCallback(r -> {
                downCount.decrementAndGet();
            }, thx -> {
                ex.set(thx.getMessage());
                mark.set(false);
            });
        });

        while (true) {
            if (downCount.intValue() == 0) {
                break;
            }
            if (!mark.get()) {
                throw new ConcurrentMergeException("并发执行异常, ex: " + ex.get());
            }
        }
    }

    /**
     * 计算归并结果集
     *
     * @param result 归并结果集
     * @param func   结果集处理函数
     * @return 归并结果
     * @author gewx
     **/
    public static <T, R> List<R> getResult(List<T> result, Function<T, R> func) {
        List<R> list = new ArrayList<>(64);
        result.forEach(val -> {
            list.add(func.apply(val));
        });
        return list;
    }

    @Setter
    @Getter
    static class InnerResult<R> {

        private Integer index;

        private R result;
    }
}
