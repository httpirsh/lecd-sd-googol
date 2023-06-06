package pt.uc.dei.lecd.sd.googol;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebServerController {

    @GetMapping(value = "/monitor")
    public String monitor(Model model) {
        Monitor monitor = WebServer.getMonitor();
        model.addAttribute("downloaders", monitor.getDownloadersNames());
        model.addAttribute("barrels", monitor.getBarrelsNames());
        model.addAttribute("topsearches", monitor.getTopSearches());

        return "monitor";
    }

    @GetMapping(value = "/search")
	public String getString(@RequestParam(name="query", required=false, defaultValue="") String query, Model model) throws MalformedURLException, RemoteException, NotBoundException {

        InterfaceSearchModule search = WebServer.getRegistry().lookupSearch();
        List<Page> results = search.search(query);

		model.addAttribute("query", query);
        model.addAttribute("results", results);

		return "searchResults";
	}

    @PostMapping("/index")
    public String indexPage(@RequestParam("url") String url) throws RemoteException, MalformedURLException, NotBoundException {
        InterfaceSearchModule search = WebServer.getRegistry().lookupSearch();
        search.indexNewURL(url);
        return "redirect:/index.html";
    }
}