package com.itesoft.contrib.fraud;

import java.util.ArrayList;
import java.util.List;

public class FraudResult
{

  private boolean _validity;
  private boolean _status;
  private String _statusText;

  private List<FraudDetail> _details = new ArrayList<>();

  public boolean isValid()
  {
    return _validity;
  }

  public void setValid(boolean validity)
  {
    _validity = validity;
  }

  public boolean getStatus()
  {
    return _status;
  }

  public void setStatus(boolean status)
  {
    _status = status;
  }

  public List<FraudDetail> getDetails()
  {
    return _details;
  }

  public void setDetails(List<FraudDetail> details)
  {
    _details = details;
  }

  public String getStatusText()
  {
    return _statusText;
  }

  public void setStatusText(String statusText)
  {
    _statusText = statusText;
  }

  @Override
  public String toString()
  {
    return "validity => " + _validity + ", status => " + _status + " [" + _statusText + "]" + ", details => " + _details;
  }
}
