public class LinkedList<T> implements IStack<T>, IDeque<T> {
    private final Node sentinel = new Node();
    int size = 0;

    @Override
    public T head() {
        if (size == 0) {
            throw new RuntimeException("The stack is empty");
        }

        return sentinel.next.element;
    }

    @Override
    public T tail() {
        if (size == 0) {
            throw new RuntimeException("The stack is empty");
        }

        return sentinel.prev.element;
    }

    @Override
    public void putFront(T e) {
        Node new_node = new Node();
        new_node.element = e;
        size++;

        new_node.prev = sentinel;
        new_node.next = sentinel.next;
        sentinel.next.prev = new_node;
        sentinel.next = new_node;
    }

    @Override
    public void putBack(T e) {
        Node new_node = new Node();
        new_node.element = e;
        size++;

        new_node.next = sentinel;
        new_node.prev = sentinel.prev;
        sentinel.prev.next = new_node;
        sentinel.prev = new_node;
    }

    @Override
    public T popFront() {
        if (size == 0) {
            throw new RuntimeException("The stack is empty");
        }

        T last = head();
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return last;
    }

    @Override
    public T popBack() {
        if (size == 0) {
            throw new RuntimeException("The stack is empty");
        }

        T last = tail();
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size--;
        return last;
    }

    @Override
    public T top() {
        return tail();
    }

    @Override
    public T pop() {
        return popBack();
    }

    @Override
    public void push(T element) {
        putBack(element);
    }

    @Override
    public int size() {
        return size;
    }

    private class Node {
        T element;
        Node next, prev;

        Node() {
            element = null;
            next = prev = this;
        }
    }
}
