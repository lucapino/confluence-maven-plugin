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
import com.github.lucapino.confluence.rest.core.api.domain.content.ContainerBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.StorageBean;
import com.github.lucapino.confluence.rest.core.api.domain.space.SpaceBean;
import com.github.lucapino.confluence.rest.core.api.misc.ContentType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 */
@Mojo(name = "add-page", requiresProject = false)
public class AddPageConfluenceMojo extends AbstractConfluenceMojo {

    /**
     * Use wiki format in the template
     *
     * @parameter default-value="false"
     * @required
     */
    private Boolean wikiFormat;
    /**
     * Page's parent descriptor
     *
     * @parameter
     * @required
     */
    private PageDescriptor parent;
    /**
     * Page title
     *
     * @parameter
     * @required
     */
    private String pageTitle;
    /**
     * Text file with page content
     *
     * @parameter
     * @required
     */
    private File inputFile;
    /**
     * File to save exported verion of newly created page.
     *
     * @parameter
     */
    private File outputFile;
    /**
     * Attachments to add
     *
     * @parameter
     */
    private File[] attachments;

    @Override
    public void doExecute() throws Exception {
        String content = preparePageContent();
        if (wikiFormat) {
            try {
                content = convertWikiToStorageFormat(content);
            } catch (RemoteException e) {
                throw fail("Unable to convert content from wiki format to storage format", e);
            }
        }
        createPageObject(parent, content);
    }

    private String preparePageContent() throws MojoFailureException {
        try {
            return getEvaluator().evaluate(inputFile, null);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw fail("Unable to evaluate page content", e);
        }
    }

    private void createPageObject(PageDescriptor parent, String content) throws Exception {
        try {
            String parentId = getClient().getPageId(parent);
            String space = parent.getSpace();
            // create content
            ContentBean contentBean = new ContentBean();
            SpaceBean spaceBean = new SpaceBean(parent.getSpace());
            contentBean.setSpace(spaceBean);
            ContainerBean containerBean = new ContainerBean();
            containerBean.setKey(space);
            containerBean.setId(Integer.valueOf(parentId));
            contentBean.setContainer(containerBean);
            contentBean.setType(ContentType.PAGE.getName());
            contentBean.setSpace(spaceBean);
            contentBean.setTitle(pageTitle);
            BodyBean body = new BodyBean();
            StorageBean storageBean = new StorageBean();
            storageBean.setValue(content);
            storageBean.setRepresentation("storage");
            body.setStorage(storageBean);
            contentBean.setBody(body);
            ContentBean newContentBean = getClient().getClientFactory().getContentClient().createContent(contentBean).get();

            if (!ArrayUtils.isEmpty(attachments)) {
                new AddAttachmentConfluenceMojo(this, newContentBean.getId(), attachments).execute();
            }
            if (outputFile != null) {
                new ExportPageConfluenceMojo(this, newContentBean.getId(), outputFile).execute();
            }
        } catch (Exception e) {
            throw fail("Unable to upload page", e);
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
