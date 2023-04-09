package DataStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Tuple extends Vector {
		public int id;
		public double[] value;
		public int nn;
		public int nnIn; //number of neighbors inside the same cell
		public int nnSafeOut; //number of succeeding neighbors outside the cell 
		public int nnUnSafeOut; //number of preceding neighbors outside the cell
		public boolean safeness;
		public HashMap<Integer, Integer> unSafeOutNeighbors; //number of neighbors
		public int lastNNSlideID;
		public ArrayList<Short> subDimCellIdx;
		public ArrayList<Short> fullDimCellIdx;

		public int last_calculate_time;
		
		public Tuple(int id, int slideID, double[] value) {
			this.id = id;
			this.slideID = slideID;
			this.value = value;
			this.unSafeOutNeighbors = new HashMap<Integer, Integer>();
			this.lastNNSlideID = -1;
			this.safeness = false;
			this.last_calculate_time = -1;
		}
		
		public int getNN() {
			nn = nnIn+nnSafeOut+nnUnSafeOut;
			return nn;
		}
		
		public void removeOutdatedNNUnsafeOut(int itr, int nS) {
			Iterator<Integer> it = unSafeOutNeighbors.keySet().iterator();
			
			while (it.hasNext()) {
				int slideIDKey = it.next();
				if (slideIDKey <= itr-nS) {
					nnUnSafeOut -= unSafeOutNeighbors.get(slideIDKey);
					it.remove();
				}
			}
		}
	}
