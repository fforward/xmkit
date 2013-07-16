import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Random;

import net.sf.saxon.tinytree.TinyTree;

import org.xml.sax.InputSource;

import xmlkit.XMLWriter;
import xmlkit.Xml;
import xmlkit.Xml.DocumentType;
import xmlkit.XmlDocument;
import junit.framework.TestCase;

public class TTXmlTest extends TestCase {

  private static File largeXmlFile;
  
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
    sb.append("<Root/>");
    return sb.toString();
  }

  public void testCreateSimpleTinyTree() {

    try {

      String xml = createSimpleXmlString();
      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(xml));

      TinyTree tt = Xml.getTTXmlFactory().createTinyTree(is);

      assertNotNull(tt);

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  public void testLoadLargeTinyTree() {
    try {
      InputSource is = new InputSource();
      FileInputStream fis = new FileInputStream(largeXmlFile);
      is.setByteStream(fis);

      TinyTree tt = Xml.getTTXmlFactory().createTinyTree(is);

      assertNotNull(tt);

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void testLoadLargeDocument() {
    try {
      InputSource is = new InputSource();
      FileInputStream fis = new FileInputStream(largeXmlFile);
      is.setByteStream(fis);

      XmlDocument doc = Xml.createDocument(is, DocumentType.TINY_TREE);

      assertNotNull(doc);
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

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
