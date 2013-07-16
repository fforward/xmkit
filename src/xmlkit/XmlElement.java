package xmlkit;

import java.util.List;

public interface XmlElement extends XmlNode {
  
  public String getAttributeText(String key);
  
  public void setAttributeText(String key, String text);
  
  public String getQualifiedName();
  
  public String getLocalName();
  
  public List<XmlNode> getChildNodes();
  
  public List<XmlElement> getChildElements();
}
