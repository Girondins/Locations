package girondins.locations;

import java.util.LinkedList;

/**
 * Created by Girondins on 05/10/15.
 */
public class Buffer<T> {
    private LinkedList<T> buffer = new LinkedList<T>();

    public synchronized void put(T element) {
        buffer.addLast(element);
        notifyAll();
    }

    public synchronized T get() throws InterruptedException {
        while(buffer.isEmpty()) {
            wait();
        }
        return buffer.removeFirst();
    }
}
