/*
 * Copyright 2011 Tomasz Maciejewski
 * Copyright 2013 Luca Tagliani
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lucapino.confluence;

import com.github.lucapino.confluence.model.PageDescriptor;
import com.github.lucapino.confluence.rest.core.api.domain.content.BodyBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentResultsBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.StorageBean;
import com.github.lucapino.confluence.rest.core.api.misc.ContentStatus;
import com.github.lucapino.confluence.rest.core.api.misc.ContentType;
import com.github.lucapino.confluence.rest.core.api.misc.ExpandField;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Updates the content of an existing page.
 * If the user don't specify append or prepend, the new content will substitute
 * the whole page.
 *
 * @goal update-page
 * @requiresProject false
 */
public class UpdatePageConfluenceMojo extends AbstractConfluenceMojo {

    /**
     * Use wiki format in the template
     */
    @Parameter(defaultValue = "false", required = true)
    private Boolean wikiFormat;
    /**
     * Page's parent descriptor
     */
    @Parameter(required = true)
    private PageDescriptor parent;
    /**
     * Page title
     */
    @Parameter(required = true)
    private String pageTitle;
    /**
     * Text file with page content
     */
    @Parameter(required = true)
    private File inputFile;
    /**
     * File to save exported verion of updated page.
     */
    @Parameter
    private File outputFile;
    /**
     * Prepend content to existing page.
     */
    @Parameter(defaultValue = "false", required = true)
    private boolean prepend;

    /**
     * Append content to existing page.
     */
    @Parameter(defaultValue = "false", required = true)
    private boolean append;

    @Override
    public void doExecute() throws Exception {
        Log log = getLog();
        if (!inputFile.exists()) {
            log.warn("No template file found. Mojo skipping.");
            return;
        }
        String content = preparePageContent();
        if (wikiFormat) {
            try {
                content = convertWikiToStorageFormat(content);
            } catch (RemoteException e) {
                throw fail("Unable to convert content from wiki format to storage format", e);
            }
        }
        List<String> expand = new ArrayList<>();
        expand.add(ExpandField.BODY_STORAGE.getName());
        ContentResultsBean contentResultsBean = getClient().getClientFactory().getContentClient().getContent(ContentType.PAGE, parent.getSpace(), pageTitle, ContentStatus.ANY, null, expand, 0, 0).get();

        ContentBean contentBean = contentResultsBean.getResults().get(0);
        String oldContent = contentBean.getBody().getStorage().getValue();
        if (prepend) {
            content = content + oldContent;
        } else if (append) {
            content = oldContent + content;
        }
        updatePage(contentBean, content);
    }

    private String preparePageContent() throws MojoFailureException {
        try {
            return getEvaluator().evaluate(inputFile, null);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw fail("Unable to evaluate page content", e);
        }
    }

    private void updatePage(ContentBean contentBean, String content) throws Exception {
        try {

            BodyBean body = new BodyBean();
            StorageBean storageBean = new StorageBean();
            storageBean.setValue(content);
            storageBean.setRepresentation("storage");
            body.setStorage(storageBean);
            contentBean.setBody(body);

            ContentBean updated = getClient().getClientFactory().getContentClient().updateContent(contentBean).get();

            if (outputFile != null) {
                new ExportPageConfluenceMojo(this, updated.getId(), outputFile).execute();
            }
        } catch (InterruptedException | ExecutionException | MojoExecutionException | MojoFailureException e) {
            throw fail("Unable to update page", e);
        }
    }

    private String convertWikiToStorageFormat(String wikiText) throws Exception {
        // we need to call <url>/rest/api/contentbody/convert/storage
        // passing a post body with {"value":"<wikiText>","representation":"wiki"}
        URI uri = new URI(url + "rest/api/contentbody/convert/storage");
        Map<String, String> params = new HashMap<>();
        params.put("value", wikiText);
        params.put("representation", "wiki");
        Map<String, String> executePostRequest = getClient().getRequestService().executePostRequest(uri, params, Map.class);
        return executePostRequest.get("value");
    }
}
