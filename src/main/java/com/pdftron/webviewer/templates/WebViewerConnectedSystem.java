package com.pdftron.webviewer.templates;

import com.appian.connectedsystems.simplified.sdk.SimpleConnectedSystemTemplate;
import com.appian.connectedsystems.simplified.sdk.configuration.SimpleConfiguration;
import com.appian.connectedsystems.templateframework.sdk.ExecutionContext;
import com.appian.connectedsystems.templateframework.sdk.TemplateId;

@TemplateId(name="WebViewerConnectedSystem")
public class WebViewerConnectedSystem extends SimpleConnectedSystemTemplate {
    public static final String UPLOAD_DOC_AS_PROP = "uploadImageAs";

    @Override
    protected SimpleConfiguration getConfiguration(
            SimpleConfiguration simpleConfiguration, ExecutionContext executionContext) {
        return simpleConfiguration.setProperties(
                textProperty(UPLOAD_DOC_AS_PROP)
                        .label("Upload Document as a User")
                        .instructionText("Document from the WebViewer Component will be shown as created and accessed by this user")
                        .isRequired(true)
                        .build());
    }
}
