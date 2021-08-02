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
import pl.jusiak.backendmicro.model.Item;

import java.util.List;
import java.util.UUID;

public class ItemDAV extends AbstractVerticle {

  private MongoClient mongoClient;
  private MongoAuthentication mongoAuthenticationProvider;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    mongoClient = MongoClient.createShared(vertx,
      new JsonObject()
        .put("db_name", config()
          .getString("db_name", "microservice"))
        .put("connection_string", config()
          .getString("connection_string", "mongodb://localhost:27017")));

    mongoAuthenticationProvider = MongoAuthentication.create(mongoClient,
      new MongoAuthenticationOptions()
        .setUsernameField("login")
        .setUsernameCredentialField("login")
        .setCollectionName("users"));

    EventBus eventBus = vertx.eventBus();
    MessageConsumer<JsonObject> consumer = eventBus.consumer("items-actions");

    consumer.handler(message -> {

      String action = message.body().getString("action");

      switch (action) {
        case "add-item":
          addItem(message);
          break;
        case "get-items":
          getItems(message);
          break;
        default:
          message.fail(1, "Unknown action: " + message.body());
      }
    });

    startPromise.complete();
  }

  private void getItems(Message<JsonObject> message) {

    String ownerUUID = message.body().getString("owner");

    searchForItemsByCriteria("owner", ownerUUID).onComplete(ar -> {
      if (ar.succeeded()) {
        message.reply(new JsonObject().put("items", ar.result()));
      } else {
        message.fail(1, "Getting items error: " + ar.cause().getMessage());
      }
    });
  }

  private Future<List<JsonObject>> searchForItemsByCriteria(String criteria, String value) {

    Promise<List<JsonObject>> retVal = Promise.promise();

    JsonObject query = new JsonObject()
      .put(criteria, value);
    //System.out.println("Query: " + query);

    mongoClient.find("items", query, ar -> {
      if (ar.succeeded()) {
        retVal.complete(ar.result());
      } else {
        retVal.fail(ar.cause().getMessage());
      }
    });

    return retVal.future();
  }

  private void addItem(Message<JsonObject> message) {

    JsonObject itemToAddJson = new JsonObject()
      .put("owner", message.body().getString("owner"))
      .put("name", message.body().getJsonObject("item").getString("name"));

    final Item itemToAdd = new Item(itemToAddJson);

    insertItem(itemToAdd).onComplete(ar -> {
      if (ar.succeeded()) {
        message.reply(itemToAdd.toMongoItemJson());
      } else {
        message.fail(1, "Item inserting error: " + ar.cause().getMessage());
      }
    });
  }

  private Future<Void> insertItem(Item item) {

    Promise<Void> retVal = Promise.promise();

    UUID id = UUID.randomUUID();
    item.setId(id);

    mongoClient.save("items", item.toMongoItemJson(), ar -> {
      if (ar.succeeded()) {
        retVal.complete();
      } else {
        retVal.fail(ar.cause().getMessage());
      }
    });

    return retVal.future();
  }
}
