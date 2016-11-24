package org.carbon.migration;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.apimgt.migration.client._110Specific.dto.SynapseDTO;
import org.wso2.carbon.apimgt.migration.client._200Specific.ResourceModifier200;
import org.wso2.carbon.apimgt.migration.util.Constants;

/**
 * Created by pubudu on 11/23/16.
 */
public class Client {

    private static Log log = LogFactory.getLog(App.class);
    private String repoPath;

    public Client(String repoPath) {
        this.repoPath = repoPath;
    }

    public void migrateThrottlingHandler() {

        log.info("Starting throttle handler migration...");

        File[] tenants = getTenants();

        for (File tenantId : tenants) {

            String apiPath = tenantId.getPath() + File.separatorChar + "synapse-configs" + File.separatorChar +
                    "default" + File.separatorChar + "api";
            List<SynapseDTO> synapseDTOs = RUtil.getVersionedAPIs(apiPath);
            ResourceModifier200.updateThrottleHandler(synapseDTOs);

            for (SynapseDTO synapseDTO : synapseDTOs) {
                RUtil.transformXMLDocument(synapseDTO.getDocument(), synapseDTO.getFile());
            }
        }

    }

    public File[] getTenants() {

        File folder = new File(repoPath);
        return folder.listFiles();
    }

    public void updateRequestTimeProperty() {

        log.info("Starting updating request time property...");

        File[] tenants = getTenants();

        for (File tenantId : tenants) {

            String apiPath = tenantId.getPath() + File.separatorChar + "synapse-configs" + File.separatorChar +
                    "default" + File.separatorChar + "api";
            List<SynapseDTO> synapseDTOs = RUtil.getVersionedAPIs(apiPath);

            for (SynapseDTO synapseDTO : synapseDTOs) {

                boolean isPropertyNeeded = true;
                Document document = synapseDTO.getDocument();
                NodeList handlerNodes = document.getElementsByTagName("handler");

                for (int temp = 0; temp < handlerNodes.getLength(); temp++) {

                    Node nNode = handlerNodes.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String className = eElement.getAttribute("class");

                        if (className.equals("org.wso2.carbon.apimgt.gateway.handlers.security.APIAuthenticationHandler")) {
                            isPropertyNeeded = false;
                            break;
                        }
                    }
                }

                if (isPropertyNeeded) {
                    updateResource(synapseDTO.getDocument());
                    RUtil.transformXMLDocument(synapseDTO.getDocument(), synapseDTO.getFile());
                }
            }
        }
    }

    public void updateResource(Document document) {

        NodeList resourceNodes = document.getElementsByTagName("resource");
        for (int i = 0; i < resourceNodes.getLength(); i++) {
            Element resourceElement = (Element) resourceNodes.item(i);
            updateInSequence(resourceElement, document);
        }
    }

    private static void updateInSequence(Element resourceElement, Document doc) {

        Element inSequenceElement = (Element) resourceElement
                .getElementsByTagName(Constants.SYNAPSE_API_ELEMENT_INSEQUENCE).item(0);

        boolean isExistProp = false;
        NodeList properties = inSequenceElement.getElementsByTagName(Constants.SYNAPSE_API_ELEMENT_PROPERTY);
        for (int i = 0; i < properties.getLength(); ++i) {
            Element propertyElement = (Element) properties.item(i);
            if (propertyElement.hasAttribute(Constants.SYNAPSE_API_ATTRIBUTE_NAME)) {
                if ("api.ut.requestTime".equals(propertyElement.getAttribute(Constants.SYNAPSE_API_ATTRIBUTE_NAME))) {
                    isExistProp = true;
                    log.info("Property api.ut.requestTime'" + "' already exist.");
                    break;
                }
            }
        }

        if (!isExistProp) {
            Element propertyElement = doc
                    .createElementNS(Constants.SYNAPSE_API_XMLNS, Constants.SYNAPSE_API_ELEMENT_PROPERTY);
            propertyElement
                    .setAttribute(Constants.SYNAPSE_API_ATTRIBUTE_EXPRESSION, Constants.SYNAPSE_API_VALUE_EXPRESSION);
            propertyElement.setAttribute(Constants.SYNAPSE_API_ATTRIBUTE_NAME,
                    "api.ut.requestTime");

            Node firstElement = inSequenceElement.getFirstChild();

            if (firstElement != null) {
                inSequenceElement.insertBefore(propertyElement, firstElement);
            } else {
                inSequenceElement.appendChild(propertyElement);
            }

        }

    }


}
