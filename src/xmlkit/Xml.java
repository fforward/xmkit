package xmlkit;

import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import xmlkit.tiny.TTXmlFactory;

public class Xml {
  
  private static ThreadLocal<XMLReader> tXmlReader = new ThreadLocal<XMLReader>() {
    @Override
    protected XMLReader initialValue() {
      try {
        
        XMLReader reader = XMLReaderFactory.createXMLReader();
        return reader;
        
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  };
  
  public static enum DocumentType {
    TINY_TREE
  }
  
  public static XMLReader createXmlReader() {
    return tXmlReader.get();
  }
  
  public static TTXmlFactory getTTXmlFactory() {
    return new TTXmlFactory();
  }
  
  public static XmlDocument createDocument(InputSource source, DocumentType type) 
        throws IOException {
    
    return getTTXmlFactory().createDocument(source);
  }
}
