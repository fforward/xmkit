package xmlkit.tiny;

import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.tinytree.TinyDocumentImpl;
import xmlkit.XmlDocument;
import xmlkit.XmlElement;
import xmlkit.XmlNode;

public class TTNode implements XmlNode {
  
  protected final NodeInfo nodeInfo;
  
  public TTNode(NodeInfo nodeInfo) {
    this.nodeInfo = nodeInfo;
  }
  
  public NodeInfo getNodeInfo() {
    return nodeInfo;
  }

  @Override
  public boolean isSame(XmlNode node) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public XmlDocument getDocument() {
    
    DocumentInfo dInfo = getNodeInfo().getDocumentRoot();
    return TTNode.wrap(dInfo, XmlDocument.class);
  }
  
  @SuppressWarnings("unchecked")
  public static <E extends XmlNode> E wrap(NodeInfo nodeInfo, Class<E> type) {
    
    if(type == XmlDocument.class) {
      return (E)(new TTDocument((TinyDocumentImpl)nodeInfo.getDocumentRoot()));
    }
    
    return null;
  }

  @Override
  public XmlElement getRootElement() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public XmlNode getNext() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public XmlNode getPrevious() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getText() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setText(String text) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean queryBool(String path) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public String queryText(String path) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Number queryNumber(String path) {
    // TODO Auto-generated method stub
    return null;
  }
}
