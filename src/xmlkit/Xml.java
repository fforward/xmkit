package xmlkit;

import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.Configuration;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import xmlkit.tiny.TTXmlFactory;

public class Xml {

  public static final String[] DEFAULT_XPATH_FACTORIES = {
      // can do XSLT 2.0 and XQuery 1.0
      "net.sf.saxon.xpath.XPathFactoryImpl",
      // fast and mostly correct
      "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl" };

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
      Constructor<?> ct = (Constructor<?>)clazz.getConstructor(Configuration.class);
      xpathFactory = (XPathFactory)ct.newInstance(TTXmlFactory.getGlobalConfiguration());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

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

  public static XPathExpression compileXPathExpression(String sExpr)
      throws XPathExpressionException {

    XPath xpath = xpathFactory.newXPath();
    XPathExpression expr = xpath.compile(sExpr);
    return expr;
  }

  public static Object evalXPath(String sExpr, Object source, QName constant) 
      throws XPathExpressionException {

    Object result = null;
    XPathExpression expr = compileXPathExpression(sExpr);
    result = expr.evaluate(source, constant);
    return result;
  }
}
