package xmlkit.tiny;

import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.tinytree.TinyElementImpl;
import xmlkit.XmlElement;

public class TTElement extends TTNode implements XmlElement {
  
  public TTElement(TinyElementImpl impl) {
    super(impl);
  }
  
  protected TTElement(NodeInfo nodeInfo) {
    super(nodeInfo);
  }
  
  @Override
  public String getAttributeText(String key) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setAttributeText(String key, String text) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public String getQualifiedName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getLocalName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getNamespaceUri() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getNamespacePrefix() {
    // TODO Auto-generated method stub
    return null;
  }

}
