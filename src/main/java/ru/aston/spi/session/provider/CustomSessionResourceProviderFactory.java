package ru.aston.spi.session.provider;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class CustomSessionResourceProviderFactory implements RealmResourceProviderFactory {

  public static final String ID = "custom-session-provider";

  @Override
  public RealmResourceProvider create (KeycloakSession session) {
    return new CustomSessionResourceProvider(session);
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

  @Override
  public String getId() {
    return ID;
  }

}
