package xmlkit;

import java.io.OutputStream;
import java.util.List;

public interface XmlNode {
  
  public boolean isSame(XmlNode node);
  
  public XmlDocument getDocument();
  
  public XmlElement getRootElement();
  
  public XmlNode getNext();
  
  public XmlNode getPrevious();
  
  public String getText();
  
  public String getName();
  
  public String getLocalName();
  
  public boolean isAttribute();
  
  public boolean isText();
  
  public void setText(String text);
  
  public XmlNode getParent();

  public List<XmlNode> query(String xPath);
  
  public boolean queryBool(String xPath);
  
  public String queryText(String xPath);
  
  public Number queryNumber(String xPath);
  
  public XmlElement queryElement(String xPath);
  
  public XmlNode queryNode(String xPath);
  
  public String getNamespaceUri();
  
  public String getNamespacePrefix();
  
  public String toXml(boolean xmlDecl);
  
  public OutputStream toXml(OutputStream os, boolean xmlDecl);
  
  public String toXml();
  
  public XmlNode copy();
  
  public String getEncoding();
}
