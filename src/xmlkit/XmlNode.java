package xmlkit;

public interface XmlNode {
  
  public boolean isSame(XmlNode node);
  
  public XmlDocument getDocument();
  
  public XmlElement getRootElement();
  
  public XmlNode getNext();
  
  public XmlNode getPrevious();
  
  public String getText();
  
  public void setText(String text);
  
  public boolean queryBool(String path);
  
  public String queryText(String path);
  
  public Number queryNumber(String path);
  
}
