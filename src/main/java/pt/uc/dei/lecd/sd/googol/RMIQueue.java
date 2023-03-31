package pt.uc.dei.lecd.sd.googol;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementação da Queue remota via RMI.
 * Os métodos são "synchronized" para garantir que apenas uma thread possa acessar a lista de URLs
 * e executar as operações definidas, evitando possíveis conflitos de acesso e
 * garantindo que a lista seja manipulada de maneira consistente e segura em ambientes onde
 * múltiplas threads podem estar a aceder simultaneamente.
 */
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
        queue.addLast(element);
        log.info("Enqueue element {}. Size is {}", element, queue.size());

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