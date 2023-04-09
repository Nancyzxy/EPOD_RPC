package DataStructure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Cell {
	public ArrayList<Short> cellIdx;
	public HashMap<ArrayList<Short>,Cell> fullCells; //这个子空间的cell对应的所有全空间的cell
	public HashSet<Tuple> tuples;
	public double[] cellCenter; //子空间的中心点
	
	public Cell(ArrayList<Short> cellIdx){
		this.cellIdx = cellIdx;
		this.tuples = new HashSet<Tuple>();
	}

	
	public Cell(ArrayList<Short> cellIdx,  double[] cellCenter){
		this.cellIdx = cellIdx;
		this.tuples = new HashSet<Tuple>();
		this.cellCenter = cellCenter;
	}
	
	public Cell(ArrayList<Short> cellIdx, ArrayList<Short> fullDimCellIdx, double[] cellCenter, Boolean subDimFlag){
		this.cellIdx = cellIdx;
		this.cellCenter = cellCenter;
		this.tuples = new HashSet<Tuple>();
		if(subDimFlag) this.fullCells = new HashMap<ArrayList<Short>,Cell>();
	}
	
	public int getNumTuples() {
		return this.tuples.size();
	}
	
	public void addTuple(Tuple t, double[] fullDimCellCenter, Boolean subDimFlag) {
		this.tuples.add(t);
		if(subDimFlag) {
			if(!this.fullCells.containsKey(t.fullDimCellIdx))
				this.fullCells.put(t.fullDimCellIdx, new Cell(t.fullDimCellIdx, fullDimCellCenter));
			this.fullCells.get(t.fullDimCellIdx).addTuple(t, fullDimCellCenter, false);
		}
	}
	
	public void addTuple(Tuple t,  Boolean subDimFlag) {
		this.tuples.add(t);
		if(subDimFlag) {
			if(!this.fullCells.containsKey(t.fullDimCellIdx))
				this.fullCells.put(t.fullDimCellIdx, new Cell(t.fullDimCellIdx));
			this.fullCells.get(t.fullDimCellIdx).addTuple(t, false);
		}
	}
}
	