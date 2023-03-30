package pt.uc.dei.lecd.sd.googol;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Queue extends UnicastRemoteObject {
    private LinkedList<Object> queue;
    private String name;

    public Queue(String name) throws RemoteException {
        super();
        log.info("Creating queue {}", this.name);
        this.name = name;
        queue = new LinkedList<Object>();
    }

    public synchronized void enqueue(Object element) throws RemoteException {
        log.info("enqueue element {}", element);
        queue.addLast(element);
    }

    public synchronized Object dequeue() throws RemoteException {
        log.info("dequeueing... size is {}", size());

        if (queue.isEmpty()) {
            return null;
        }
        return queue.removeFirst();
    }

    public synchronized int size() throws RemoteException {
        return queue.size();
    }

    public synchronized boolean isEmpty() throws RemoteException {
        return queue.isEmpty();
    }
}