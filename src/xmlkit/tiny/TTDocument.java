package xmlkit.tiny;

import net.sf.saxon.om.Axis;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.tinytree.TinyDocumentImpl;
import net.sf.saxon.tinytree.TinyElementImpl;
import xmlkit.XmlDocument;

public class TTDocument extends 
  TTElement implements XmlDocument {
  
  public TTDocument(TinyDocumentImpl impl) {
    super((TinyElementImpl)impl.getRoot().iterateAxis(Axis.CHILD, NodeKindTest.ELEMENT).next());
  }
}
