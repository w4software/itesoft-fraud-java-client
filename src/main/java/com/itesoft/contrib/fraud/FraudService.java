package com.itesoft.contrib.fraud;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

public class FraudService
{

  private static final String BASE_URL = "https://itesoftfrauddev.azure-api.net";
  private static final String SUBSCRIPTION_KEY_HEADER = "Ocp-Apim-Subscription-Key";

  private String _subscriptionKey;

  public FraudService(String subscriptionKey)
  {
    _subscriptionKey = subscriptionKey;
  }

  private static byte[] readAll(InputStream inputStream) throws IOException
  {
    try
    {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[2048];
      int read = 0;
      while (read >= 0)
      {
        if (read > 0)
        {
          outputStream.write(buffer, 0, read);
        }
        read = inputStream.read(buffer);
      }
      return outputStream.toByteArray();
    }
    finally
    {
      inputStream.close();
    }
  }


  public Map<String, ObjectType> getTypesAndCategories()
  {
    try
    {
      Map<String, ObjectType> typesAndCategories = new HashMap<>();
      HttpClient client = HttpClients.createDefault();
      HttpUriRequest request = new HttpGet(BASE_URL + "/directory/documentTypesAndCategories");
      request.addHeader(SUBSCRIPTION_KEY_HEADER, _subscriptionKey);
      HttpResponse response = client.execute(request);
      JSONObject result;
      try(InputStream responseStream = response.getEntity().getContent())
      {
        result = new JSONObject(new String(readAll(responseStream)));
      }
      JSONArray data = result.getJSONArray("data");
      for(Object item : data)
      {
        JSONObject jsonItem = (JSONObject) item;
        String value = jsonItem.getString("value");
        String objectType = jsonItem.getString("objectType");
        typesAndCategories.put(value,
                               ObjectType.valueOf(objectType));
      }
      return typesAndCategories;
    }
    catch (final IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  public Map<String, MetadataDefinition> getMetadataDefinitions(String checkAlgorithm)
  {
    try
    {
      Map<String, MetadataDefinition> metadataDefinitions = new HashMap<>();
      HttpClient client = HttpClients.createDefault();
      HttpUriRequest request = new HttpGet(BASE_URL + "/directory/metadataDefinitions/" + checkAlgorithm);
      request.addHeader(SUBSCRIPTION_KEY_HEADER, _subscriptionKey);
      HttpResponse response = client.execute(request);
      JSONObject result;
      try(InputStream responseStream = response.getEntity().getContent())
      {
        result = new JSONObject(new String(readAll(responseStream)));
      }
      JSONArray data = result.getJSONArray("data");
      for(Object item : data)
      {
        MetadataDefinition metadataDefinition = new MetadataDefinition();
        JSONObject jsonItem = (JSONObject) item;
        String key = jsonItem.getString("key");
        metadataDefinition.setKey(key);
        metadataDefinition.setRequired(jsonItem.getBoolean("required"));
        metadataDefinition.setType(jsonItem.getString("metadataType"));
        metadataDefinitions.put(key, metadataDefinition);
      }
      return metadataDefinitions;
    }
    catch (final IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  public FraudResult checkDocument(String checkAlgorithm, Map<String, Object> metadata)
  {
    try
    {
      HttpClient client = HttpClients.createDefault();

      HttpPost httpPost = new HttpPost(BASE_URL + "/checkdocument/" + checkAlgorithm.toUpperCase());
      httpPost.addHeader(SUBSCRIPTION_KEY_HEADER, _subscriptionKey);

      Map<String, Object> fixedMetadata = new HashMap<>(metadata);
      Map<String, File> additionalParts = new HashMap<>();

      int additionalIndex = 0;
      for(Entry<String, Object> entry : fixedMetadata.entrySet())
      {
        if (entry.getValue() instanceof File)
        {
          File file = (File) entry.getValue();
          String name = "file" + additionalIndex++;
          entry.setValue(name);
          additionalParts.put(name, file);
        }
      }

      Map<String, Object> parameters = new HashMap<>();
      parameters.put("metadata", fixedMetadata);
      MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
      entityBuilder.addPart("Parameters", new StringBody(new JSONObject(parameters).toString(), ContentType.APPLICATION_JSON));

      for(Entry<String, File> additionalPart : additionalParts.entrySet())
      {
        entityBuilder.addPart(additionalPart.getKey(), new FileBody(additionalPart.getValue(), ContentType.create("image/jpeg")));
      }

      httpPost.setEntity(entityBuilder.build());

      HttpResponse response = client.execute(httpPost);
      JSONObject result;
      try(InputStream responseStream = response.getEntity().getContent())
      {
        result = new JSONObject(new String(readAll(responseStream)));
      }

      final FraudResult fraudResult = new FraudResult();
      fraudResult.setValid(result.getBoolean("result"));
      fraudResult.setStatus("SUCCESS".equals(result.getString("status").toUpperCase()));
      fraudResult.setStatusText(result.getString("status").toUpperCase());

      final JSONObject details = result.getJSONObject("details");
      if (details != null)
      {
        for (final String detailKey : details.keySet())
        {
          final JSONObject detail = details.getJSONObject(detailKey);
          final FraudDetail fraudDetail = new FraudDetail();
          fraudDetail.setStatus("SUCCESS".equals(detail.getString("status").toUpperCase()));
          fraudDetail.setStatusText(detail.getString("status").toUpperCase());
          fraudDetail.setDescription(detail.getString("description"));
          fraudDetail.setName(detailKey.toUpperCase());
          fraudResult.getDetails().add(fraudDetail);
        }
      }
      return fraudResult;
    }
    catch (final IOException e)
    {
      throw new RuntimeException(e);
    }
  }
}
