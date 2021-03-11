package com.example.calculator;

public class NodeList<T extends ListNode> {
    T head, tail = null;

    public void add(T node) {
        node.next = null;
        node.previous = null;
        if (head == null) {
            head = tail = node;
            head.previous = null;
            tail.next = null;
        } else {
            tail.next = node;
            node.previous = tail;
            tail = node;
            tail.next = null;
        }
    }

    public void remove(T node) {
        if(node == head){
            head = (T) node.next;
        }else if(node == tail){
            tail = (T) node.previous;
        }else{
            if (node.previous == null || node.next == null) {
                throw new NullPointerException();
            }
            node.previous.next = node.next;
            node.next.previous = node.previous;
        }
        node.next = null;
        node.previous = null;
    }
}