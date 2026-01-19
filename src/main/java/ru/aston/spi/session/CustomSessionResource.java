package ru.aston.spi.session;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.models.UserSessionModel.State;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

@Path("/")
public class CustomSessionResource {

  private final KeycloakSession session;

  public CustomSessionResource(KeycloakSession session) {
    this.session = session;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSessions(@QueryParam("userId") String userIdParam) {
    RealmModel realmModel = session.getContext().getRealm();

    AuthenticationManager.AuthResult authResult =
      new AppAuthManager.BearerTokenAuthenticator(session)
        .setRealm(realmModel)
        .setConnection(session.getContext().getConnection())
        .setHeaders(session.getContext().getRequestHeaders())
        .authenticate();

    if (authResult == null) {
      return Response.status(Status.UNAUTHORIZED).build();
    }

    AccessToken token = authResult.getToken();
    if (token == null || !token.isActive()) {
      return Response.status(Status.UNAUTHORIZED).build();
    }

    UserModel user;
    String currentSessionId =
      authResult.getSession() != null ? authResult.getSession().getId() : null;

    if (userIdParam != null && !userIdParam.isEmpty()) {
      Set<String> roles =
        token.getRealmAccess() != null ? token.getResourceAccess("realm-management").getRoles()
          : null;
      if (roles == null || (!roles.contains("manage-users") && !roles.contains("view-users"))) {
        return Response.status(Status.FORBIDDEN).build();
      }

      user = session.users().getUserById(realmModel, userIdParam);

      if (user == null) {
        return Response.status(Status.BAD_REQUEST).build();
      }
      currentSessionId = null;
    } else {
      String userId = token.getSubject();
      user = session.users().getUserById(realmModel, userId);

      if (user == null) {
        return Response.status(Status.UNAUTHORIZED).build();
      }
    }

    List<UserSessionModel> userSessions = session
      .sessions()
      .getUserSessionsStream(realmModel, user)
      .toList();

    List<Map<String, Object>> sessionsList = new ArrayList<>();
    int totalActive = 0;

    for (UserSessionModel userSession : userSessions) {
      if (userSession.getState() == State.LOGGED_IN) {
        totalActive++;
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("session_id", userSession.getId());
        sessionData.put("user_id", user.getId());
        sessionData.put("ip", userSession.getIpAddress());
        sessionData.put("login_time", Instant.ofEpochSecond(userSession.getStarted()).toString());
        sessionData.put("last_activity",
          Instant.ofEpochSecond(userSession.getLastSessionRefresh()).toString());
        sessionData.put("user_agent", userSession.getNote("PARSED_USER_AGENT"));
        sessionsList.add(sessionData);
      }
    }

    Map<String, Object> responseJson = new HashMap<>();
    responseJson.put("sessions", sessionsList);
    responseJson.put("total_active", totalActive);
    responseJson.put("current_session_id", currentSessionId);

    return Response.ok(responseJson).build();
  }
}
