package ru.dantalian.copvoc.web.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class AuthEntryPoint extends LoginUrlAuthenticationEntryPoint {

	public AuthEntryPoint(final String aLoginFormUrl) {
		super(aLoginFormUrl);
	}

	@Override
	public void commence(final HttpServletRequest aRequest, final HttpServletResponse aResponse,
			final AuthenticationException aAuthException) throws IOException, ServletException {
		if (aRequest.getRequestURI().startsWith("/v1/api")) {
			aResponse.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
		} else {
			super.commence(aRequest, aResponse, aAuthException);
		}
	}

}
