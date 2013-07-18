package xmlkit.test;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.xml.sax.InputSource;

import xmlkit.XMLWriter;
import xmlkit.Xml;
import xmlkit.Xml.DocumentType;
import xmlkit.XmlDocument;
import xmlkit.XmlElement;
import xmlkit.XmlNode;

public abstract class XmlTest extends TestCase {

  public static final int NODE_SIZE = 10000;

  private File largeXmlFile;

  private final DocumentType type;

  private XmlDocument largeDocument;

  public XmlTest(DocumentType type) {
    this.type = type;
  }

  @Override
  public void setUp() {
    largeXmlFile = createLargeXmlDataSet();
  }

  @Override
  public void tearDown() {
    largeXmlFile.delete();
  }

  public void testSimpleToXmlTest() {
    try {
      XmlDocument dom = getLargeDocument();
      List<XmlElement> children = dom.getChildElements();

      assertEquals(NODE_SIZE, children.size());
      XmlElement e = children.get(4);
      String xml = e.toXml();

      assertTrue(!xml.startsWith("<?xml"));

      xml = e.toXml(true);

      assertTrue(xml.startsWith("<?xml"));
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static File createLargeXmlDataSet() {

    try {
      File f = File.createTempFile("TTXmlTest", ".xml");
      FileOutputStream fos = new FileOutputStream(f);
      OutputStreamWriter osw = new OutputStreamWriter(fos);
      XMLWriter xWriter = new XMLWriter(osw);

      xWriter.writeStartDocument();
      xWriter.writeStartElement("nodes");

      Random r = new Random();
      for (int i = 0; i < NODE_SIZE; i++) {

        float price = r.nextFloat();

        xWriter.writeStartElement("node");
        xWriter.writeStartAttribute("i");
        xWriter.writeString(i);
        xWriter.writeEndAttribute();

        xWriter.writeStartElement("name");
        xWriter.writeString(String.format("Node %d", i + 1));
        xWriter.writeEndElement();

        xWriter.writeStartElement("price");
        xWriter.writeString(String.format("Node %f", price));
        xWriter.writeEndElement();

        xWriter.writeEndElement();
      }

      xWriter.writeEndElement();

      xWriter.close();

      return f;

    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static String createSimpleXmlString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    sb.append("<node><node/></node>");
    return sb.toString();
  }

  public void testGetTextNodes() {
    try {
      XmlDocument dom = getLargeDocument();

      List<XmlNode> children = dom.query("/*/node/name/text()");

      assertEquals(children.size(), NODE_SIZE);

      for (XmlNode e : children) {
        assertTrue(e.isText());

        assertNotNull(e.getText());

        assertTrue(e.getText().length() > 0);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void testGetAttributes() {

    try {
      XmlDocument dom = getLargeDocument();

      List<XmlNode> children = dom.query("/*/node/@i");

      assertEquals(children.size(), NODE_SIZE);

      for (XmlNode e : children) {
        assertTrue(e.isAttribute());

        assertNotNull(e.getText());

        assertTrue(e.getText().length() > 0);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void testGetName() {

    try {
      XmlDocument dom = getLargeDocument();

      List<XmlElement> children = dom.getChildElements();

      assertEquals(children.size(), NODE_SIZE);

      for (XmlElement e : children) {
        assertEquals("node", e.getName());
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public XmlDocument getLargeDocument() throws IOException {
    return getLargeDocument(type);
  }

  public XmlDocument getLargeDocument(DocumentType type) throws IOException {

    if (largeDocument == null) {
      largeDocument = createLargeDocument(type);
    }

    return largeDocument;
  }

  protected XmlDocument createLargeDocument(DocumentType type) {
    try {
      InputSource is = new InputSource();
      FileInputStream fis = new FileInputStream(largeXmlFile);
      is.setByteStream(fis);
      return Xml.createDocument(is, type);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void testIsSameNode() {
    try {

      XmlDocument doc1 = getLargeDocument();
      XmlDocument doc2 = getLargeDocument();

      List<XmlElement> list1 = doc1.getChildElements();
      List<XmlElement> list2 = doc2.getChildElements();

      for (int i = 0; i < list1.size(); i++) {

        assertTrue(list1.get(i) != list2.get(i));

        assertTrue(list1.get(i).isSame(list2.get(i)));

        if (i + 1 < list2.size()) {
          assertTrue(!list1.get(i).isSame(list2.get(i + 1)));
        }
      }

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void testLoadLargeDocument() {
    try {

      XmlDocument doc = getLargeDocument();

      assertNotNull(doc);

      List<?> list = doc.getChildElements();

      assertEquals(NODE_SIZE, list.size());

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void testSimpleCreateDocument() {

    try {

      String xml = createSimpleXmlString();
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(xml));

      XmlDocument doc = Xml.createDocument(is, this.type);

      assertNotNull(doc);

      List<?> list = doc.getChildElements();

      assertEquals(1, list.size());

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
