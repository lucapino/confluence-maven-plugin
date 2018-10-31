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
import com.github.lucapino.confluence.rest.core.api.domain.content.BodyBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentResultsBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.StorageBean;
import com.github.lucapino.confluence.rest.core.api.misc.ContentType;
import java.io.File;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Updates the content of an existing page.
 * If the user don't specify append or prepend, the new content will substitute
 * the whole page.
 */
@Mojo(name = "update-page", requiresProject = false)
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
        // Run only at the execution root
        if (runOnlyAtExecutionRoot && !isThisTheExecutionRoot()) {
            log.info("Skipping the announcement mail in this project because it's not the Execution Root");
        } else {
            if (!inputFile.exists()) {
                log.warn("No template file found. Mojo skipping.");
                return;
            }

            // configure page
            ContentResultsBean contentResult = getClientFactory().getContentClient().getContent(ContentType.PAGE, parent.getSpace(), pageTitle, null, null, null, 0, 0).get();
            // getContentBySpaceKeyAndTitle(page.getSpace(), page.getTitle());
            ContentBean updatedPage = contentResult.getResults().get(0);

            // always in storage format
            String oldContent = updatedPage.getBody().getStorage().getValue();
            String content = processContent(inputFile);
            StorageBean storage = new StorageBean();
            if (wikiFormat) {
                storage.setRepresentation(Storage.Representation.WIKI.toString());
            } else {
                storage.setRepresentation(Storage.Representation.STORAGE.toString());
            }
            // now append or prepend
            if (prepend) {
                content = storage.getValue() + oldContent;
            } else if (append) {
                content = oldContent + storage.getValue();
            }
            storage.setValue(content);
            BodyBean body = new BodyBean();
            body.setStorage(storage);
            updatedPage.setBody(body);
            getClientFactory().getContentClient().updateContent(updatedPage);
        }
    }
}
