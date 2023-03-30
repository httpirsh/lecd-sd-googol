package pt.uc.dei.lecd.sd.googol;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdminInfoClientPluginTest {
    @Test
    void Should_get_empty_text_When_no_data_available(){
        AdminInfoClientPlugin victim = new AdminInfoClientPlugin();

        Assertions.assertEquals("", victim.getAdminInfoText());
    }

    void Should_get_downloaders_When_existing(){
        Downloader downloader = new Downloader();



    }


}
