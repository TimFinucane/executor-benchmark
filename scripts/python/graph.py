import sys
import json
import matplotlib.pyplot as plt


def create_metrics_graphs(metrics):
    local_metrics = metrics['local']
    global_metrics = metrics['global']

    plt.plot(local_metrics['taskCompletionTimes'])
    plt.ylabel('some numbers')
    plt.show()
    

if __name__ == '__main__':
    with open(sys.argv[1], 'r') as f:
        metrics = json.load(f)

    create_metrics_graphs(metrics)
