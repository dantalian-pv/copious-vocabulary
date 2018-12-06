package ru.dantalian.copvoc.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping("/")
  String index(){
      return "index";
  }

	@RequestMapping("/page/{page}")
  String login(@PathVariable("page") final String aPage){
      return aPage;
  }

}
