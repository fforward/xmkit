package xmlkit;

import java.util.List;

public interface XmlNode {
  
  public boolean isSame(XmlNode node);
  
  public XmlDocument getDocument();
  
  public XmlElement getRootElement();
  
  public XmlNode getNext();
  
  public XmlNode getPrevious();
  
  public String getText();
  
  public void setText(String text);
  
  public List<XmlNode> query(String xPath);
  
  public boolean queryBool(String xPath);
  
  public String queryText(String xPath);
  
  public Number queryNumber(String xPath);
  
  public String getNamespaceUri();
  
  public String getNamespacePrefix();
}
