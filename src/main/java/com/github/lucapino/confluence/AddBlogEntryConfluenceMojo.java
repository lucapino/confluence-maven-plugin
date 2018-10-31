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

import com.github.lucapino.confluence.model.Storage;
import com.github.lucapino.confluence.rest.core.api.domain.content.BodyBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.StorageBean;
import com.github.lucapino.confluence.rest.core.api.domain.space.SpaceBean;
import com.github.lucapino.confluence.rest.core.api.misc.ContentType;
import java.io.File;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 */
@Mojo(name = "add-blog-entry", requiresProject = false)
public class AddBlogEntryConfluenceMojo extends AbstractConfluenceMojo {

    /**
     * Space name
     */
    @Parameter(required = true)
    private String space;
    /**
     * Entry title
     */
    @Parameter(required = true)
    private String entryTitle;
    /**
     * Text file with page content
     */
    @Parameter(required = true)
    private File entryFile;

    @Override
    public void doExecute() throws Exception {
        Log log = getLog();
        // Run only at the execution root
        if (runOnlyAtExecutionRoot && !isThisTheExecutionRoot()) {
            log.info("Skipping the announcement mail in this project because it's not the Execution Root");
        } else {// parse template
            String evaluate = processContent(entryFile);
            try {
                // configure page
                ContentBean blog = new ContentBean();
                blog.setType(ContentType.BLOGPOST.getName());
                blog.setSpace(new SpaceBean(space));
                blog.setTitle(entryTitle);
                BodyBean body = new BodyBean();
                StorageBean storage = new StorageBean();
                storage.setRepresentation(Storage.Representation.STORAGE.toString());
                storage.setValue(evaluate);
                body.setStorage(storage);
                getClientFactory().getContentClient().createContent(blog);
            } catch (MojoFailureException e) {
                throw fail("Unable to upload blog entry", e);
            }
        }
    }
}
