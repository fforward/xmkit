package xmlkit;

import java.io.IOException;

import org.xml.sax.InputSource;

public interface XmlFactory {
  
  public XmlDocument createDocument(InputSource source) throws IOException;
  
  public XmlDocument createDocument();

}
