package pt.uc.dei.lecd.sd.googol;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RMIQueue extends UnicastRemoteObject implements Queue {
    private LinkedList<Object> queue;
    private String name;

    public RMIQueue(String name) throws RemoteException {
        super();
        this.name = name;
        queue = new LinkedList<Object>();
        log.info("Creating queue {}", this.name);
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