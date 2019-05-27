import sys
import json
import matplotlib.pyplot as plt


def create_metrics_graphs(metrics):
    local_metrics = metrics['local']
    global_metrics = metrics['global']

    first_sample = global_metrics['globalDataSamples'][0]

    sample_times = [g['sampleTime'] - first_sample['sampleTime'] for g in global_metrics['globalDataSamples']]
    cpu_loads = [g['cpuLoad'] for g in global_metrics['globalDataSamples']]
    thread_counts = [g['threadCount'] for g in global_metrics['globalDataSamples']]

    plt.plot(sample_times, cpu_loads)
    plt.ylabel('CPU Load (%)')
    plt.xlabel('Time (nano seconds)')
    plt.show()

    plt.plot(sample_times, thread_counts)
    plt.ylabel('Thread Count')
    plt.xlabel('Time (nano seconds)')
    plt.show()


if __name__ == '__main__':
    with open(sys.argv[1], 'r') as f:
        metrics = json.load(f)

    create_metrics_graphs(metrics)
