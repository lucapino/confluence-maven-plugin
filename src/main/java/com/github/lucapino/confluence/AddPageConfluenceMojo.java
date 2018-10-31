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
import com.github.lucapino.confluence.model.Storage;
import com.github.lucapino.confluence.rest.core.api.domain.content.AncestorBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.BodyBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentResultsBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.StorageBean;
import com.github.lucapino.confluence.rest.core.api.domain.space.SpaceBean;
import com.github.lucapino.confluence.rest.core.api.misc.ContentType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 */
@Mojo(name = "add-page", requiresProject = false)
public class AddPageConfluenceMojo extends AbstractConfluenceMojo {

    /**
     * Use wiki format in the template
     *
     */
    @Parameter(defaultValue = "false", required = true)
    private Boolean wikiFormat;
    /**
     * Page's parent descriptor
     *
     */
    @Parameter(required = true)
    private PageDescriptor parent;
    /**
     * Page title
     *
     */
    @Parameter(required = true)
    private String pageTitle;
    /**
     * Text file with page content
     *
     */
    @Parameter(required = true)
    private File inputFile;
    /**
     * Attachments to add
     *
     */
    @Parameter
    private File[] attachments;

    @Override
    public void doExecute() throws Exception {
        String evaluate = processContent(inputFile);
        createPageObject(parent, evaluate);
    }

    private void createPageObject(PageDescriptor parent, String content) throws Exception {
        Log log = getLog();
        // Run only at the execution root
        if (runOnlyAtExecutionRoot && !isThisTheExecutionRoot()) {
            log.info("Skipping the announcement mail in this project because it's not the Execution Root");
        } else {
            try {
                // configure page
                ContentResultsBean contentResult = getClientFactory().getContentClient().getContent(ContentType.PAGE, parent.getSpace(), parent.getTitle(), null, null, null, 0, 0).get();
                ContentBean parentContent = contentResult.getResults().get(0);

                ContentBean newPage = new ContentBean();
                newPage.setType(ContentType.PAGE.getName());
                newPage.setSpace(new SpaceBean(parent.getSpace()));
                newPage.setTitle(pageTitle);
                List<AncestorBean> ancestors = new ArrayList<>();
                AncestorBean ancestor = new AncestorBean();
                ancestor.setId(parentContent.getId());
                ancestors.add(ancestor);
                newPage.setAncestors(ancestors);
                BodyBean body = new BodyBean();
                StorageBean storage;
                if (wikiFormat) {
                    StorageBean contentStorage = new StorageBean();
                    contentStorage.setValue(content);
                    contentStorage.setRepresentation(Storage.Representation.WIKI.toString());
                    storage = getClientFactory().getContentClient().convertContent(contentStorage).get();
                } else {
                    storage = new StorageBean();
                    storage.setRepresentation(Storage.Representation.STORAGE.toString());
                    storage.setValue(content);
                }
                body.setStorage(storage);
                newPage.setBody(body);
                ContentBean newContent = getClientFactory().getContentClient().createContent(newPage).get();

                PageDescriptor newPageDescriptor = new PageDescriptor(newContent.getId(), newContent.getSpace().getKey(), newContent.getTitle());

                if (!ArrayUtils.isEmpty(attachments)) {
                    new AddAttachmentConfluenceMojo(this, newPageDescriptor, attachments).execute();
                }
            } catch (Exception e) {
                throw fail("Unable to upload page", e);
            }
        }
    }
}
