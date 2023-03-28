package pt.uc.dei.lecd.sd.googol;

import java.rmi.RemoteException;

public class Cliente {
    private final InterfaceBarrel barrel;

    public Cliente(InterfaceBarrel barrel) {
        this.barrel = barrel;
    }

    public String callPing() throws RemoteException {
        return barrel.ping();
    }
}
