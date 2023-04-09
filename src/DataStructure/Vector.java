/*
*      _______                       _        ____ _     _
*     |__   __|                     | |     / ____| |   | |
*        | | __ _ _ __ ___  ___  ___| |    | (___ | |___| |
*        | |/ _` | '__/ __|/ _ \/ __| |     \___ \|  ___  |
*        | | (_| | |  \__ \ (_) \__ \ |____ ____) | |   | |
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|   |_|
*                                                         
* -----------------------------------------------------------
*
*  TarsosLSH is developed by Joren Six at 
*  The School of Arts,
*  University College Ghent,
*  Hoogpoort 64, 9000 Ghent - Belgium
*  
* -----------------------------------------------------------
*
*  Info    : http://tarsos.0110.be/tag/TarsosLSH
*  Github  : https://github.com/JorenSix/TarsosLSH
*  Releases: http://tarsos.0110.be/releases/TarsosLSH/
* 
*/

package DataStructure;
import mtree.DistanceFunctions.EuclideanCoordinate;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * A Vector contains a vector of 'dimension' values. It serves as the main data
 * structure that is stored and retrieved. It also has an identifier (key).
 * 
 * @author Joren Six
 */
public class Vector implements Serializable,Comparable<Vector>,EuclideanCoordinate {
	
	/**
	 * Values are stored here.
	 */
	public double[] values;
	public int arrivalTime; //arrival order, used in the count-based window
	public int slideID;
	public Date arrivalRealTime; // arrival timestamp, used in the time-based window


	/**
	 * Creates a new vector with the requested number of dimensions.
	 * @param dimensions The number of dimensions.
	 */
	public Vector(int dimensions) {
		this(null,new double[dimensions]);
	}

	public Vector(double... values){
		this.values = values;
	}
	/**
	 * Copy constructor.
	 * @param other The other vector.
	 */
	public Vector(Vector other){
		//copy the values
//		this(other.getKey(),Arrays.copyOf(other.values, other.values.length));
	}


	/**
	 * Creates a vector with the values and a key
	 * @param key The key of the vector.
	 * @param values The values of the vector.
	 */
	public Vector(String key, double[] values){
		this.values = values;
	}

	@Override
	public int dimensions() {
		return values.length;
	}

	@Override
	public double get(int index) {
		return values[index];
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vector) {
			Vector that = (Vector) obj;
			if(this.arrivalTime != that.arrivalTime) return false;
			if(this.dimensions() != that.dimensions()) {
				return false;
			}
			for(int i = 0; i < this.dimensions(); i++) {
				if(this.values[i] != that.values[i]) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(Vector that) {
		int dimensions = Math.min(this.dimensions(), that.dimensions());
		for(int i = 0; i < dimensions; i++) {
			double v1 = this.values[i];
			double v2 = that.values[i];
			if(v1 > v2) {
				return +1;
			}
			if(v1 < v2) {
				return -1;
			}
		}

		if(this.dimensions() > dimensions) {
			return +1;
		}

		if(that.dimensions() > dimensions) {
			return -1;
		}

		return 0;
	}

	/**
	 * Moves the vector slightly, adds a value selected from -radius to +radius to each element.
	 * @param radius The radius determines the amount to change the vector.
	 */
	public void moveSlightly(double radius){
		Random rand = new Random();
		for (int d = 0; d < dimensions(); d++) {
			//copy the point but add or subtract a value between -radius and +radius
			double diff = radius + (-radius - radius) * rand.nextDouble();
			double point = get(d) + diff;
			set(d, point);
		}
	}

	/**
	 * Set a value at a certain dimension d.
	 * @param dimension The dimension, index for the value.
	 * @param value The value to set.
	 */
	public void set(int dimension, double value) {
		values[dimension] = value;
	}

	/**
	 * Calculates the dot product, or scalar product, of this vector with the
	 * other vector.
	 * 
	 * @param other
	 *            The other vector, should have the same number of dimensions.
	 * @return The dot product of this vector with the other vector.
	 * @exception ArrayIndexOutOfBoundsException
	 *                when the two vectors do not have the same dimensions.
	 */
	public double dot(Vector other) {
		double sum = 0.0;
		for(int i=0; i < dimensions(); i++) {
			sum += values[i] * other.values[i];
		}
		return sum;
	}

	
	public String toString(){
		StringBuilder sb= new StringBuilder();
		sb.append("values:[");
		for(int d=0; d < dimensions() - 1; d++) {
			sb.append(values[d]).append(",");
		}
		sb.append(values[dimensions()-1]).append("]");
		return sb.toString();
	}
}
