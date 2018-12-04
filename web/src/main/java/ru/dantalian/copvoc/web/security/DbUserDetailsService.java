package ru.dantalian.copvoc.web.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistPrincipalManager;
import ru.dantalian.copvoc.persist.api.model.personal.Principal;

public class DbUserDetailsService implements UserDetailsService {

	private PersistPrincipalManager principalManager;

	public DbUserDetailsService(final PersistPrincipalManager aPrincipalManager) {
		principalManager = aPrincipalManager;
	}

	@Override
	public UserDetails loadUserByUsername(final String aUsername) throws UsernameNotFoundException {
		try {
			final Principal principal = principalManager.getPrincipalByName(aUsername);
			if (principal == null) {
				throw new UsernameNotFoundException("No user found with a given name");
			}
			final String encryptedPassword = principalManager.getPasswordFor(aUsername);
			return User
					.builder()
					.username(principal.getName())
          .password(encryptedPassword)
          .roles("USER")
          .build();
		} catch (final PersistException e) {
			throw new UsernameNotFoundException("Failed to get user", e);
		}
	}

}
