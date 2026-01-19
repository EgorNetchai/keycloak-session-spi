package ru.aston.spi.session.authenticator;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.List;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

@ApplicationScoped
public class UserAgentAuthenticatorFactory implements AuthenticatorFactory {

  public static final String PROVIDER_ID = "user-agent-authenticator";

  @Override
  public String getDisplayType() {
    return "Capture User Agent";
  }

  @Override
  public String getReferenceCategory() {
    return null;
  }

  @Override
  public boolean isConfigurable() {
    return false;
  }

  @Override
  public Requirement[] getRequirementChoices() {
    return new Requirement[] {
      Requirement.REQUIRED,
      Requirement.DISABLED,
      Requirement.ALTERNATIVE
    };
  }

  @Override
  public boolean isUserSetupAllowed() {
    return false;
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return Collections.emptyList();
  }

  @Override
  public String getHelpText() {
    return "Captures and parses User-Agent from request.";
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }

  @Override
  public Authenticator create(KeycloakSession session) {
    return new UserAgentAuthenticator();
  }


  @Override
  public void init(Config.Scope config) {
    //default implementation
  }

  @Override
  public void postInit(KeycloakSessionFactory sessionFactory) {
    //default implementation
  }

  @Override
  public void close() {
    //default implementation
  }

}
