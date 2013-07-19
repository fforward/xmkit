package xmlkit.saxon;

import net.sf.saxon.om.Axis;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.tinytree.TinyDocumentImpl;
import net.sf.saxon.tinytree.TinyElementImpl;
import xmlkit.XmlDocument;
import xmlkit.XmlElement;

public class TTDocument extends 
  TTElement implements XmlDocument {
  
  public TTDocument(TinyDocumentImpl impl) {
    super((TinyElementImpl)impl.iterateAxis(Axis.CHILD, NodeKindTest.ELEMENT).next());
  }
  
  @Override
  public NodeInfo getNodeInfo() {
    return super.getNodeInfo().getDocumentRoot();
  }

  @Override
  public XmlElement getElementById(String id) {
    
    DocumentInfo di = getNodeInfo().getDocumentRoot();
    if(di==null) return null;
    
    return (XmlElement)di.selectID(id, false);
  }
}
