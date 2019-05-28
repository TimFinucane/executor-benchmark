import sys
import json
import matplotlib.pyplot as plt


def graph(x, y, xlabel, ylabel):
    plt.plot(x, y)
    plt.ylabel(ylabel)
    plt.xlabel(xlabel)


def create_metrics_graphs(metrics_set):
    legend = []
    for metrics in metrics_set:
        local_metrics = metrics['local']
        global_metrics = metrics['global']

        # Local Plotting
        first_local_sample_time = local_metrics['taskDataSamples'][0]['taskSubmitTime']
        task_start_times = [(l['taskSubmitTime'] - first_local_sample_time) / 1000000000 for l in local_metrics['taskDataSamples']]
        task_completion_times = [l['taskCompletionTime'] for l in local_metrics['taskDataSamples']]

        graph(task_start_times, task_completion_times, 'Time (seconds)', 'Task Completion Time (seconds)')

        # Global Plotting
        first_global_sample = global_metrics['globalDataSamples'][0]['sampleTime']
        sample_times = [(g['sampleTime'] - first_global_sample) / 1000000000 for g in global_metrics['globalDataSamples']]
        cpu_loads = [g['cpuLoad'] for g in global_metrics['globalDataSamples']]
        thread_counts = [g['threadCount'] for g in global_metrics['globalDataSamples']]
        responsiveness = [g['responsiveWorkCompleted'] for g in global_metrics['globalDataSamples']]

        #graph(sample_times, cpu_loads, 'Time (seconds)', 'CPU Load (%)')
        #graph(sample_times, thread_counts, 'Time (seconds)', 'Thread Count')
        #graph(sample_times, responsiveness, 'Time (seconds)', 'Responsiveness')

        legend.append(metrics['serviceType'] + ' ' + metrics['profileType'])

    plt.legend(legend, loc='lower right')
    plt.show()


if __name__ == '__main__':
    metrics_set = []
    for file in sys.argv[1:]:
        with open(file, 'r') as f:
            metrics_set.append(json.load(f))

    create_metrics_graphs(metrics_set)
