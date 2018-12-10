package ru.dantalian.copvoc.web.controllers;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	private final Set<String> whiteList = new HashSet<>(Arrays.asList("login", "main"));

	@RequestMapping("/")
	String index(final Principal aPrincipal, final Model aModel) {
		if (aPrincipal == null) {
			return "redirect:/page/login";
		} else {
			aModel.addAttribute("tpl", "main");
			return "frame";
		}
	}

	@RequestMapping("/page/{page}")
	String login(@PathVariable("page") final String aPage, final Model aModel) {
		if (!whiteList.contains(aPage)) {
			throw new PageNotFoundException();
		}
		aModel.addAttribute("tpl", aPage);
		return "frame";
	}

}
