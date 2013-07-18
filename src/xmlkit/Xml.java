package xmlkit;

import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.Configuration;
import net.sf.saxon.FeatureKeys;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import xmlkit.jaxp.JAXPFactory;
import xmlkit.tiny.TTXmlFactory;

public class Xml {

  public static final String[] DEFAULT_TRAX_FACTORIES = {
      "net.sf.saxon.TransformerFactoryImpl",
      "com.icl.saxon.TransformerFactoryImpl",
      "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl",
      "org.apache.xalan.processor.TransformerFactoryImpl",
      "org.apache.xalan.xsltc.trax.TransformerFactoryImpl",
      "com.icl.saxon.TransformerFactoryImpl",
      "oracle.xml.jaxp.JXSAXTransformerFactory",
      "jd.xml.xslt.trax.TransformerFactoryImpl", };

  public static final String[] DEFAULT_XPATH_FACTORIES = {
      // can do XSLT 2.0 and XQuery 1.0
      "net.sf.saxon.xpath.XPathFactoryImpl",
      // fast and mostly correct
      "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl" };

  private static Class<? extends TransformerFactory> preferredTransformerFactory;

  public static ThreadLocal<TransformerFactory> tTf = new ThreadLocal<TransformerFactory>() {

    @Override
    protected TransformerFactory initialValue() {

      Class<? extends TransformerFactory> tf;
      tf = getPreferredTransformerFactoryClass();
      return createTransformerFactory(tf);
    }
  };

  private static ThreadLocal<DocumentBuilderFactory> tDbf = new ThreadLocal<DocumentBuilderFactory>() {
    @Override
    protected synchronized DocumentBuilderFactory initialValue() {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      return dbf;
    }
  };

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

  @SuppressWarnings("unchecked")
  public static Class<? extends XPathFactory> getPreferredXPathFactory() {

    final String[] xpathFactories = DEFAULT_XPATH_FACTORIES;
    for (int i = 0; xpathFactories != null && i < xpathFactories.length; i++) {

      try {
        Class<? extends XPathFactory> cl;
        cl = (Class<? extends XPathFactory>) Class.forName(xpathFactories[i]);
        if (cl != null) {
          return cl;
        }
      } catch (Throwable t) {
      }
    }

    return null;
  }

  private static XPathFactory xpathFactory;

  static {
    Class<? extends XPathFactory> clazz = getPreferredXPathFactory();
    try {
      Constructor<?> ct = (Constructor<?>) clazz
          .getConstructor(Configuration.class);
      xpathFactory = (XPathFactory) ct.newInstance(TTXmlFactory
          .getGlobalConfiguration());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static enum DocumentType {
    TINY_TREE, JAXP_TREE
  }

  public static XMLReader createXmlReader() {
    return tXmlReader.get();
  }

  public static org.w3c.dom.Document createW3CDocument(InputSource is)
      throws IOException {
    try {
      return createDocumentBuilder().parse(is);
    } catch (SAXException e) {
      throw new IOException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public static DocumentBuilder createDocumentBuilder()
      throws ParserConfigurationException {

    DocumentBuilderFactory dbf;
    dbf = tDbf.get();
    DocumentBuilder b = dbf.newDocumentBuilder();
    return b;
  }

  public static JAXPFactory getJAXPXmlFactory() {
    return new JAXPFactory();
  }

  public static TTXmlFactory getTTXmlFactory() {
    return new TTXmlFactory();
  }

  public static XmlDocument createDocument(InputSource source, DocumentType type)
      throws IOException {

    switch (type) {
    case TINY_TREE:
      return getTTXmlFactory().createDocument(source);
    case JAXP_TREE:
    default:
      return getJAXPXmlFactory().createDocument(source);
    }
  }

  public static XPathExpression compileXPathExpression(String sExpr)
      throws XPathExpressionException {

    XPath xpath = xpathFactory.newXPath();
    XPathExpression expr = xpath.compile(sExpr);
    return expr;
  }

  public static Number evalXPathAsNumber(String xPath, Object source)
      throws XPathExpressionException {
    return (Number) evalXPath(xPath, source, XPathConstants.NUMBER);
  }

  public static String evalXPathAsString(String xPath, Object source)
      throws XPathExpressionException {
    return (String) evalXPath(xPath, source, XPathConstants.STRING);
  }

  public static Object evalXPath(String sExpr, Object source, QName constant)
      throws XPathExpressionException {

    Object result = null;
    XPathExpression expr = compileXPathExpression(sExpr);
    result = expr.evaluate(source, constant);
    return result;
  }

  public static InputSource createInputSource(File f) throws IOException {
    InputSource ss = new InputSource();
    ss.setByteStream(new FileInputStream(f));
    return ss;
  }

  public static InputSource createInputSource(InputStream byteStream) {
    InputSource ss = new InputSource();
    ss.setByteStream(byteStream);
    return ss;
  }

  public static InputSource createInputSource(Reader charStream) {
    InputSource ss = new InputSource();
    ss.setCharacterStream(charStream);
    return ss;
  }

  @SuppressWarnings("unchecked")
  private static Class<? extends TransformerFactory> createPreferredTransformerFactory() {
    final String[] traxFactories = DEFAULT_TRAX_FACTORIES;
    for (int i = 0; traxFactories != null && i < traxFactories.length; i++) {

      try {
        Class<? extends TransformerFactory> cl;
        cl = (Class<? extends TransformerFactory>) Class
            .forName(traxFactories[i]);
        if (cl != null) {
          return cl;
        }
      } catch (Throwable t) {
      }
    }

    return null;
  }

  private static TransformerFactory configureTransformerFactory(
      TransformerFactory transformerFactory) {

    // NOTE: Saxon specific stuff
    transformerFactory.setAttribute(FeatureKeys.CONFIGURATION,
        TTXmlFactory.getGlobalConfiguration());
    return transformerFactory;
  }

  public static TransformerFactory getPreferredTransformerFactory() {
    return tTf.get();
  }

  private static Class<? extends TransformerFactory> getPreferredTransformerFactoryClass() {

    if (preferredTransformerFactory == null) {
      preferredTransformerFactory = createPreferredTransformerFactory();
    }

    return preferredTransformerFactory;
  }

  protected static TransformerFactory createTransformerFactory(
      Class<? extends TransformerFactory> tf) {

    try {
      return configureTransformerFactory(tf.newInstance());
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static final void serialize(Source source, StreamResult r,
      boolean xmlDecl) {
    try {
      TransformerFactory transFactory = Xml.getPreferredTransformerFactory();
      Transformer transformer;

      transformer = transFactory.newTransformer();
      if (!xmlDecl) {
        transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes");
      }

      transformer.transform(source, r);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
