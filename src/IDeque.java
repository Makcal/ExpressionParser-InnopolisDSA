public interface IDeque<T> extends IQueue<T> {
    T head();

    T tail();

    void putFront(T e);

    T popBack();
}
