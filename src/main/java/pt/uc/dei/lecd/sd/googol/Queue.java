package pt.uc.dei.lecd.sd.googol;

import java.nio.channels.AlreadyBoundException;
import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementação de uma Queue remota via RMI.
 * Os métodos são "synchronized" para garantir que apenas uma thread possa acessar a lista de URLs
 * e executar as operações definidas, evitando possíveis conflitos de acesso e
 * garantindo que a lista seja manipulada de maneira consistente e segura em ambientes onde
 * múltiplas threads podem estar a aceder simultaneamente.
 */
@Slf4j
public class Queue extends UnicastRemoteObject implements InterfaceQueue {
    private LinkedList<Object> queue;
    private GoogolRegistry registry;

    public Queue() throws RemoteException {
        super();
        queue = new LinkedList<Object>();
        log.info("Creating queue");
    }

     /**
     * O método enqueue adiciona um elemento à fila. O elemento é passado como um objeto genérico.
     */
    public synchronized void enqueue(Object element) throws RemoteException {
        queue.addLast(element);
        log.info("Enqueue element {}. Size is {}", element, queue.size());

    }

    /**
     * O método dequeue remove e retorna o elemento na frente da fila. Se a fila estiver vazia, retorna nulo.
     */
    public synchronized Object dequeue() throws RemoteException {
        log.info("dequeueing... size is {}", size());

        if (queue.isEmpty()) {
            return null;
        }
        return queue.removeFirst();
    }

    /**
     * O método size retorna o número de elementos na fila.
     */
    public synchronized int size() throws RemoteException {
        return queue.size();
    }

    public static void main(String[] args) throws RemoteException {
		ArgumentsProcessor arguments = new ArgumentsProcessor(args);
        String host = arguments.getHost();
        int port = arguments.getPort();

        try {
            Queue queue = new Queue();
            queue.start(host, port);
            System.out.println("Googol queue started");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Press any key to stop RemoteQueue...");
            scanner.nextLine();
            scanner.close();
            queue.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    public boolean stop() throws AccessException, RemoteException, NotBoundException {
        try {
            log.info("Stopping queue");
            return registry.unbind(this);
        } catch (NoSuchObjectException e) {
            log.error("Error stopping queue.", e);
            return false;
        }
    }

    public boolean start(String host, int port) throws java.rmi.AlreadyBoundException {
        try {
            log.info("Starting queue at rmi://{}:{}", host, port);
            registry = new GoogolRegistry(host, port);
            registry.bind(this);
            return true;
        } catch (RemoteException | AlreadyBoundException e) {
            log.error("Error starting queue object.", e);
            return false;
        } 
    }

}