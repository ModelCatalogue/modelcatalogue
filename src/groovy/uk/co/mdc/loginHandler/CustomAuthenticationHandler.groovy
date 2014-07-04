package uk.co.mdc.loginHandler

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Created by soheil on 20/06/2014.
  CustomAuthenticationHandler class will manage users welcome page
  if a user has role of ROLE_READONLY_USER, he/she will be lead to /metadataCurator otherwise will be lead to /dashboard
 */
public class CustomAuthenticationHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
		String otherUsersTargetUrl    = "/dashboard/"
		String readonlyUserTargetUrl  = "/metadataCurator"

		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

		if (roles.contains("ROLE_READONLY_USER")) {
			getRedirectStrategy().sendRedirect(request, response, readonlyUserTargetUrl);
		} else {
			getRedirectStrategy().sendRedirect(request, response, otherUsersTargetUrl);
		}
	}
}