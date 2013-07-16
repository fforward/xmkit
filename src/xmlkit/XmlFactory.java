package xmlkit;

import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public interface XmlFactory {
  
  public XmlDocument createDocument(InputSource source) throws IOException;
  
}
