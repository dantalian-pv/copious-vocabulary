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
			return page("main", aModel);
		}
	}

	@RequestMapping("/page/{page}")
	String page(@PathVariable("page") final String aPage, final Model aModel) {
		if (!whiteList.contains(aPage)) {
			throw new PageNotFoundException();
		}
		aModel.addAttribute("tpl", aPage);
		aModel.addAttribute("top_menu", "login".equals(aPage) ? null : true);
		aModel.addAttribute("title", "login".equals(aPage) ? "Login" : null);
		return "frame";
	}

}
