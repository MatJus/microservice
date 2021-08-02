package pl.jusiak.backendmicro.verticles.dav;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.mongo.MongoAuthentication;
import io.vertx.ext.auth.mongo.MongoAuthenticationOptions;
import io.vertx.ext.mongo.MongoClient;
import pl.jusiak.backendmicro.model.User;

import java.security.SecureRandom;
import java.util.UUID;

public class UserDAV extends AbstractVerticle {

  private MongoClient mongoClient;
  private MongoAuthentication mongoAuthenticationProvider;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    mongoClient = MongoClient.createShared(vertx, new JsonObject()
      .put("db_name", config()
        .getString("db_name", "microservice"))
      .put("connection_string", config()
        .getString("connection_string", "mongodb://localhost:27017")));

    mongoAuthenticationProvider = MongoAuthentication.create(mongoClient, new MongoAuthenticationOptions()
      .setUsernameField("login")
      .setUsernameCredentialField("login")
      .setCollectionName("users"));

    EventBus eventBus = vertx.eventBus();
    MessageConsumer<JsonObject> consumer = eventBus.consumer("user-actions");

    consumer.handler(message -> {

      String action = message.body().getString("action");

      switch (action) {
        case "login-user":
          loginUser(message);
          break;
        case "register-user":
          registerUser(message);
          break;
        default:
          message.fail(1, "Unknown action: " + message.body());
      }
    });

    startPromise.complete();
  }

  private void loginUser(Message<JsonObject> message) {

    JsonObject authInfo = message.body().getJsonObject("authinfo");

    //System.out.println(authInfo);

    mongoAuthenticationProvider.authenticate(authInfo, ar -> {
      if (ar.succeeded()) {
        searchForUserByCriteria("login", authInfo.getString("username"))
          .onComplete(ar2 -> {
            if (ar2.succeeded()) {
              message.reply(ar2.result().toIdAndLoginJson());
            } else {
              message.fail(2, "Search for user error: " + ar2.cause().getMessage());
            }
          });
      } else {
        message.fail(1, "Authenticate error: " + ar.cause().getMessage());
      }
    });
  }

  private Future<User> searchForUserByCriteria(String criteria, String value) {

    Promise<User> retVal = Promise.promise();

    JsonObject query = new JsonObject()
      .put(criteria, value);
    //System.out.println("Query: " + query);

    mongoClient.find("users", query, ar -> {
      if (ar.succeeded()) {
        retVal.complete(new User(ar.result().get(0)));
      } else {
        retVal.fail(ar.cause().getMessage());
      }
    });

    return retVal.future();
  }

  private void registerUser(Message<JsonObject> message) {

    final User userToRegister = new User(message.body().getJsonObject("user"));

    insertUser(userToRegister).onComplete(ar -> {
      if (ar.succeeded()) {
        message.reply(userToRegister.toIdAndLoginJson());
      } else {
        message.fail(1, "User inserting error: " + ar.cause().getMessage());
      }
    });
  }

  private Future<Void> insertUser(User user) {

    Promise<Void> retVal = Promise.promise();

    UUID id = UUID.randomUUID();
    user.setId(id);
    String salt = generateSalt();
    String hashedPassword = mongoAuthenticationProvider.hash("sha512", salt, user.getPassword());
    user.setPassword(hashedPassword);

    mongoClient.save("users", user.toMongoUserJson(), ar -> {
      if (ar.succeeded()) {
        retVal.complete();
      } else {
        retVal.fail(ar.cause().getMessage());
      }
    });

    return retVal.future();
  }

  private String generateSalt() {

    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);

    return salt.toString();
  }
}
