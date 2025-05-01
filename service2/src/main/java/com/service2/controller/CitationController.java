package com.service2.controller;

import com.service2.model.Citation;
import com.service2.service.CitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CitationController {
    @Autowired
    private CitationService citationService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("citation", citationService.getRandomCitation());
        return "citation";
    }

    @GetMapping("/api/citation")
    @ResponseBody
    public Citation getRandomCitation() {
        return citationService.getRandomCitation();
    }
}
