# Period Summary

## Method

LSH:

- Comprehensive experiments on datasets 

  - Variables: K, L

  - Measurement: recall and accuracy, # connections

CellID:

- Adopt **Hilbert curve** to optimize space index - the grid index 
- Define an algorithm (Greedy):
  - Input: support device and the # of points within 1-hop and 2-hop cells
  - Output: an order of support devices in which the device ask the vector
  
- With 100% recall
  - Comprehensive experiments on datasets
    - Measurement: recall and accuracy, # connections
- With tolerant error
  - model prediction(under brainstorming)

## Datasets

- Original Datasets

- Extreme cases

  - Cluster the datasets, and assign the different clusters to different devices (case 1)

  - Cluster the datasets, and distribute the same cluster to different devices (case 2)

- Normal cases
  - Cluster the datasets, and mix a% vector of each cluster then assign the different clusters to different devices

- Expected result:

  - No transfer between devices in case 1

  - All devices exchange vector in case 2

  - Other cases lie in between