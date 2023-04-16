package utils;

import RPC.Vector;
import DataStructure.Tuple;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class StreamGenerator {
	private double[] maxValues;
	private double[] minValues;
	private BufferedReader br; 
	private String filePath;
	private List<Integer> priorityList;
	private final boolean timeBased;
	private DataGenerator dataGenerator;
	private int tid=0;

	public StreamGenerator (String dataset, int random, boolean timeBased) throws Throwable {
		/* Datasets */
		this.timeBased = timeBased;
		switch (dataset) {
			default: case "HPC":
				if (timeBased){
					filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\Timestamp_data\\Time_household2.txt";
				}
				else filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\household2.txt";
				this.maxValues = new double[]{10.67, 1.39, 252.14, 46.4, 80, 78, 31};
				this.minValues = new double[]{0.076, 0, 223.49, 0.2, 0 , 0 , 0};		
				this.priorityList = Arrays.asList(new Integer[] {2,3,0,1,6,5,4});
				sortPriority(random);
				break;
			case "EM":
				if (timeBased){
					filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\Timestamp_data\\Time_ethylene.txt";
				}
				else filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\ethylene.txt";
				/*Original */
				maxValues = new double[]{2993.82,9422.14,5567.44,6127.68,4420.84,5593.51,4717.23,5376.15,4134.21,3295.82,4493.98,4037.97,4540.98,5108.82,4417.46,3468.07};
				minValues = new double[]{-56.48,1664.2,-47.78,-6.83,-12.68,-41.98,-15.28,-11.87,2976.53,2367.65,789.55,671.67,460.37,453.42,862.61,659.45};
				this.priorityList = Arrays.asList(new Integer[] {8,9,10,15,11,14,2,1,3,6,7,0,4,13,12,5});
				sortPriority(random);
				break;
			case "TAO":
				if (timeBased){
					filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\Timestamp_data\\Time_tao.txt";
				}
				else filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\tao.txt";
				this.maxValues = new double[]{75.39,101.68,30.191};
				this.minValues = new double[]{-9.99,-9.99,-9.999};
				this.priorityList = Arrays.asList(new Integer[] {1,2,0});
				sortPriority(random);
				break;
			case "STK":
				if (timeBased){
					filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\Timestamp_data\\Time_stock.txt";
				}
				else filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\stock.txt";
				this.maxValues = new double[]{9930};
				this.minValues = new double[]{0};	
				this.priorityList = Arrays.asList(new Integer[] {0});
				sortPriority(random);
				break;
			case "GAU":
				if (timeBased){
					filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\Timestamp_data\\Time_gaussian.txt";
				}
				else filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\gaussian.txt";
				this.maxValues = new double[]{100.81};
				this.minValues = new double[]{-3.5042};
				this.priorityList = Arrays.asList(new Integer[] {0});
				sortPriority(random);
				break;
			case "FC":
				if (timeBased){
					filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\Timestamp_data\\Time_fc.data";
				}
				else filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\fc.data";
				this.maxValues = new double[]{3858,360,66,1397,601,7117,254,254,254,7173,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,7};
				this.minValues = new double[]{1859,0,0,0,-173,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1};
				this.priorityList = Arrays.asList(new Integer[] {1,5,9,8,0,3,6,2,7,4,54,10,12,42,36,45,13,46,43,25,23,35,44,37,51,26,52,33,24,11,17,53,19,15,32,30,14,29,16,39,31,18,48,22,41,34,47,40,27,50,21,49,20,28,38});
				sortPriority(random);
				break;
			case "GAS":
				if (timeBased){
					filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\Timestamp_data\\Time_gas.txt";
				}
				else filePath = "C:\\Users\\14198\\Desktop\\outlier_detection\\Datasets\\gas.txt";
				this.maxValues = new double[]{13.7333,11.3155,11.3742,12.7548,378.75,73.8178,102.575,99.8881,30.3254,77.6805};
				this.minValues = new double[]{5.43146,1.82066,1.6269,2.28292,1.90129,5.58795,1.22037,1.43053,24.4344,44.6604};
				this.priorityList = Arrays.asList(new Integer[] {8,1,3,5,2,9,0,7,6,4});
				sortPriority(random);
				break;	
		}
		//TODO
//		dataGenerator = DataGenerator.getInstance(dataset,0);
		//System.out.println("Priority of dims: "+priorityList);
	}
	
	public void sortPriority(int random){
		if(random>0) Collections.shuffle(this.priorityList);
		double[] new_maxValues = new double[priorityList.size()];
		double[] new_minValues = new double[priorityList.size()];
		for(int i=0; i< priorityList.size(); i++) {
			new_maxValues[i] = maxValues[this.priorityList.get(i)];
			new_minValues[i] = minValues[this.priorityList.get(i)];
		}
		this.maxValues = new_maxValues;
		this.minValues = new_minValues;
	}
	
	public ArrayList<Tuple> getNewSlideTuples(int itr, int S) throws Throwable {
		ArrayList<Tuple> newSlide = new ArrayList<Tuple>();
		this.br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();

		if (!timeBased) {
			int tid = 0;
			while (line != null) {
				if (tid >= itr * S) {
					String[] rawValues = line.split(",");
					double[] value = new double[rawValues.length];

					int j = 0;
					for (int i : priorityList) {
						value[j] = Double.parseDouble(rawValues[i]);
						j++;
					}
					Tuple tuple = new Tuple(tid, itr, value);
					newSlide.add(tuple);
				}
				tid++;
				if (tid == (itr + 1) * S) break;
				line = br.readLine();
			}
		}
		else {
			//TODO
			Date currentRealTime = null;/*dataGenerator.getFirstTimeStamp(DataGenerator.datasetPathWithTime);*/
			currentRealTime.setTime(currentRealTime.getTime() + (long) Constants.S * 10 * 1000 * itr);
			ArrayList<Vector>tmp = dataGenerator.getTimeBasedIncomingData(currentRealTime, Constants.S*10);
			for (Vector v:tmp){
				double[] value = new double[v.values.size()];
				int j = 0;
				for (int i : priorityList) {
					value[j] = v.values.get(i);
					j++;
				}
				Tuple tuple = new Tuple(tid++, itr, value);
				newSlide.add(tuple);
			}
		}
		return newSlide;
	}

	public double[] getMaxValues() {
		return this.maxValues;
	}
	public double[] getMinValues(){
		return this.minValues;
	}
	
	public void setPriorityList(Integer[] list){
		this.priorityList = Arrays.asList(list);
	}
	
}

