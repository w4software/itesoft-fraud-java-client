package com.itesoft.contrib.fraud;

public class MetadataDefinition
{
  private String _key;
  private boolean _required;
  private String _type;

  public MetadataDefinition()
  {
  }

  public String getKey()
  {
    return _key;
  }

  public String getType()
  {
    return _type;
  }

  public boolean isRequired()
  {
    return _required;
  }

  public void setKey(String key)
  {
    _key = key;
  }

  public void setRequired(boolean required)
  {
    _required = required;
  }

  public void setType(String type)
  {
    this._type = type;
  }

  @Override
  public String toString()
  {
    return _key + "/" + _type + "/" + _required;
  }
}
