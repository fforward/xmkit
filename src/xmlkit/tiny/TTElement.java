package xmlkit.tiny;

import java.util.ArrayList;
import java.util.List;

import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.tinytree.TinyElementImpl;
import xmlkit.XmlElement;
import xmlkit.XmlNode;

public class TTElement extends TTNode implements XmlElement {

  public TTElement(TinyElementImpl impl) {
    super(impl);
  }

  protected TTElement(NodeInfo nodeInfo) {
    super(nodeInfo);
  }
  
  @Override
  public TinyElementImpl getNodeInfo() {
    return (TinyElementImpl)super.getNodeInfo();
  }

  @Override
  public String getAttributeText(String name) {
    AxisIterator atts = getNodeInfo().iterateAxis(Axis.ATTRIBUTE);
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
  public String getQualifiedName() {
    return getNodeInfo().getDisplayName();
  }

  @Override
  public String getLocalName() {
    return getNodeInfo().getLocalPart();
  }

  @Override
  public List<XmlElement> getChildElements() {
    List<XmlElement> nodes = new ArrayList<XmlElement>(10);
    try {
      SequenceIterator iter = getNodeInfo().iterateAxis(Axis.CHILD, 
          NodeKindTest.ELEMENT);
      
      while (true) {
        NodeInfo node = (NodeInfo) iter.next();
        if (node == null)
          break;
        nodes.add((XmlElement)TTNode.wrap(node));
      }
    } catch (net.sf.saxon.trans.XPathException err) {
      return null;
    }

    return nodes;
  }

  @Override
  public List<XmlNode> getChildNodes() {
    List<XmlNode> nodes = new ArrayList<XmlNode>(10);
    try {
      SequenceIterator iter = getNodeInfo().iterateAxis(Axis.CHILD);
      while (true) {
        NodeInfo node = (NodeInfo) iter.next();
        if (node == null)
          break;
        nodes.add(TTNode.wrap(node));
      }
    } catch (net.sf.saxon.trans.XPathException err) {
      return null;
    }

    return nodes;
  }
}
