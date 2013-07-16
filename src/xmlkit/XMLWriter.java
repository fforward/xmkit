package xmlkit;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class XMLWriter {

  private static final int CONTENT = 1;

  private static final int ATTRIBUTE = 2;

  private static final int PROLOG = 3;

  private static final int ELEMENT = 4;

  private Writer writer;

  private boolean escapeEnabled = true;

  private String lineSeparator;

  private String encoding = "ISO-8859-15";

  private ArrayList openElements = new ArrayList();

  private int openElementCount = 0;

  private int indentLevel = 0;

  private char indentChar = ' ';

  private char quoteChar = '\"';

  private boolean openStartElement = false;

  private boolean openAttribute = false;

  private boolean attributeWrittenForElement = false;

  private boolean documentStarted = false;

  private boolean writeString = false;
  private short writeState;

  public XMLWriter(Writer writer) {
    this.writer = writer;
  }

  public void write(String toWrite) throws IOException {
    this.writer.write(toWrite);
  }

  private void write(char character) throws IOException {
    this.writer.write(character);
  }

  private String getLineSeparator() {
    if (lineSeparator == null) {
      lineSeparator = System.getProperty("line.separator", "\n");
    }

    return lineSeparator;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  public String getEncoding() {
    return encoding;
  }
  

  public void writeStartDocument() {
    if (documentStarted) {
      String err = "Document already started.";
      throw new RuntimeException(err);
    }
    
    writeState = PROLOG;
  }

  public void writeStartDocument(boolean standAlone) throws IOException {
    if (documentStarted) {
      String err = "Document already started.";
      throw new RuntimeException(err);
    }

    write("<?xml version=\"1.0\" encoding=\"" + getEncoding() + "\" ");
    if (standAlone) {
      // write("standalone=\"yes\"");
    } else {
      // write("standalone=\"no\"");
    }

    write("?>");

    writeState = PROLOG;
  }

  public void writeXMLDeclaration() throws IOException {
    write("<?xml version=\"1.0\" encoding=\"" + getEncoding() + "\"?>");
  }

  public void writeStartElement(String localName) throws IOException {
    closeStartElement();
    writeIndent();

    write("<");
    write(localName);

    if (openElements.size() == openElementCount) {
      openElements.add(new Element(localName));
    }

    writeState = ELEMENT;
    openStartElement = true;
    openElementCount++;
    indentLevel++;
  }

  public void writeEndElement() throws IOException {
    writeEndElementInt(false);
  }

  public void writeFullEndElement() throws IOException {
    writeEndElementInt(true);
  }

  private void writeEndElementInt(boolean fullEnd) throws IOException {
    if (openElementCount == 0) {
      String err = "There was no XML start tag open";
      throw new RuntimeException(err);
    }

    if (openAttribute) {
      writeEndAttribute();
    }

    indentLevel--;

    if (openStartElement) {
      if (openAttribute) {
        writeEndAttribute();
      }

      if (fullEnd) {
        write(">");

        write("</");
        Element e = (Element) openElements.get(openElementCount - 1);
        write(e.localName);
        write(">");
      } else {
        write("/>");
      }

      openElements.remove(openElementCount - 1);
      openElementCount--;
      openStartElement = false;
    } else {
      if (!writeString)
        writeIndent();
      write("</");
      Element e = (Element) openElements.get(openElementCount - 1);
      write(e.localName);
      write('>');
      openElements.remove(openElementCount - 1);
      openElementCount--;
    }

    writeString = false;
  }

  private void writeIndent() throws IOException {
    write(getLineSeparator());
    for (int i = 0; i < indentLevel; i++) {
      write(indentChar);
    }
  }

  public void writeStartAttribute(String localName) throws IOException {

    String formatSpace = "";

    if (writeState == CONTENT) {
      String err = "StartAttribute would result in an invalid XML";
      err += " document in state CONTENT";
      throw new RuntimeException(err);
    }

    if (openStartElement || attributeWrittenForElement) {
      formatSpace = " ";
    }

    write(formatSpace);
    write(localName);
    write('=');
    write(quoteChar);

    openAttribute = true;
    attributeWrittenForElement = true;
    writeState = ATTRIBUTE;

  }

  public void writeEndAttribute() throws IOException {
    write(quoteChar);
    openAttribute = false;
  }

  private void writeStringInt(String text, boolean entitize) throws IOException {

    if (text == null) {
      text = "";
    }

    if (entitize) {
      text = escapeString(text);
    }

    if (!openAttribute) {
      closeStartElement();
    }

    write(text);
    writeString = true;
  }

  public void writeElementString(String localName, String value)
      throws IOException {
    writeStartElement(localName);
    writeString(value);
    writeEndElement();
  }

  public void writeString(String text) throws IOException {
    writeString(text, true);
  }

  public void writeString(int value) throws IOException {
    writeString(String.valueOf(value), true);
  }

  public void writeString(long value) throws IOException {
    writeString(String.valueOf(value), true);
  }

  public void writeString(String text, boolean entitize) throws IOException {
    if (writeState == PROLOG) {
      String err = "Content in Prolog would result in invalid XML";
      throw new RuntimeException(err);
    }
    writeStringInt(text, entitize);
  }

  private String escapeString(String text) {
    if (isEscapeStringEnabled()) {
      return Entities.XML.escape(text);
    }

    return text;
  }

  public boolean isEscapeStringEnabled() {
    return escapeEnabled;
  }

  public void setEscapeStringEnabled(boolean enabled) {
    escapeEnabled = enabled;
  }

  private void closeStartElement() throws IOException {
    if (!openStartElement) {
      return;
    }

    if (openAttribute) {
      writeEndAttribute();
    }

    write(">");

    attributeWrittenForElement = false;
    openStartElement = false;
    writeString = false;
  }

  public void close() throws IOException {
    closeOpenAttributeAndElements();
    writer.close();
  }

  private void closeOpenAttributeAndElements() throws IOException {
    if (openAttribute) {
      writeEndAttribute();
    }

    while (openElementCount > 0) {
      writeEndElement();
    }
  }

  private class Element {

    private String localName;

    public Element(String localName) {
      this.localName = localName;
    }
  }

  /*
   * public static void main(String[] args) { StringWriter writer = new
   * StringWriter(); XMLWriter xml = new XMLWriter(writer); try {
   * xml.writeStartDocument(false); xml.writeStartElement("Insurance");
   * xml.writeStartAttribute("test"); xml.writeString("quark");
   * xml.writeStartElement("Level"); xml.writeStartAttribute("name");
   * xml.writeString("testLevel"); xml.writeEndElement(); xml.writeEndElement();
   * writer.close(); StringBuffer sb = writer.getBuffer();
   * //System.out.println(sb.toString()); } catch (Exception e) {
   * Assert.debug(e); } }
   */
}
