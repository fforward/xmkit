package xmlkit.test;
import static xmlkit.Xml.DocumentType.JAXP_TREE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

import org.xml.sax.InputSource;

import xmlkit.Xml.DocumentType;
import xmlkit.XmlDocument;
import xmlkit.jaxp.JAXPFactory;


public class JAXPXmlTest extends XmlTest {
  public JAXPXmlTest() {
    super(JAXP_TREE);
  }
}
