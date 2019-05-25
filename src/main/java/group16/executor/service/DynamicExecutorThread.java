package group16.executor.service;

import group16.executor.service.task.management.TaskManager;

public class DynamicExecutorThread extends Thread {

    private TaskManager taskManager;

    public DynamicExecutorThread(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void run() {
        // TODO: make this continue until stopped.
        try {
            taskManager.nextTask().call();
        } catch (Exception e) {
            System.out.println("Task threw exception at DynamicExecutorService thread level:");
            e.printStackTrace();
        }
    }
}
