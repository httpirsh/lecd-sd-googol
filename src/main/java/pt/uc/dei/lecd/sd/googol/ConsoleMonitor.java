package pt.uc.dei.lecd.sd.googol;

import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.Set;

public class ConsoleMonitor extends Monitor {

    public ConsoleMonitor() throws RemoteException {
        super();
    }

    @Override
    public void barrelNotification(String name) throws RemoteException {
        super.barrelNotification(name);
        System.out.println("Notified by barrel " + name);
        print("Active barrels:", getBarrelsNames());
    }

    @Override
    public void downloaderNotification(String name) throws RemoteException {
        super.downloaderNotification(name);
        System.out.println("Notified by downloader " + name);
        print("Active downloaders:", getDownloadersNames());
    }

    @Override
    public void topSearchChangedNotification(Set<String> topSearches) {
        super.topSearchChangedNotification(topSearches);
        System.out.println("Top search changed.");
        print("Top search terms list is:", getTopSearches());
    }

    private void print(String title, Set<String> lines) {
        System.out.println(title);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println("");
    }

    private void printCurrentState() {
        print("Active barrels:", getBarrelsNames());
        print("Active downloaders:", getDownloadersNames());
        print("Top search terms list is:", getTopSearches());
    }

    public static void main(String[] args) {
		ArgumentsProcessor arguments = new ArgumentsProcessor(args);
        String host = arguments.getHost();
        int port = arguments.getPort();

        try {
            System.out.println("Googol ConsoleMonitor starting with registry at rmi://" + host + ":" + port);
			ConsoleMonitor monitor = new ConsoleMonitor();
			monitor.connect(host, port);
            System.out.println("Googol ConsoleMonitor started.");
            monitor.printCurrentState();
            Scanner scanner = new Scanner(System.in);
            System.out.print("Press any key to stop monitor...");
            scanner.nextLine();
            scanner.close();
            monitor.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
