package xmlkit.tiny;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.AbstractList;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.NamespaceConstant;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.query.QueryResult;
import net.sf.saxon.tinytree.TinyDocumentImpl;
import net.sf.saxon.tinytree.TinyElementImpl;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;
import xmlkit.Xml;
import xmlkit.XmlDocument;
import xmlkit.XmlElement;
import xmlkit.XmlNode;

public class TTNode implements XmlNode {

  protected final NodeInfo nodeInfo;

  public TTNode(NodeInfo nodeInfo) {
    if(nodeInfo == null) throw new IllegalArgumentException("nodeInfo cannot be null");

    this.nodeInfo = nodeInfo;
  }

  public NodeInfo getNodeInfo() {
    return nodeInfo;
  }
  
  @Override
  public String getName() {
    return getNodeInfo().getLocalPart();
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

    return nodeInfo != null ? new TTNode(nodeInfo) : null;
  }

  protected static void throwUpdateError() {
    throw new UnsupportedOperationException(
        "The Saxon Tiny Tree cannot be updated");
  }

  @Override
  public XmlElement getRootElement() {

    DocumentInfo di = nodeInfo.getDocumentRoot();
    AxisIterator ax = di.iterateAxis(Axis.CHILD, NodeKindTest.ELEMENT);
    return (XmlElement)TTNode.wrap((NodeInfo)ax.next());
  
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
  
  @Override
  public XmlElement queryElement(String xPath) {
    return (XmlElement)queryNode(xPath);
  }

  @Override
  public XmlNode queryNode(String xPath) {
    try {
      return TTNode.wrap((NodeInfo)Xml.evalXPath(xPath, getNodeInfo(),
          XPathConstants.NODE));
    } catch (XPathExpressionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toXml() {
    return toXml(false);
  }
  
  @Override
  public String toXml(boolean xmlDecl) {
    StringWriter sw = new StringWriter();
    
    
    serialize(getNodeInfo(), new StreamResult(sw), xmlDecl);
    return sw.toString();
  }
  
  private final Result serialize(NodeInfo ni, Result result, boolean decl) {

    try {
      Properties props = new Properties();
      props.setProperty("method", "xml");
      props.setProperty("indent", "yes");
      props.setProperty("omit-xml-declaration", decl ? "no" : "yes");
      QueryResult.serialize(ni, result, props);
      return result;
    } catch (XPathException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  public XmlNode getParent() {
    return TTNode.wrap(getNodeInfo().getParent());
  }

  @Override
  public boolean isAttribute() {
    return getNodeInfo().getNodeKind()  == Type.ATTRIBUTE;
  }

  @Override
  public boolean isText() {
    return getNodeInfo().getNodeKind()  == Type.TEXT;
  }
  
  

  @Override
  public OutputStream toXml(OutputStream os, boolean xmlDecl) {
    toXml(new StreamResult(os), xmlDecl);
    return os;
  }
  
  private final void toXml(StreamResult r, boolean xmlDecl) {
    Xml.serialize(getNodeInfo(), r, xmlDecl);
  }
  
  @Override
  public TTNode copy() {
    throwUpdateError();
    return null;
  }
  
  @Override
  public int hashCode() {
    return getNodeInfo().hashCode();
  }
}
