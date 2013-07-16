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

public class TTXmlTest extends TestCase {

  private static File largeXmlFile;

  private XmlDocument largeDocument;

  static {
    largeXmlFile = createLargeXmlDataSet();
    largeXmlFile.deleteOnExit();
    System.out.println("largeXmlFileSize: " + largeXmlFile.length());
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
      for (int i = 0; i < 1000000; i++) {

        float price = r.nextFloat();

        xWriter.writeStartElement("node");

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

  private String createSimpleXmlString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    sb.append("<node><node/></node>");
    return sb.toString();
  }

  public XmlDocument getLargeDocument() throws IOException {
    if (largeDocument == null) {
      InputSource is = new InputSource();
      FileInputStream fis = new FileInputStream(largeXmlFile);
      is.setByteStream(fis);

      largeDocument = Xml.createDocument(is, DocumentType.TINY_TREE);
    }

    return largeDocument;
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
        
        if(i+1< list2.size()) {
          assertTrue(!list1.get(i).isSame(list2.get(i+1)));
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

      assertEquals(1000000, list.size());

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void testSimpleCreateDocument() {

    try {

      String xml = createSimpleXmlString();
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(xml));

      XmlDocument doc = Xml.createDocument(is, DocumentType.TINY_TREE);

      assertNotNull(doc);

      List<?> list = doc.getChildElements();

      assertEquals(1, list.size());

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
