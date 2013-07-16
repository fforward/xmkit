package xmlkit.tiny;

import java.io.IOException;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.ReceivingContentHandler;
import net.sf.saxon.tinytree.TinyBuilder;
import net.sf.saxon.tinytree.TinyDocumentImpl;
import net.sf.saxon.tinytree.TinyTree;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import xmlkit.Xml;
import xmlkit.XmlDocument;
import xmlkit.XmlFactory;

public class TTXmlFactory implements XmlFactory {

  private static final Configuration globalConfiguration;

  static {
    globalConfiguration = new Configuration();
  }

  public TinyTree createTinyTree(InputSource source) throws IOException {
    try {
      XMLReader reader = Xml.createXmlReader();
      TinyBuilder tb = new TinyBuilder();

      ReceivingContentHandler rch = getReceivingContentHandler();
      rch.setReceiver(tb);

      Configuration cfg = TTXmlFactory.getGlobalConfiguration();
      rch.setPipelineConfiguration(cfg.makePipelineConfiguration());

      reader.setContentHandler(rch);

      reader.parse(source);

      return tb.getTree();
    } catch (SAXException ex) {
      throw new IOException(ex);
    }
  }

  public TinyDocumentImpl createTinyDocumentImpl(InputSource source)
      throws IOException {
    return new TinyDocumentImpl(createTinyTree(source));
  }

  @Override
  public XmlDocument createDocument(InputSource source) throws IOException {
    TinyDocumentImpl tt = createTinyDocumentImpl(source);
    return TTNode.wrap(tt, XmlDocument.class);
  }

  public static Configuration getGlobalConfiguration() {
    return globalConfiguration;
  }

  private ReceivingContentHandler getReceivingContentHandler() {

    ReceivingContentHandler r = new ReceivingContentHandler();
    return r;
  }
}
