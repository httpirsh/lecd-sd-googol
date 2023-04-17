package pt.uc.dei.lecd.sd.googol;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Este código é uma interface Java que define o contrato para uma fila remota RMI (Interface de Método Remoto).
 */
public interface Queue extends Remote {

    /**
     * O método enqueue adiciona um elemento à fila. O elemento é passado como um objeto genérico.
     */
    public void enqueue(Object element) throws RemoteException;

    /**
     * O método dequeue remove e retorna o elemento na frente da fila. Se a fila estiver vazia, retorna nulo.
     */
    public Object dequeue() throws RemoteException;

    /**
     * O método size retorna o número de elementos na fila.
     */
    public int size() throws RemoteException;
}

