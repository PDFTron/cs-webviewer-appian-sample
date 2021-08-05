package com.pdftron.webviewer.templates;

import com.appian.connectedsystems.simplified.sdk.SimpleClientApi;
import com.appian.connectedsystems.simplified.sdk.SimpleClientApiRequest;
import com.appian.connectedsystems.templateframework.sdk.ClientApiResponse;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;
import com.appiancorp.services.ServiceContext;
import com.appiancorp.services.ServiceContextFactory;
import com.appiancorp.suiteapi.common.ServiceLocator;
import com.appiancorp.suiteapi.common.exceptions.PrivilegeException;
import com.appiancorp.suiteapi.common.exceptions.StorageLimitException;
import com.appiancorp.suiteapi.content.Content;
import com.appiancorp.suiteapi.content.ContentConstants;
import com.appiancorp.suiteapi.content.ContentOutputStream;
import com.appiancorp.suiteapi.content.ContentService;
import com.appiancorp.suiteapi.content.exceptions.*;
import com.appiancorp.suiteapi.knowledge.Document;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.pdftron.webviewer.templates.WebViewerConnectedSystem.UPLOAD_DOC_AS_PROP;

@TemplateId(name = "WebViewerStorageClientApi")
public class WebViewerStorageClientApi extends SimpleClientApi {

    @Override
    protected ClientApiResponse execute(
            SimpleClientApiRequest simpleClientApiRequest, ExecutionContext executionContext) {

        Map<String,Object> resultMap = new HashMap<>();

        String uploadImageAsUser = simpleClientApiRequest.getConnectedSystemConfiguration().getValue(UPLOAD_DOC_AS_PROP);

        // Obtain the values from the request sent from the rich text editor.
        String docData,newDocName = "New Document";
        long existingDocument = 0;
        long uploadFolder = 0;
        boolean createNewDocument;

        try {
            docData = (String) simpleClientApiRequest.getPayload().get("base64");
            createNewDocument = (boolean) simpleClientApiRequest.getPayload().get("createNewDocument");
            if(!createNewDocument) {
                existingDocument = ((Number) simpleClientApiRequest.getPayload().get("documentId")).longValue();
            } else {
                newDocName = (String) simpleClientApiRequest.getPayload().get("newDocName");
                uploadFolder = ((Number) simpleClientApiRequest.getPayload().get("documentFolder")).longValue();
            }
                   
        } catch (Exception e) {
            resultMap.put("error", e.getLocalizedMessage());
            return new ClientApiResponse(resultMap);
        }

        // Convert base64 to a buffered image.
        String extension = "pdf";
        byte[] docBytes = Base64.getDecoder().decode(docData);

        // Create an Appian document.
        // I know this is deprecated, but the dependency injection strategy only works for
        // smart services and expression functions.
        // Reference:
        // https://community.appian.com/discussions/f/plug-ins/12745/contentservice-dependency-injection-not-working
        ServiceContext uploadImageUserCtx = ServiceContextFactory.getServiceContext(uploadImageAsUser);
        ContentService cs = ServiceLocator.getContentService(uploadImageUserCtx);

        if(createNewDocument) {
            Document doc = new Document();
            doc.setName(newDocName);
            doc.setDescription("Document generated from the WebViewer component");
            doc.setExtension(extension);
            doc.setParent(uploadFolder);

            Long newDocId;

            try (ContentOutputStream cos = cs.upload(doc, ContentConstants.UNIQUE_NONE)) {
                cos.write(docBytes);
                newDocId = cos.getContentId();
            } catch (Exception e) {
                resultMap.put("error", e.getLocalizedMessage());
                return new ClientApiResponse(resultMap);
            }

            // Return the document id back to the Rich Text Editor.
            resultMap.put("docId", newDocId);
        }
        else{
            Document oldVersionDoc;
            Long newVersionDocId;
            try {
                oldVersionDoc = (Document) cs.download(existingDocument, ContentConstants.VERSION_CURRENT, false)[0];
                Document newVersionDoc = new Document(oldVersionDoc.getParent(), oldVersionDoc.getName(), "pdf");
                newVersionDoc.setSize(docBytes.length);
                newVersionDoc.setId(oldVersionDoc.getId());
                newVersionDoc.setDescription(oldVersionDoc.getDescription());
                newVersionDocId = cs.createVersion(newVersionDoc, ContentConstants.UNIQUE_NONE).getId()[0];
                Path path = Paths.get(cs.getInternalFilename(newVersionDocId));
                Files.write(path,docBytes);
            } catch (Exception e) {
                resultMap.put("error", e.getLocalizedMessage());
                return new ClientApiResponse(resultMap);
            }
            resultMap.put("docId", newVersionDocId);
        }

        return new ClientApiResponse(resultMap);
    }
}
