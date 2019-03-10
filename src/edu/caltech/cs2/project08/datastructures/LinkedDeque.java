package edu.caltech.cs2.project08.datastructures;

import edu.caltech.cs2.project08.interfaces.IDeque;
import edu.caltech.cs2.project08.interfaces.IQueue;
import edu.caltech.cs2.project08.interfaces.IStack;

import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    private class Node<E> {
        public E data;
        public Node<E> next;
        public Node<E> previous;

        Node( E data) {
            this.data = data;
        }
    }

    private Node<E> head;
    private Node<E> end;
    private int size;

    public LinkedDeque() {
        this.head = null;
        this.end = null;
        this.size = 0;
    }
    @Override
    public void addFront(E e) {
        Node<E> temp = new Node<>(e);
        if (head == null) {
            head = temp;
            end = temp;
            size++;
        } else {
            temp.next = head;
            head.previous = temp;
            head = temp;
            size++;
        }
    }

    @Override
    public void addBack(E e) {
        Node<E> temp = new Node<>(e);
        if (head == null) {
            head = temp;
            end = temp;
            size++;
        } else {
            end.next = temp;
            end.next.previous = end;
            end = end.next;
            size++;
        }
    }

    @Override
    public E removeFront() {
        if (head == null) {
            return null;
        }
        E front = head.data;
        head = head.next;
        size--;
        return front;
    }

    @Override
    public E removeBack() {

        if (head == null || end == null) {
            return null;
        }
        if (end == head) {
            E back = head.data;
            head = null;
            end = null;
            size--;
            return back;
        } else if (end.previous == null){
            E back = end.data;
            end = head;
            size--;
            return back;
        } else{
            E back = end.data;
            end = end.previous;
            end.next = null;
            size--;
            return back;
        }
    }

    @Override
    public boolean enqueue(E e) {
        try {
            addBack(e);
            return true;
        } catch (NullPointerException el) {
            el.printStackTrace();
            return false;
        }
    }

    @Override
    public E dequeue() {
        if (head == null) {
            return null;
        }
        return removeFront();
    }

    @Override
    public boolean push(E e) {
        try {
            addFront(e);
            return true;
        } catch (NullPointerException el) {
            el.printStackTrace();
            return false;
        }
    }

    @Override
    public E pop() {
        if (head == null) {
            return null;
        }
        return removeFront();
    }

    @Override
    public E peek() {
        return peekFront();
    }

    @Override
    public E peekFront() {
        if (head == null) {
            return null;
        }
        return head.data;
    }

    @Override
    public E peekBack() {
        if (head == null) {
            return null;
        }
        return end.data;
    }

    private class MyIterator<E> implements java.util.Iterator<E> {

        private Node<E> curr;

        public MyIterator(Node<E> head) {
            this.curr = head;
        }

        @Override
        public boolean hasNext() {
            return curr != null;
        }


        @Override
        public E next() {
            if (this.hasNext()) {
                E datas = this.curr.data;
                this.curr = this.curr.next;
                return datas;
            } else {
                return null;
            }
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator<E>(head);
    }

    @Override
    public int size() {
        if (head == null) {
            return 0;
        }
        return size;
    }

    @Override
    public String toString() {

        if (head == null) {
            return "[]";
        }
        String sstart = "[";
        for (Node<E> curr = head; curr != null; curr = curr.next) {
            if (curr.data != null) {
                sstart += curr.data.toString() + ", ";
            }
        }
        if (sstart.equals("[")) {
            sstart = "[, ";
        }
        return sstart.substring(0, sstart.length() - 2) + "]";
        /*
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
        return sstart.substring(0, sstart.length() - 2) + "]";*/
    }
}