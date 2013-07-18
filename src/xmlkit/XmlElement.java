package xmlkit;

import java.util.List;

public interface XmlElement extends XmlNode {
  
  public void removeAttribute(String name);
  
  public String getAttributeText(String name);
  
  public void setAttributeText(String name, String text);
  
  public List<XmlNode> getChildNodes();
  
  public boolean hasChildNodes();
  
  public List<XmlElement> getChildElements();
  
  public List<XmlNode> getAttributes();
  
  public XmlNode replaceChild(XmlNode old, XmlNode n);
  
  public void appendText(String text);
  
  public XmlElement appendElement(String name);
  
  public XmlNode appendChild(XmlNode n);
  
  public XmlNode removeChild(XmlNode n);
}
