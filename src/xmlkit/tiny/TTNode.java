package xmlkit.tiny;

import java.util.AbstractList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import net.sf.saxon.om.Axis;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.NamespaceConstant;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.tinytree.TinyDocumentImpl;
import net.sf.saxon.tinytree.TinyElementImpl;
import net.sf.saxon.type.Type;
import xmlkit.Xml;
import xmlkit.XmlDocument;
import xmlkit.XmlElement;
import xmlkit.XmlNode;

public class TTNode implements XmlNode {

  protected final NodeInfo nodeInfo;

  public TTNode(NodeInfo nodeInfo) {

    assert nodeInfo != null;

    this.nodeInfo = nodeInfo;
  }

  public NodeInfo getNodeInfo() {
    return nodeInfo;
  }

  @Override
  public boolean isSame(XmlNode node) {
    if(node instanceof TTNode) {
      return getNodeInfo().isSameNodeInfo(((TTNode)node).getNodeInfo());
    }
    
    return false;
  }

  @Override
  public String getNamespaceUri() {

    if (getNodeInfo().getNodeKind() == Type.NAMESPACE) {
      return NamespaceConstant.XMLNS;
    }

    String uri = getNodeInfo().getURI();
    return ("".equals(uri) ? null : uri);
  }

  @Override
  public String getNamespacePrefix() {
    if (getNodeInfo().getNodeKind() == Type.NAMESPACE) {
      if (getNodeInfo().getLocalPart().length() == 0) {
        return null;
      }
    }

    String p = getNodeInfo().getNamePool().getPrefix(
        getNodeInfo().getNameCode());
    return ("".equals(p) ? null : p);
  }

  @Override
  public XmlDocument getDocument() {

    DocumentInfo dInfo = getNodeInfo().getDocumentRoot();
    return (XmlDocument) TTNode.wrap(dInfo);
  }

  public static XmlNode wrap(NodeInfo nodeInfo) {

    if (nodeInfo instanceof DocumentInfo) {
      return (XmlNode) (new TTDocument(
          (TinyDocumentImpl) nodeInfo.getDocumentRoot()));
    } else if (nodeInfo instanceof TinyElementImpl) {
      return (XmlNode) (new TTElement((TinyElementImpl) nodeInfo));
    }

    return null;
  }

  protected static void throwUpdateError() {
    throw new UnsupportedOperationException(
        "The Saxon Tiny Tree cannot be updated");
  }

  @Override
  public XmlElement getRootElement() {

    DocumentInfo di = nodeInfo.getDocumentRoot();
    TTDocument doc = (TTDocument) TTNode.wrap(di);

    return doc.getRootElement();
  }

  @Override
  public XmlNode getNext() {
    TTNode.wrap((NodeInfo) getNodeInfo().iterateAxis(Axis.FOLLOWING_SIBLING)
        .next());
    return null;
  }

  @Override
  public XmlNode getPrevious() {
    return TTNode.wrap((NodeInfo) getNodeInfo().iterateAxis(
        Axis.PRECEDING_SIBLING).next());
  }

  @Override
  public String getText() {
    return getNodeInfo().getStringValue();
  }

  @Override
  public void setText(String text) {
    throwUpdateError();
  }

  @Override
  public boolean queryBool(String path) {
    try {
      Boolean b = (Boolean) Xml.evalXPath(path, getNodeInfo(),
          XPathConstants.BOOLEAN);
      if (b != null) {
        return b.booleanValue();
      }
    } catch (XPathExpressionException e) {
      throw new RuntimeException(e);
    }

    return false;
  }

  @Override
  public String queryText(String path) {
    try {
      return (String)Xml.evalXPath(path, getNodeInfo(),
          XPathConstants.STRING);
    } catch (XPathExpressionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Number queryNumber(String path) {
    try {
      return (Number)Xml.evalXPath(path, getNodeInfo(),
          XPathConstants.NUMBER);
    } catch (XPathExpressionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<XmlNode> query(String xPath) {

    try {

      final List<?> list = (List<?>) Xml.evalXPath(xPath, getNodeInfo(),
          XPathConstants.NODESET);

      return new AbstractList<XmlNode>() {
        @Override
        public XmlNode get(int index) {
          NodeInfo ni = (NodeInfo) list.get(index);
          return (XmlNode) TTNode.wrap(ni);
        }

        @Override
        public int size() {
          return list.size();
        }
      };

    } catch (XPathExpressionException e) {
      throw new RuntimeException(e);
    }

  }
}
