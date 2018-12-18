package ru.dantalian.copvoc.web.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping(value = {"/", "/main"})
	String index(final Principal aPrincipal, final Model aModel) {
		if (aPrincipal == null) {
			return "redirect:/login";
		} else {
			aModel.addAttribute("tpl", "main");
			aModel.addAttribute("top_menu", true);
			return "frame";
		}
	}

	@RequestMapping("/login")
	String login(final Model aModel) {
		aModel.addAttribute("tpl", "login");
		aModel.addAttribute("top_menu", null);
		aModel.addAttribute("title", "Login");
		return "frame";
	}

}
