package group16.executor.service;

import group16.executor.service.task.management.FixedQueueTaskManager;
import group16.executor.service.task.management.SharedTaskManager;
import group16.executor.service.task.management.TaskManager;
import group16.executor.service.thread.management.EmaThreadPredictor;
import group16.executor.service.thread.management.ThreadManager;
import group16.executor.service.thread.management.WatermarkPredictor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DynamicExecutorService extends AbstractExecutorService {
    // Default Dynamic executor service creator methods
    public static DynamicExecutorService fixedQueueWatermark() {
        int cores = Runtime.getRuntime().availableProcessors();

        return new DynamicExecutorService(
            new FixedQueueTaskManager(cores),
            new WatermarkPredictor(cores, cores * 2) // TODO: Try cores * 4, etc.
        );
    }
    public static DynamicExecutorService fixedQueueEma() {
        int cores = Runtime.getRuntime().availableProcessors();

        EmaThreadPredictor emaThreadPredictor = new EmaThreadPredictor(0.7);

        DynamicExecutorService service = new DynamicExecutorService(
            new FixedQueueTaskManager(cores),
            emaThreadPredictor
        );
        emaThreadPredictor.shutdownOn(service.shutdown);
        return service;
    }
    public static DynamicExecutorService sharedQueueWatermark() {
        int cores = Runtime.getRuntime().availableProcessors();

        return new DynamicExecutorService(
                new SharedTaskManager(),
                new WatermarkPredictor(cores, cores * 2)
        );
    }
    public static  DynamicExecutorService sharedQueueEma() {
        EmaThreadPredictor emaThreadPredictor = new EmaThreadPredictor(0.7);

        DynamicExecutorService service = new DynamicExecutorService(
                new SharedTaskManager(),
                emaThreadPredictor
        );
        emaThreadPredictor.shutdownOn(service.shutdown);
        return service;
    }

    public DynamicExecutorService(TaskManager taskManager, ThreadManager threadManager) {
        this.taskManager = taskManager;
        this.threadManager = threadManager;

        this.threads = new ArrayList<>();
        for (int i = 0; i < this.threadManager.threadDeficit(0); i++) {
            activeThreads.incrementAndGet();
            this.threads.add(new Thread(this::runThread));
            this.threads.get(i).start();
        }
    }

    @Override
    public void shutdown() {
        shutdown.set(true);
    }

    @Override
    public List<Runnable> shutdownNow() {
        return null; // TODO:
    }

    @Override
    public boolean isShutdown() {
        return shutdown.get();
    }

    @Override
    public boolean isTerminated() {
        return activeThreads.get() == 0 && shutdown.get();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        shutdown();
        terminatorLock.lock();
        try {
            if(activeThreads.get() > 0)
                terminator.await();
        } finally {
            terminatorLock.unlock();
        }
        return true; // ?
    }

    @Override
    public void execute(Runnable command) {
        if(!shutdown.get()) {
            threadManager.taskAdded();
            taskManager.addTask(command);

            if(threadManager.threadDeficit(activeThreads.get()) > 0) {
                activeThreads.incrementAndGet();
                // Add a thread
                Thread thread = new Thread(this::runThread);
                thread.start();
                threads.add(thread);
            }
        }
    }

    private void runThread() {
        int threadId = (int)Thread.currentThread().getId();

        taskManager.addThread(threadId);
        try {
            while (
                (!shutdown.get() || taskManager.remainingTasks() > 0)
            || threadManager.shouldKillThread(activeThreads.get())) {
                // TODO: Check other reasons that could make us shut down

                Runnable task = taskManager.nextTask(10, TimeUnit.MILLISECONDS);
                if(task == null) {
                    // TODO: Count multiple idlings
                    if(threadManager.shouldKillIdleThread(activeThreads.get(), 10))
                        break;
                }
                else {
                    task.run();
                    threadManager.taskCompleted();
                }
            }
        } catch(InterruptedException e) {
            System.out.println(e);
        } finally {
            taskManager.removeThread(threadId);
            terminatorLock.lock();
            try {
                if (activeThreads.decrementAndGet() == 0 && isShutdown())
                    terminator.signalAll();
            } finally {
                terminatorLock.unlock();
            }
        }
    }

    private TaskManager taskManager;
    private ThreadManager threadManager;

    // Contains all threads created, including those that are since exited.
    private List<Thread> threads;

    private AtomicBoolean shutdown = new AtomicBoolean(false);
    private AtomicInteger activeThreads = new AtomicInteger(0);

    private ReentrantLock terminatorLock = new ReentrantLock();
    private Condition terminator = terminatorLock.newCondition();
}
