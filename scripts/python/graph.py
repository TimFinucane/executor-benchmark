import sys
import json

import numpy as np
import matplotlib.pyplot as plt


def graph(x, y, xlabel, ylabel):
    plt.scatter(x, y)
    plt.ylabel(ylabel)
    plt.xlabel(xlabel)



def create_metrics_graphs(metrics_set):
    legend = []
    for metrics in metrics_set:
        local_metrics = metrics['local']
        global_metrics = metrics['global']

        # Local Plotting
        task_samples = local_metrics['taskDataSamples']
        submit_times = np.array([sample['submitTime'] for sample in task_samples])
        start_times = np.array([sample['startTime'] for sample in task_samples])
        end_times = np.array([sample['endTime'] for sample in task_samples])
        
        plt.title("Start times")
        plt.ylabel("Number of tasks")
        plt.xlabel("Start Time (seconds)")
        plt.hist(submit_times, bins=1000)
        plt.show()
        plt.title("Completion Times (From Submittal)")
        plt.ylabel("Number of tasks")
        plt.xlabel("Time taken to complete (seconds)")
        plt.hist(end_times - submit_times, bins=1000)
        plt.show()
        plt.title("Processing Times (From Task Start)")
        plt.ylabel("Number of tasks")
        plt.xlabel("Time taken to process (seconds)")
        plt.hist(end_times - start_times, bins=1000)
        plt.show()

        plt.title("Completion Times vs. Submit times")
        graph(submit_times, end_times - submit_times, 'Task Start Time (seconds)', 'Task Completion Time (seconds)')
        plt.show()

        # Global Plotting
        first_global_sample = global_metrics['globalDataSamples'][0]['sampleTime']
        sample_times = [(g['sampleTime'] - first_global_sample) / 1000000000 for g in global_metrics['globalDataSamples']]
        cpu_loads = [g['cpuLoad'] for g in global_metrics['globalDataSamples']]
        thread_counts = [g['threadCount'] for g in global_metrics['globalDataSamples']]
        responsiveness = [g['responsiveWorkCompleted'] for g in global_metrics['globalDataSamples']]

        graph(sample_times, cpu_loads, 'Sample Time (seconds)', 'CPU Load (%)')
        graph(sample_times, thread_counts, 'Sample Time (seconds)', 'Thread Count')
        graph(sample_times, responsiveness, 'Sample Time (seconds)', 'Responsiveness')

        legend.append(metrics['serviceType'] + ' ' + metrics['profileType'])

    plt.legend(legend, loc='lower right')
    plt.show()


if __name__ == '__main__':
    metrics_set = []
    for file in sys.argv[1:]:
        with open(file, 'r') as f:
            metrics_set.append(json.load(f))

    create_metrics_graphs(metrics_set)
