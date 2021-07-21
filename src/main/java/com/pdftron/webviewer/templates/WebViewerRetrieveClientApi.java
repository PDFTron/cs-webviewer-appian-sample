package com.pdftron.webviewer.templates;

import com.appian.connectedsystems.simplified.sdk.SimpleClientApi;
import com.appian.connectedsystems.simplified.sdk.SimpleClientApiRequest;
import com.appian.connectedsystems.templateframework.sdk.ClientApiResponse;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.appiancorp.services.ServiceContext;
import com.appiancorp.services.ServiceContextFactory;
import com.appiancorp.suiteapi.common.ServiceLocator;
import com.appiancorp.suiteapi.content.ContentConstants;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.knowledge.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.pdftron.webviewer.templates.WebViewerConnectedSystem.UPLOAD_DOC_AS_PROP;

@TemplateId(name = "WebViewerRetrieveClientApi")
public class WebViewerRetrieveClientApi extends SimpleClientApi {
    Logger logger = LoggerFactory.getLogger(WebViewerRetrieveClientApi.class);

    @Override
    protected ClientApiResponse execute(
            SimpleClientApiRequest simpleClientApiRequest, ExecutionContext executionContext) {

        Map<String,Object> resultMap = new HashMap<>();

        String uploadImageAsUser = simpleClientApiRequest.getConnectedSystemConfiguration().getValue(UPLOAD_DOC_AS_PROP);
        // Obtain the values from the request sent from the rich text editor.
        String docData,docName;
        long documentId = 0;

        try {
            documentId = ((Number) simpleClientApiRequest.getPayload().get("documentId")).longValue();
        } catch (Exception e) {
            logger.error("Unable to get data from client", e);
            resultMap.put("error", e.getLocalizedMessage());
            return new ClientApiResponse(resultMap);
        }

        // I know this is deprecated, but the dependency injection strategy only works for
        // smart services and expression functions.
        // Reference:
        // https://community.appian.com/discussions/f/plug-ins/12745/contentservice-dependency-injection-not-working
        ServiceContext uploadImageUserCtx = ServiceContextFactory.getServiceContext(uploadImageAsUser);
        ContentService cs = ServiceLocator.getContentService(uploadImageUserCtx);

        //Get a Document from the Appian.
        try {
            Document file = (Document) cs.download(documentId, ContentConstants.VERSION_CURRENT, false)[0];
            File sourceFile = new File(file.getInternalFilename());
            byte[] docBytes = Files.readAllBytes(sourceFile.toPath());
            docName = file.getDisplayName();
            docData = Base64.getEncoder().encodeToString(docBytes);
        } catch (Exception e) {
            logger.error("Error retrieving doc", e);
            resultMap.put("error", e.getLocalizedMessage());
            return new ClientApiResponse(resultMap);
        }


        logger.info("Returning existing docData to client for documentID: " + documentId);
        resultMap.put("docData", docData);
        resultMap.put("docName", docName);

        return new ClientApiResponse(resultMap);
    }
}
