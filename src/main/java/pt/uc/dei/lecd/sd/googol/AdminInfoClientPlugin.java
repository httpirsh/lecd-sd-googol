package pt.uc.dei.lecd.sd.googol;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminInfoClientPlugin {
    private List<Downloader> downloaders;
    private List<IndexStorageBarrel> barrels;
    private List<String> searchTerms;

    public AdminInfoClientPlugin() {
        downloaders = new ArrayList<>();
        barrels = new ArrayList<>();
        searchTerms = new ArrayList<>();
    }

    public void addDownloader(Downloader downloader) {
        downloaders.add(downloader);
    }

    public void removeDownloader(Downloader downloader) {
        downloaders.remove(downloader);
    }

    public void addBarrel(IndexStorageBarrel barrel) {
        barrels.add(barrel);
    }

    public void removeBarrel(IndexStorageBarrel barrel) {
        barrels.remove(barrel);
    }

    public void addSearchTerm(String searchTerm) {
        searchTerms.add(searchTerm);
    }

    public void removeSearchTerm(String searchTerm) {
        searchTerms.remove(searchTerm);
    }


    // Code to update the admin page with the latest data from the arrays
//    public void updateAdminPage(String adminUsername, String newContent) throws RemoteException {
//
//        // Update the admin page content
//        admin.setPageContent(newContent);
//        System.out.println("Admin page content updated");
//    }

    public static void main(String[] args) throws RemoteException {
        AdminInfoClientPlugin adminPage = new AdminInfoClientPlugin();

        Downloader downloader1 = new Downloader();
        Downloader downloader2 = new Downloader();
        adminPage.addDownloader(downloader1);
        adminPage.addDownloader(downloader2);

        IndexStorageBarrel barrel1 = new IndexStorageBarrel();
        IndexStorageBarrel barrel2 = new IndexStorageBarrel();
        adminPage.addBarrel(barrel1);
        adminPage.addBarrel(barrel2);

        String[] searchTerms = {"apple", "banana", "orange"};
        adminPage.addSearchTerm(Arrays.asList(searchTerms).toString());

        // Call updateAdminInfo() periodically to refresh the admin info with the latest data
    }

    public String getAdminInfoText() {
        return "";


    }
}
