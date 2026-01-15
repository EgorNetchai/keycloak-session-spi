package ru.aston.spi.session.provider;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;
import ru.aston.spi.session.CustomSessionResource;

public class CustomSessionResourceProvider implements RealmResourceProvider {

  private final KeycloakSession session;

  public CustomSessionResourceProvider(KeycloakSession session) {
    this.session = session;
  }

  @Override
  public Object getResource() {
    return new CustomSessionResource(session);
  }

  @Override
  public void close() {
    //default implementation
  }

}
