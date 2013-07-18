package xmlkit.jaxp;

import org.w3c.dom.Document;

import xmlkit.XmlDocument;
import xmlkit.XmlElement;

public class JAXPDocument extends JAXPElement 
  implements XmlDocument {
  
  public JAXPDocument(org.w3c.dom.Document dom) {
    super(dom.getDocumentElement());
  }
  
  @Override
  public Document getUnderlyingNode() {
    return (Document)super.getUnderlyingNode().getOwnerDocument();
  }

  @Override
  public XmlElement getElementById(String id) {
    return (XmlElement)JAXPNode.wrap(getUnderlyingNode().getOwnerDocument().getElementById(id));
  }
}
