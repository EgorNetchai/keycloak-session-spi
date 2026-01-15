package ru.aston.spi.session.authenticator;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import ua_parser.Client;
import ua_parser.Parser;
import org.jboss.logging.Logger;

public class UserAgentAuthenticator implements Authenticator {

  private static final Logger logger = Logger.getLogger(UserAgentAuthenticator.class);

  @Override
  public void authenticate(AuthenticationFlowContext context) {
    try {
      HttpRequest request = context.getHttpRequest();
      String userAgentString = request.getHttpHeaders().getHeaderString("User-Agent");

      Parser userAgentParser = new Parser();
      Client client = userAgentParser.parse(userAgentString);
      String parsedUserAgent =
        String.format("Browser: %s, Device: %s, OS: %s", client.userAgent.family,
          client.device.family, client.os.family);

      context.getAuthenticationSession().setUserSessionNote("PARSED_USER_AGENT", parsedUserAgent);
      context.success();
    } catch (Exception e) {
      logger.error("Error in UserAgentAuthenticator during authentication", e);
      context.failure(AuthenticationFlowError.INTERNAL_ERROR);
    }
  }

  @Override
  public void action(AuthenticationFlowContext context) {
    //default implementation
  }

  @Override
  public boolean requiresUser() {
    return false;
  }

  @Override
  public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
    return true;
  }

  @Override
  public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    //default implementation
  }

  @Override
  public void close() {
    //default implementation
  }
}
