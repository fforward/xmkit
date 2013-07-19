package xmlkit.saxon;

import static net.sf.saxon.om.Axis.ATTRIBUTE;
import static net.sf.saxon.om.Axis.CHILD;

import java.util.ArrayList;
import java.util.List;

import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.tinytree.TinyElementImpl;
import net.sf.saxon.trans.XPathException;
import xmlkit.XmlElement;
import xmlkit.XmlNode;

public class TTElement extends TTNode 
  implements XmlElement {



  public TTElement(TinyElementImpl impl) {
    super(impl);
  }

  protected TTElement(NodeInfo nodeInfo) {
    super(nodeInfo);
  }
  
  @Override
  public String getAttributeText(String name) {
    AxisIterator atts = super.getNodeInfo().iterateAxis(ATTRIBUTE);
    while (true) {
      NodeInfo att = (NodeInfo)atts.next();
      if (att == null) {
          return "";
      }
      
      if (att.getDisplayName().equals(name)) {
        String val = att.getStringValue();
        if (val==null) return "";
        return val;
      }
    }
  }

  @Override
  public void setAttributeText(String key, String text) {
    throwUpdateError();
  }

  @Override
  public List<XmlElement> getChildElements() {
    SequenceIterator iter = super.getNodeInfo().iterateAxis(CHILD, NodeKindTest.ELEMENT);
    return adaptSequenceIterator(iter, XmlElement.class);
  }

  @Override
  public List<XmlNode> getChildNodes() {
    SequenceIterator iter = super.getNodeInfo().iterateAxis(CHILD);
    return adaptSequenceIterator(iter, XmlNode.class);
  }
  
  private <E extends XmlNode> List<E> 
    adaptSequenceIterator(SequenceIterator it, Class<E> type) {
    try {
      List<E> nodes = new ArrayList<E>(10);
      while (true) {
        NodeInfo node = (NodeInfo) it.next();
        if (node == null) {
          break;
        }
        
        nodes.add((E)TTNode.wrap(node));
      }
      
      return nodes;
      
    } catch (net.sf.saxon.trans.XPathException err) {
      return null;
    }
  }

  @Override
  public boolean hasChildNodes() {
    try {
    SequenceIterator iter = super.getNodeInfo().iterateAxis(CHILD);
      return iter.next()!=null;
    } catch (XPathException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<XmlNode> getAttributes() {
    SequenceIterator iter = super.getNodeInfo().iterateAxis(Axis.CHILD, NodeKindTest.ATTRIBUTE);
    return adaptSequenceIterator(iter, XmlNode.class);
  }

  @Override
  public void removeAttribute(String name) {
    throwUpdateError();
  }

  @Override
  public XmlNode replaceChild(XmlNode old, XmlNode n) {
    throwUpdateError();
    return null;
  }

  @Override
  public void appendText(String text) {
    throwUpdateError();
  }

  @Override
  public XmlNode appendChild(XmlNode n) {
    throwUpdateError();
    return null;
  }

  @Override
  public XmlNode removeChild(XmlNode n) {
    throwUpdateError();
    return null;
  }

  @Override
  public XmlElement appendElement(String name) {
    throwUpdateError();
    return null;
    
  }
}
