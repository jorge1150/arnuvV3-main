package com.core.arnuv.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.core.arnuv.service.IPersonaDetalleService;

@Controller
public class IndexController {
	@Autowired
	IPersonaDetalleService personaDetalleService;

	@GetMapping({ "/", "/index" })
	public String index(@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails != null) {
			return "redirect:/auth/default";
		}
		return "landing/index";
	}

	@GetMapping("/home")
	public String home() {
		return "content-page/blank";
	}
}