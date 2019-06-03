import sys
import os
import json

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

folder = sys.argv[1] if len(sys.argv) > 1 else '../metrics'

results = {}

for filename in os.listdir(folder):
    if not filename.endswith('.json'):
        continue

    with open(os.path.join(folder, filename)) as json_file:
        data = json.load(json_file)

        service_name = filename.split('_')[0]
        profile_name = filename.split('_')[1]

        # Extract data
        submit_times = np.array([sample['submitTime'] for sample in data['local']['taskDataSamples']])
        start_times = np.array([sample['startTime'] for sample in data['local']['taskDataSamples']])
        end_times = np.array([sample['endTime'] for sample in data['local']['taskDataSamples']])

        sample_times = np.array([sample['sampleTime'] for sample in data['global']['globalDataSamples']])
        cpu_usage = np.array([sample['cpuLoad'] for sample in data['global']['globalDataSamples']])
        background_tasks_completed = np.array([sample['responsiveWorkCompleted'] for sample in data['global']['globalDataSamples']])

        summary_filename = filename.replace('.json', '_summary.txt')

        mean_completion_time = (end_times - submit_times).mean()
        max_completion_time = (end_times - submit_times).max()
        mean_cpu_usage = cpu_usage.mean()
        responsiveness_std = background_tasks_completed.std()

        if profile_name not in results:
            results[profile_name] = {}
        results[profile_name][service_name] = (
            mean_completion_time,
            max_completion_time,
            mean_cpu_usage,
            responsiveness_std
        )

        with open(os.path.join(folder, summary_filename), 'w', encoding='utf-8') as summary_file:
            summary_file.write('mean completion time - {:010.7f}\n'.format(mean_completion_time))
            summary_file.write('max completion time  - {:010.7f}\n'.format(max_completion_time))
            summary_file.write('mean cpu usage       - {:05.2f}\n'.format(mean_cpu_usage))
            summary_file.write('responsiveness std   - {:05.1f}\n'.format(responsiveness_std))

# Now make the graphs
profile_display_names = {
    'DynamicProfile-ManyTasks-10Peaks': 'Peaked',
    'IOProfile': 'IO',
    'UniformProfile-DynamicLoad': 'Uniform',
    'UniformProfile-HeavyTasks-StaticLoad': 'Static',
    'IrregularProfile': 'Irregular'
}
result_types = [
    'mean completion time',
    'max completion time',
    'mean cpu usage',
    'responsiveness (std)'
]

# Write table:
for i in range(len(result_types)):
    table_name = "TABLE_" + result_types[i].replace(' ', '-').replace('(', '').replace(')', '') + "_table"

    with open(os.path.join(folder, table_name + ".txt"), 'w', encoding='utf-8') as table_file:
        services = sorted(list(set([service for profile in results for service in results[profile]])))
        
        table_file.write("profile, " + ", ".join(services) + "\n" + 
            "\n".join(["{:9s}".format(profile_display_names[profile]) + ", " +
                ", ".join([("{:" + ("07.4f" if i < 2 else "5.2f" if i == 2 else "5.1f") + "}").format(
                    results[profile][service][i] / (np.mean([service[i] for service in results[profile].values()]) if i < 2 else 1)) for service in sorted(results[profile])])
                for profile in results
            ])
        )


for i in range(len(result_types)):
    data = pd.DataFrame([
        [profile_display_names[profile], service, results[profile][service][i] / (np.mean([service[i] for service in results[profile].values()]) if i < 2 else 1)]
        for profile in results
        for service in results[profile]],
        columns=['profile', 'service', 'value']
    )
    data.pivot('profile', 'service', 'value').plot(kind='bar', title=result_types[i])
    plt.show()
