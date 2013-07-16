package xmlkit;

public interface XmlElement extends XmlNode {
  
  public String getAttributeText(String key);
  
  public void setAttributeText(String key, String text);
  
  public String getQualifiedName();
  
  public String getLocalName();
  
  public String getNamespaceUri();
  
  public String getNamespacePrefix();
  
}
