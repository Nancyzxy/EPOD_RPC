import os

from sklearn.cluster import KMeans
import pandas as pd
import numpy as np


# Configuration file
n_clusters = 4
mix_rate = 10
data = pd.read_table('C:\\Users\\14198\\Desktop\\outlier_detection\\NETS\\Datasets\\tao.txt', sep=',', header=None)
prefix = \
    "C:\\Users\\14198\\Desktop\\outlier_detection\\NETS\\Datasets\\DeviceId_data\\Device_" + \
    str(n_clusters) + "_TAO_K_" + str(mix_rate) + "\\"


df = pd.DataFrame(data)
X = np.array(df)
kmeans = KMeans(n_clusters=n_clusters, random_state=0).fit(X)
clusters = []
for i in range(n_clusters):
    clusters.append([])
for i in range(kmeans.labels_.size):
    clusters[kmeans.labels_[i]].append(X[i])
for i in range(n_clusters):
    print(len(clusters[i]))

# Todo
# mix 10% 0->1 1->2 2->3 3->0
# count = int(len(clusters[0]) * mix_rate / 100)
# before = clusters[0][0:count]
# for i in range(n_clusters):
#     j = (i + 1) % n_clusters
#     after = clusters[j][0:count]
#     clusters[j][0:count] = before
#     before = after
# for i in range(n_clusters):
#     random.shuffle(clusters[i])


os.mkdir(prefix, 0o0755)
files = []
for i in range(n_clusters):
    file = open(prefix + str(i) + ".txt", "w+")
    files.append(file)

for i in range(len(clusters)):
    for j in range(len(clusters[i])):
        for k in range(len(clusters[i][j])):
            if k == len(clusters[i][j]) - 1:
                files[i].write(str(clusters[i][j][k]))
            else:
                files[i].write(str(clusters[i][j][k])+",")
        files[i].write("\n")

for i in range(n_clusters):
    files[i].close()

