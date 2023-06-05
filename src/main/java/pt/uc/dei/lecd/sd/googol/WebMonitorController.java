package pt.uc.dei.lecd.sd.googol;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebMonitorController {

    @GetMapping(value = "/monitor")
    public String monitor(Model model) {
        Monitor monitor = WebMonitor.getMonitor();
        model.addAttribute("downloaders", monitor.getDownloadersNames());
        model.addAttribute("barrels", monitor.getBarrelsNames());
        return "monitor";
    }

    @GetMapping(value = "/template")
	public String getString(@RequestParam(name="you", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("you", name);
		return "template";
	}
}