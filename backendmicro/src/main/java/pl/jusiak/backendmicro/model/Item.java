package pl.jusiak.backendmicro.model;

import io.vertx.core.json.JsonObject;

import java.util.UUID;

public class Item {

  private UUID id;
  private UUID owner;
  private String name;

  public Item() {
  }

  public Item(UUID id, UUID owner, String name) {
    this.id = id;
    this.owner = owner;
    this.name = name;
  }

  public Item(JsonObject jsonObject) {
    if (jsonObject.containsKey("_id"))
      this.id = UUID.fromString(jsonObject.getString("_id"));
    if (jsonObject.containsKey("owner"))
      this.owner = UUID.fromString(jsonObject.getString("owner"));
    this.name = jsonObject.getString("name");
  }

  public JsonObject toNameJson() {
    JsonObject item = new JsonObject()
      .put("name", this.name);

    JsonObject retVal = new JsonObject()
      .put("item", item);

    return retVal;
  }

  public JsonObject toMongoItemJson() {
    JsonObject retVal = new JsonObject();
    retVal.put("_id", (this.id).toString())
      .put("owner", (this.owner).toString())
      .put("name", this.name);
    return retVal;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getOwner() {
    return owner;
  }

  public void setOwner(UUID owner) {
    this.owner = owner;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
