package xmlkit.tiny;

import net.sf.saxon.tinytree.TinyDocumentImpl;
import xmlkit.XmlDocument;

public class TTDocument extends 
  TTElement implements XmlDocument {
  
  public TTDocument(TinyDocumentImpl impl) {
    super(impl);
  }
}
