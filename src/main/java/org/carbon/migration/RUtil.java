package org.carbon.migration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.carbon.apimgt.migration.APIMigrationException;
import org.wso2.carbon.apimgt.migration.client._110Specific.dto.SynapseDTO;
import org.wso2.carbon.apimgt.migration.util.Constants;
import org.wso2.carbon.apimgt.migration.util.ResourceUtil;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pubudu on 11/23/16.
 */
public class RUtil {

    private static final Log log = LogFactory.getLog(RUtil.class);

    public static List<SynapseDTO> getVersionedAPIs(String apiFilePath) {
        File apiFiles = new File(apiFilePath);
        File[] files = apiFiles.listFiles();
        List<SynapseDTO> versionedAPIs = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                try {
                    if (!file.getName().endsWith(".xml")) { // Ignore non xml files
                        continue;
                    }

                    Document doc = buildDocument(file, file.getName());
                    Element rootElement = doc.getDocumentElement();

                    // Ensure that we skip internal apis such as '_TokenAPI_.xml' and apis
                    // that represent default versions
                    if (Constants.SYNAPSE_API_ROOT_ELEMENT.equals(rootElement.getNodeName()) &&
                            rootElement.hasAttribute(Constants.SYNAPSE_API_ATTRIBUTE_VERSION)) {
                        if (log.isDebugEnabled()) {
                            log.debug("API file name : " + file.getName());
                        }
                        SynapseDTO synapseConfig = new SynapseDTO(doc, file);
                        versionedAPIs.add(synapseConfig);
                    }
                } catch (APIMigrationException e) {
                    log.error("Error when passing file " + file.getName(), e);
                }
            }
        }
        return versionedAPIs;
    }

    public static Document buildDocument(File file, String fileName) throws APIMigrationException {
        Document doc = null;
        try {
            DocumentBuilder docBuilder = getDocumentBuilder(fileName);
            doc = docBuilder.parse(file);
            doc.getDocumentElement().normalize();
        } catch (SAXException e) {
            ResourceUtil.handleException("Error occurred while parsing the " + fileName + " xml document", e);
        } catch (IOException e) {
            ResourceUtil.handleException("Error occurred while reading the " + fileName + " xml document", e);
        }

        return doc;
    }

    private static DocumentBuilder getDocumentBuilder(String fileName) throws APIMigrationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = null;
        try {
            docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            ResourceUtil.handleException("Error occurred while trying to build the " + fileName + " xml document", e);
        }

        return docBuilder;
    }

    public static void transformXMLDocument(Document document, File file) {
        document.getDocumentElement().normalize();

        String apiPath = file.getPath();

        try {
            Transformer e = TransformerFactory.newInstance().newTransformer();
            e.setOutputProperty("encoding", Charset.defaultCharset().toString());
            e.setOutputProperty("indent", "yes");
            e.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            File foFile = new File(apiPath);
            Result foResult = new StreamResult(foFile.toURI().getPath());
            e.transform(new DOMSource(document), foResult);
            log.info(apiPath + " File Updated");

        } catch (TransformerConfigurationException var3) {
            log.error("Transformer configuration error encountered while transforming file " + file.getName(), var3);
        } catch (TransformerException var4) {
            log.error("Transformer error encountered while transforming file " + file.getName(), var4);
        }

    }
}
