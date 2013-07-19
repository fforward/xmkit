package xmlkit.jaxp;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import xmlkit.XmlDocument;
import xmlkit.XmlElement;
import xmlkit.XmlNode;

public class JAXPDocument extends JAXPElement 
  implements XmlDocument {
  
  public JAXPDocument(org.w3c.dom.Document dom) {
    super(dom);
  }
  
  @Override
  public Document getUnderlyingNode() {
    
    Node nn = super.getUnderlyingNode();
    if(nn instanceof Document) {
      return (Document)nn;
    }
    
    return (Document)nn.getOwnerDocument();
  }

  @Override
  public XmlElement getElementById(String id) {
    return (XmlElement)JAXPNode.wrap(getUnderlyingNode().getElementById(id));
  }
  
  @Override
  public String getAttributeText(String name) {
    return getRootElement().getAttributeText(name);
  }

  @Override
  public void setAttributeText(String name, String text) {
    getRootElement().setAttributeText(name, text);
  }

  @Override
  public List<XmlNode> getChildNodes() {
    return getRootElement().getChildNodes();
  }

  @Override
  public boolean hasChildNodes() {
    return getRootElement().hasChildNodes();
  }

  @Override
  public List<XmlElement> getChildElements() {
    return getRootElement().getChildElements();
  }

  @Override
  public List<XmlNode> getAttributes() {
    return getRootElement().getAttributes();
  }

  @Override
  public void removeAttribute(String name) {
    getRootElement().removeAttribute(name);
  }

  @Override
  public XmlNode replaceChild(XmlNode old, XmlNode n) {
    return getRootElement().replaceChild(old, n);
  }

  @Override
  public void appendText(String text) {
    getRootElement().appendText(text);
  }

  @Override
  public XmlNode appendChild(XmlNode n) {
    if(n instanceof JAXPNode) {
      JAXPNode jN = (JAXPNode)n;
      XmlElement root = getRootElement();
      
      getUnderlyingNode().adoptNode(jN.getUnderlyingNode());
      
      if(root == null) {
        return wrap(getUnderlyingNode().appendChild(jN.getUnderlyingNode()));  
      }
      
      return getRootElement().appendChild(n);
    }
    
    return null;
  }

  @Override
  public XmlNode removeChild(XmlNode n) {
    return getRootElement().removeChild(n);
  }

  @Override
  public XmlElement appendElement(String tagName) {
    if(getRootElement() != null) {
      return getRootElement().appendElement(tagName);
    } else {
      Element e = getUnderlyingNode().createElement(tagName);
      return (XmlElement)JAXPNode.wrap(this.getUnderlyingNode().appendChild(e));
    }
  }
}
