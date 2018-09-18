package comp3506.assn1.adts;


/**
 * Queue with ability to iterate over all the elements in the queue.
 * 
 * @author Richard Thomas <richard.thomas@uq.edu.au>
 *
 * @param <T> Type of the elements held in the queue.
 */
public interface IterableQueue<T> extends Iterable<T> {
	
	/**
	 * Add a new element to the end of the queue.
	 * 
	 * @param element The element to be added to the queue.
	 * @throws IllegalStateException Queue cannot accept a new element (e.g. queue space is full).
	 */
	void enqueue(T element) throws IllegalStateException;
	
	/**
	 * Remove and return the element at the head of the queue.
	 * 
	 * @return Element at that was at the head of the queue.
	 * @throws IndexOutOfBoundsException Queue is empty and nothing can be dequeued.
	 */
	T dequeue() throws IndexOutOfBoundsException;
	
	/**
	 * @return Number of elements in the queue.
	 */
	int size();
	
}
