package pt.uc.dei.lecd.sd.googol;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationController {
    
    public ApplicationController(RmiClient rmiClient) {
    }

    @GetMapping("/")
    public String index(Model model) {
        // Add necessary data to the model
        model.addAttribute("Googol", "Search Module");
        return "index";
    }

    // Handle other HTTP requests and interact with the RMI client as needed
}
