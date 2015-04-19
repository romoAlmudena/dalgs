package es.ucm.fdi.dalgs.user.service;

import es.ucm.fdi.dalgs.domain.User;
import es.ucm.fdi.dalgs.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A custom {@link UserDetailsService} where user information is retrieved from
 * a JPA repository
 */
@Service(value = "customUserDetailsService")
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Returns a populated {@link UserDetails} object. The username is first
	 * retrieved from the database and then mapped to a {@link UserDetails}
	 * object.
	 */
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

		try {

			User domainUser = userRepository.findByUsername(username);

			if (domainUser == null) {
				domainUser = userRepository.findByEmail(username);

			}

			if (domainUser == null) {
				throw new UsernameNotFoundException(String.format(
						"User %s not found", username));
			}

			return domainUser;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}