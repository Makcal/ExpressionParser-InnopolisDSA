public interface IDeque<T> extends ICollection {
    T head();

    T tail();

    void putFront(T e);

    void putBack(T e);

    T popFront();

    T popBack();
}
