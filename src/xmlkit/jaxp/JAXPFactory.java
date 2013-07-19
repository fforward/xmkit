package xmlkit.jaxp;

import java.io.IOException;

import org.xml.sax.InputSource;

import xmlkit.Xml;
import xmlkit.XmlDocument;
import xmlkit.XmlFactory;

public class JAXPFactory implements XmlFactory {

  @Override
  public XmlDocument createDocument(InputSource source) throws IOException {
    org.w3c.dom.Document dom = Xml.createW3CDocument(source);
    return (XmlDocument)JAXPNode.wrap(dom);
  }

  @Override
  public XmlDocument createDocument() {
    org.w3c.dom.Document dom =  Xml.createW3CDocument();
    return (XmlDocument)JAXPNode.wrap(dom);
  }
}
