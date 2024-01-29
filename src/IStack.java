public interface IStack<T> extends ICollection {
    T top();

    T pop();

    void push(T elem);
}
