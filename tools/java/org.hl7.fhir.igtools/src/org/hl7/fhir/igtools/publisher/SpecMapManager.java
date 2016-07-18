package org.hl7.fhir.igtools.publisher;

import java.io.IOException;
import java.util.Calendar;

import org.hl7.fhir.utilities.TextFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class SpecMapManager {

  private JsonObject spec;
  private JsonObject paths;

  public SpecMapManager(String version, String svnRevision, Calendar genDate) {
    spec = new JsonObject();
    spec.addProperty("version", version);
    spec.addProperty("build", svnRevision);
    spec.addProperty("date", genDate.toString());
    paths = new JsonObject();
    spec.add("paths", paths);
  }

  public SpecMapManager(byte[] bytes) throws JsonSyntaxException, IOException {
    spec = (JsonObject) new com.google.gson.JsonParser().parse(TextFile.bytesToString(bytes));
    paths = spec.getAsJsonObject("paths");
  }

  public void path(String url, String path) {
    paths.addProperty(url, path);
  }

  public void save(String filename) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(spec);
    TextFile.stringToFile(json, filename);    
  }

  public String getVersion() throws Exception {
    return str(spec, "version");
  }

  public String getPath(String url) throws Exception {
    return str(paths, url);
  }

  private String str(JsonObject obj, String name) throws Exception {
    if (!obj.has(name))
      throw new Exception("Property "+name+" not found");
    if (!(obj.get(name) instanceof JsonPrimitive))
      throw new Exception("Property "+name+" not a primitive");
    JsonPrimitive p = (JsonPrimitive) obj.get(name);
    if (!p.isString())
      throw new Exception("Property "+name+" not a string");
    return p.getAsString();
  }

  
}