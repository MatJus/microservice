package pl.jusiak.backendmicro.model;

import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class User {

  private UUID id;
  private String login;
  private String password;

  public User() {
  }

  public User(String login, String password) {
    this.login = login;
    this.password = password;
  }

  public User(UUID id, String login, String password) {
    this.id = id;
    this.login = login;
    this.password = password;
  }

  public User(JsonObject jsonObject) {
    if (jsonObject.containsKey("_id"))
      this.id = UUID.fromString(jsonObject.getString("_id"));
    this.login = jsonObject.getString("login");
    this.password = jsonObject.getString("password");
  }

  public JsonObject toIdAndLoginJson() {
    JsonObject user = new JsonObject()
      .put("_id", this.id.toString())
      .put("login", this.login);

    JsonObject retVal = new JsonObject()
      .put("user", user);

    return retVal;
  }

  public JsonObject toMongoUserJson() {
    JsonObject retVal = new JsonObject();
    retVal.put("_id", (this.id).toString())
      .put("login", this.login)
      .put("password", this.password);
    return retVal;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
