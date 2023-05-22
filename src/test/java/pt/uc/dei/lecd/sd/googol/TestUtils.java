package pt.uc.dei.lecd.sd.googol;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Este código é uma classe utilitária que fornece um método para obter uma instância do registro RMI para um determinado número de porta.
 * O método getRegistryInstance recebe um número de porta como argumento e tenta criar um registro RMI nessa porta.
 * Se a criação do registro falhar, o método obtém uma referência ao registro existente nessa porta.
 * Em seguida, o método retorna o registro para o chamador.
 *
 * O objetivo desta classe é fornecer um método comum para obter uma instância do registro RMI que pode ser usado em vários testes.
 * Isso pode ajudar a reduzir a duplicação de código e a simplificar a implementação dos testes.
 */
public class TestUtils {
    static Registry startLocalRegistry(int port) throws RemoteException {
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            registry = LocateRegistry.getRegistry(port);
        }
        return registry;
    }
}
