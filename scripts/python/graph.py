import sys
import json
import matplotlib.pyplot as plt


def graph(x, y, xlabel, ylabel):
    plt.plot(x, y)
    plt.ylabel(ylabel)
    plt.xlabel(xlabel)
    plt.show()


def create_metrics_graphs(metrics):
    local_metrics = metrics['local']
    global_metrics = metrics['global']

    # Local Plotting
    first_local_sample_time = local_metrics['taskDataSamples'][0]['taskSubmitTime']
    task_start_times = [l['taskSubmitTime'] - first_local_sample_time for l in local_metrics['taskDataSamples']]
    task_completion_times = [l['taskCompletionTime'] for l in local_metrics['taskDataSamples']]

    graph(task_start_times, task_completion_times, 'Time (nano seconds)', 'Task Completion Time (seconds)')

    # Global Plotting
    first_global_sample = global_metrics['globalDataSamples'][0]
    sample_times = [g['sampleTime'] - first_global_sample['sampleTime'] for g in global_metrics['globalDataSamples']]
    cpu_loads = [g['cpuLoad'] for g in global_metrics['globalDataSamples']]
    thread_counts = [g['threadCount'] for g in global_metrics['globalDataSamples']]
    responsiveness = [g['responsiveWorkCompleted'] for g in global_metrics['globalDataSamples']]

    graph(sample_times, cpu_loads, 'Time (nano seconds)', 'CPU Load (%)')
    graph(sample_times, thread_counts, 'Time (nano seconds)', 'Thread Count')
    graph(sample_times, responsiveness, 'Time (nano seconds)', 'Responsiveness')


if __name__ == '__main__':
    with open(sys.argv[1], 'r') as f:
        metrics = json.load(f)

    create_metrics_graphs(metrics)
