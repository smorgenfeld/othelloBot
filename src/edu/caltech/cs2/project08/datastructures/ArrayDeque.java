package edu.caltech.cs2.project08.datastructures;

import edu.caltech.cs2.project08.interfaces.IDeque;
import edu.caltech.cs2.project08.interfaces.IQueue;
import edu.caltech.cs2.project08.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    private static final int defaultCapacity = 10;
    private static final int garbage = 0;
    private E[] data;
    private int size;

    public ArrayDeque() {
        this(defaultCapacity);
    }

    public ArrayDeque(int initialCapacity) {
        this.data = (E[]) new Object[initialCapacity];
        size = 0;
    }

    @Override
    public void addFront(E e) {
        int newCapacity = data.length + 1;
        E[] temp = (E[]) new Object[newCapacity];
        System.arraycopy(data, 0, temp, 1, size);
        data = temp;
        size++;;
        data[0] = e;
    }

    @Override
    public void addBack(E e) {
        while (size == data.length) {
            int newCapacity = data.length * 2;
            E[] temp = (E[]) new Object[newCapacity];
            System.arraycopy(data, 0, temp, 0, size);
            data = temp;
        }
        size++;
        data[size - 1] = e;
    }

    @Override
    public E removeFront() {
        if (size == 0) {
            return null;
        }
        E thing = data[0];
        size--;
        E[] temp = (E[]) new Object[data.length];
        System.arraycopy(data, 1, temp, 0, size);
        data = temp;
        return thing;
    }

    @Override
    public E removeBack() {
        if (size == 0) {
            return null;
        }
        size--;
        return data[size];
    }

    @Override
    public boolean enqueue(E e) {
        try {
            addFront(e);
            return true;
        } catch (ArrayIndexOutOfBoundsException el) {
            el.printStackTrace();
            return false;
        }
    }

    @Override
    public E dequeue() {
        if (data.length == 0) {
            return null;
        }
        return removeBack();
    }

    @Override
    public boolean push(E e) {
        try {
            addBack(e);
            return true;
        } catch (ArrayIndexOutOfBoundsException el) {
            el.printStackTrace();
            return false;
        }
    }

    @Override
    public E pop() {
        if (size == 0) {
            return null;
        }
        return removeBack();
    }

    @Override
    public E peek() {
        return peekBack();
    }

    @Override
    public E peekFront() {
        if (size == 0) {
            return null;
        }
        return data[0];
    }

    @Override
    public E peekBack() {
        if (size==0) {
            return null;
        }
        return data[size - 1];
    }

    private class MyIterator<E> implements Iterator<E>{
        private int index;

        public MyIterator(){
            index = 0;
        }

        public boolean hasNext() {
            return index < size;
        }
        public E next() {
            if (this.hasNext()) {
                E thing = (E) data[index];
                index++;
                return thing;
            }
            else {
                return null;
            }
        }
    };
    @Override
    public Iterator<E> iterator() {
        return new MyIterator<E>();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        String sstart = "[";
        for (int i = 0; i < size; i++) {
            if (data[i]!= null) {
                sstart += data[i].toString() + ", ";
            }
        }
        if (sstart.equals("[")) {
            sstart = "[, ";
        }
        return sstart.substring(0, sstart.length() - 2) + "]";
    }
}