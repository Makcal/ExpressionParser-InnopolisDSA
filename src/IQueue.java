public interface IQueue<T> extends ICollection, Iterable<T> {

    void putBack(T e);

    T popFront();
}
