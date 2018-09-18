package comp3506.assn1.adts;


/**
 * A three-dimensional data structure that holds items in a positional relationship to each other.
 * Each cell in the data structure can hold multiple items.
 * 
 * @author Richard Thomas <richard.thomas@uq.edu.au>
 *
 * @param <T> The type of element held in the data structure.
 */
public interface Cube<T> {
	
	/**
	 * Add an element at a fixed position.
	 * 
	 * @param element The element to be added at the indicated position.
	 * @param x X Coordinate of the position of the element.
	 * @param y Y Coordinate of the position of the element.
	 * @param z Z Coordinate of the position of the element.
	 * @throws IndexOutOfBoundsException If x, y or z coordinates are out of bounds.
	 */
	void add(int x, int y, int z, T element) throws IndexOutOfBoundsException;
	
	/**
	 * Return the 'oldest' element at the indicated position.
	 * 
	 * @param x X Coordinate of the position of the element.
	 * @param y Y Coordinate of the position of the element.
	 * @param z Z Coordinate of the position of the element.
	 * @return 'Oldest' element at this position or null if no elements at the indicated position.
	 * @throws IndexOutOfBoundsException If x, y or z coordinates are out of bounds.
	 */
	T get(int x, int y, int z) throws IndexOutOfBoundsException;
	
	/**
	 * Return all the elements at the indicated position.
	 * 
	 * @param x X Coordinate of the position of the element(s).
	 * @param y Y Coordinate of the position of the element(s).
	 * @param z Z Coordinate of the position of the element(s).
	 * @return An IterableQueue of all elements at this position or null if no elements at the indicated position.
	 * @throws IndexOutOfBoundsException If x, y or z coordinates are out of bounds.
	 */
	IterableQueue<T> getAll(int x, int y, int z) throws IndexOutOfBoundsException;
	
	/**
	 * Indicates whether there are more than one elements at the indicated position.
	 * 
	 * @param x X Coordinate of the position of the element(s).
	 * @param y Y Coordinate of the position of the element(s).
	 * @param z Z Coordinate of the position of the element(s).
	 * @return true if there are more than one elements at the indicated position, false otherwise.
	 * @throws IndexOutOfBoundsException If x, y or z coordinates are out of bounds.
	 */
	boolean isMultipleElementsAt(int x, int y, int z) throws IndexOutOfBoundsException;
	
	/**
	 * Removes the specified element at the indicated position.
	 * 
	 * @param element The element to be removed from the indicated position.
	 * @param x X Coordinate of the position.
	 * @param y Y Coordinate of the position.
	 * @param z Z Coordinate of the position.
	 * @return true if the element was removed from the indicated position, false otherwise.
	 * @throws IndexOutOfBoundsException If x, y or z coordinates are out of bounds.
	 */
	boolean remove(int x, int y, int z, T element) throws IndexOutOfBoundsException;
	
	/**
	 * Removes all elements at the indicated position.
	 * 
	 * @param x X Coordinate of the position.
	 * @param y Y Coordinate of the position.
	 * @param z Z Coordinate of the position.
	 * @throws IndexOutOfBoundsException If x, y or z coordinates are out of bounds.
	 */
	void removeAll(int x, int y, int z) throws IndexOutOfBoundsException;
	
	/**
	 * Removes all elements stored in the cube.
	 */
	void clear();
	
}
