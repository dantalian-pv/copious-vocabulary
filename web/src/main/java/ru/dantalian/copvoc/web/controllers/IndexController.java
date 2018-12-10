package ru.dantalian.copvoc.web.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping("/")
  String index(final Principal principal) {
		if (principal == null) {
			return "redirect:/page/login";
		} else {
      return "index";
		}
  }

	@RequestMapping("/page/{page}")
  String login(@PathVariable("page") final String aPage) {
      return aPage;
  }

}
