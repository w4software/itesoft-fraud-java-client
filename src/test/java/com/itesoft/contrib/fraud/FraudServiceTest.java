package com.itesoft.contrib.fraud;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FraudServiceTest
{

  /**
   * SET HERE YOUR SUBSCRIPTION KEY FOR ITESOFT FRAUD DETECTION SAAS
   */
  public static final String SUBSCRIPTION_KEY = "";

  /**
   * SET HERE THE PATH OF THE FILE TO TEST-CHECK
   */
  public static final String FILE_PATH = "";

  /**
   * SET HERE THE TYPE OF CHECK YOU WANT TO APPLY
   */
  public static final String CHECKTYPE = "IDENTITYCARD";

  private FraudService _fraudService;

  @Before
  public void setup()
  {
    if (SUBSCRIPTION_KEY == null || SUBSCRIPTION_KEY.isEmpty())
    {
      Assert.fail("You must set the subscription-key associated to your ITESOFT license in the java constant SUBSCRIPTION_KEY");
    }
    _fraudService = new FraudService(SUBSCRIPTION_KEY);
  }

  /**
   * This call will return all the catalog of check types
   */
  @Test
  public void testGetTypes()
  {
    System.out.println(_fraudService.getTypesAndCategories());
  }

  /**
   * This call will return all parameters that can be given to particular check type
   */
  @Test
  public void testGetMetadataDefinition()
  {
    System.out.println(_fraudService.getMetadataDefinitions(CHECKTYPE));
  }

  /**
   * This test will check the given document according to the given check type
   */
  @Test
  public void testCheckDocument()
  {
    /* allowed metadata for a check-type will be returned by getMetadataDefinitions */
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("IMAGE_RECTO", new File(FILE_PATH));
    System.out.println(_fraudService.checkDocument(CHECKTYPE, metadata));
  }
}
