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

import com.github.lucapino.confluence.model.Body;
import com.github.lucapino.confluence.model.Content;
import com.github.lucapino.confluence.model.ContentResultList;
import com.github.lucapino.confluence.model.PageDescriptor;
import com.github.lucapino.confluence.model.Parent;
import com.github.lucapino.confluence.model.Space;
import com.github.lucapino.confluence.model.Storage;
import com.github.lucapino.confluence.model.Type;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.maven.plugin.MojoFailureException;
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
                ContentResultList contentResult = getClient().getContentBySpaceKeyAndTitle(parent.getSpace(), parent.getTitle());
                Content parentContent = contentResult.getContents()[0];
                Parent parentPage = new Parent();
                parentPage.setId(parentContent.getId());
                Content newPage = new Content();
                newPage.setType(Type.PAGE);
                newPage.setSpace(new Space(parent.getSpace()));
                newPage.setTitle(pageTitle);
                newPage.setAncestors(new Parent[]{parentPage});
                Storage newStorage;
                if (wikiFormat) {
                    Storage contentStorage = new Storage(content, Storage.Representation.WIKI.toString());
                    newStorage = getClient().convertContent(contentStorage, Storage.Representation.STORAGE);
                } else {
                    newStorage = new Storage(content, Storage.Representation.STORAGE.toString());
                }
                newPage.setBody(new Body(newStorage));
                getClient().postContent(newPage);

                PageDescriptor newPageDescriptor = new PageDescriptor();

                if (!ArrayUtils.isEmpty(attachments)) {
                    new AddAttachmentConfluenceMojo(this, newPageDescriptor, attachments).execute();
                }
            } catch (Exception e) {
                throw fail("Unable to upload page", e);
            }
        }
    }
}
