package pt.uc.dei.lecd.sd.googol;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestUtils {
    static Registry getRegistryInstance(int port) throws RemoteException {
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            registry = LocateRegistry.getRegistry(port);
        }
        return registry;
    }
}
