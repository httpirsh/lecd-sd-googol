package pt.uc.dei.lecd.sd.googol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Esta classe contém os testes funcionais da classe {@link GoogolRegistry}.
 */
public class RegistryEntriesTest {

    @Test
    void shouldCreateWithAListOfEntries() {
        String[] entries = {};
        GoogolRegistry reg = new GoogolRegistry(entries);
    }

    @Test
    void shouldReturnEmptyListOfDownloaders() throws AccessException, RemoteException {
        String[] entries = {};
        GoogolRegistry reg = new GoogolRegistry(entries);
        List<String> downloaders = reg.getListOfDownloaders();
        Assertions.assertTrue(downloaders.isEmpty());
    }

    @Test
    void shouldReturnListOfDownloaders() throws AccessException, RemoteException {
        String[] entries = {"googol/downloaders/downloader_1", "someotherapp/someotherentry"};
        GoogolRegistry reg = new GoogolRegistry(entries);
        List<String> downloaders = reg.getListOfDownloaders();
        Assertions.assertTrue(downloaders.contains("googol/downloaders/downloader_1"));
        Assertions.assertFalse(downloaders.contains("someotherapp/someotherentry"));
    }

    @Test
    void shouldReturnNewCorrectNameWhenEntriesIsEmpty() throws AccessException, RemoteException {
        String[] entries = {};
        GoogolRegistry reg = new GoogolRegistry(entries);
        Assertions.assertEquals("googol/downloaders/downloader_1", reg.getNextDownloaderName());
    }

    @Test
    void shouldReturnNewCorrectName() throws AccessException, RemoteException {
        String[] entries = {"googol/downloaders/downloader_2"};
        GoogolRegistry reg = new GoogolRegistry(entries);
        Assertions.assertEquals("googol/downloaders/downloader_3", reg.getNextDownloaderName());
    }

    @Test 
    void shouldGetARandomBarrel() throws AccessException, RemoteException {
        String[] entries = {
            "googol/barrels/barrel_1",
            "googol/barrels/barrel_2",
            "googol/barrels/barrel_3",
            "googol/barrels/barrel_4",
            "googol/barrels/barrel_5",
            "googol/barrels/barrel_6",
            "googol/barrels/barrel_7",
            "googol/barrels/barrel_8",
            "googol/barrels/barrel_9",
        };

        GoogolRegistry reg = new GoogolRegistry(entries);

        for (String entry: entries) {
            assertEquals(entry, reg.getNextBarrelNameInRoundRobin());
        }
    }

}
