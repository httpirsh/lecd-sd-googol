package pt.uc.dei.lecd.sd.googol;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Esta classe contém os testes funcionais da classe {@link RegistryEntries}.
 */
public class RegistryEntriesTest {

    @Test
    void shouldCreateWithAListOfEntries() {
        String[] entries = {};
        RegistryEntries reg = new RegistryEntries(entries);
    }

    @Test
    void shouldReturnEmptyListOfDownloaders() {
        String[] entries = {};
        RegistryEntries reg = new RegistryEntries(entries);
        List<String> downloaders = reg.getListOfDownloaders();
        Assertions.assertTrue(downloaders.isEmpty());
    }

    @Test
    void shouldReturnListOfDownloaders() {
        String[] entries = {"/googol/downloaders/downloader_1", "/someotherapp/someotherentry"};
        RegistryEntries reg = new RegistryEntries(entries);
        List<String> downloaders = reg.getListOfDownloaders();
        Assertions.assertTrue(downloaders.contains("/googol/downloaders/downloader_1"));
        Assertions.assertFalse(downloaders.contains("/someotherapp/someotherentry"));
    }

    @Test
    void shouldReturnNewCorrectNameWhenEntriesIsEmpty() {
        String[] entries = {};
        RegistryEntries reg = new RegistryEntries(entries);
        Assertions.assertEquals("downloader_1", reg.getNextDownloaderName());
    }

    @Test
    void shouldReturnNewCorrectName() {
        String[] entries = {"/googol/downloaders/downloader_2"};
        RegistryEntries reg = new RegistryEntries(entries);
        Assertions.assertEquals("downloader_3", reg.getNextDownloaderName());
    }

}
