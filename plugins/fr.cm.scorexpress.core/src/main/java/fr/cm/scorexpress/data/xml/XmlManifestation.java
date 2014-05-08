package fr.cm.scorexpress.data.xml;

import fr.cm.scorexpress.core.model.ObjManifestation;
import fr.cm.scorexpress.data.node.IManifVisitor;
import org.apache.xml.serialize.BaseMarkupSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.*;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class XmlManifestation {

    private static final boolean ALLOW_ZIP = false;

    public static final int BUFFER_SIZE = 10240;

    private XmlManifestation() {
    }

    public static ObjManifestation loadFile(final String fileName) {
        try {
            final ManifestationHandler manifestationHandler = new ManifestationHandler();
            final SAXParserFactory spf = SAXParserFactory.newInstance();
            // spf.setNamespaceAware(true);
            final SAXParser saxParser = spf.newSAXParser();
            final XMLReader parser = saxParser.getXMLReader();
            parser.setContentHandler(manifestationHandler);
            System.out.println("Load " + fileName);
            parser.parse(fileName.replaceAll("\\s", "%20"));
            final IManifVisitor visitor = new ManifVisitor();
            final ObjManifestation manif = (ObjManifestation) manifestationHandler.getRoot().accept(visitor, null);
            manif.setFileName(fileName);
            return manif;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public static void writeFile(final ObjManifestation manif) {
        BufferedWriter writer = null;
        try {
            final DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
            final Document document = builder.getDOMImplementation().createDocument(
                    null, "Manif", null);
            manif.accept(new ManifWriterVisitor(document), null);
            final File file = new File(manif.getFileName());
            System.out.println(file.getPath());
            final OutputFormat of = new OutputFormat("XML", "ISO-8859-1", false);
            of.setIndent(1);
            of.setPreserveEmptyAttributes(false);
            of.setPreserveSpace(false);
            of.setIndenting(true);
            writer = new BufferedWriter(new FileWriter(file));
            final BaseMarkupSerializer serializer = new XMLSerializer(writer, of);
            serializer.asDOMSerializer();
            serializer.serialize(document.getDocumentElement());
            if (ALLOW_ZIP) {
                final String fileN = manif.getFileName();
                final String zipFileName = fileN.substring(0, fileN.length() - 3) + "zip";
                final File archiveFile = new File(zipFileName);
                final File[] files = {file};
                createZipArchive(archiveFile, files);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null)
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
        }
    }

    protected static void createZipArchive(final File archiveFile, final File[] tobeZippedFiles) {
        try {
            final byte[] buffer = new byte[BUFFER_SIZE];
            // Open archive file
            final FileOutputStream stream = new FileOutputStream(archiveFile);
            final ZipOutputStream out = new ZipOutputStream(stream);
            for (final File tobeZippedFile : tobeZippedFiles) {
                if (tobeZippedFile == null || !tobeZippedFile.exists() || tobeZippedFile.isDirectory())
                    continue;
                System.out.println("Adding " + tobeZippedFile.getName());
                // Add archive entry
                final ZipEntry zipAdd = new ZipEntry(tobeZippedFile.getName());
                zipAdd.setTime(tobeZippedFile.lastModified());
                out.putNextEntry(zipAdd);
                // Read input & write to output
                final FileInputStream in = new FileInputStream(tobeZippedFile);
                while (true) {
                    final int nRead = in.read(buffer, 0, buffer.length);
                    if (nRead <= 0)
                        break;
                    out.write(buffer, 0, nRead);
                }
                in.close();
            }
            out.close();
            stream.close();
            System.out.println("Adding completed OK");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    static class MyErrorHandler implements ErrorHandler {

        public void error(final SAXParseException exception) throws SAXException {
            System.out.println("error");
        }

        public void fatalError(final SAXParseException exception) throws SAXException {
            System.out.println("fatalError");
        }

        public void warning(final SAXParseException exception) throws SAXException {
            System.out.println("warning");
        }
    }
}
