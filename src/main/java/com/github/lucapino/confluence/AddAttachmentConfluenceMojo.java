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
import com.github.lucapino.confluence.rest.core.api.domain.content.AncestorBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.AttachmentBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentResultsBean;
import com.github.lucapino.confluence.rest.core.api.domain.space.SpaceBean;
import com.github.lucapino.confluence.rest.core.api.misc.ContentType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 */
@Mojo(name = "add-attachment", requiresProject = false)
public class AddAttachmentConfluenceMojo extends AbstractConfluenceMojo {

    /**
     * Page descriptor
     */
    @Parameter(required = true)
    private PageDescriptor page;
    /**
     * Comment
     */
    @Parameter(defaultValue = "")
    private String comment;
    /**
     * Files to attach
     */
    @Parameter(required = true)
    private File[] attachments;

    public AddAttachmentConfluenceMojo() {
        super();
    }

    public AddAttachmentConfluenceMojo(AbstractConfluenceMojo mojo, PageDescriptor page, File[] attachments) {
        super(mojo);
        this.page = page;
        this.attachments = attachments;
    }

    @Override
    public void doExecute() throws MojoFailureException {
        Log log = getLog();
        // Run only at the execution root
        if (runOnlyAtExecutionRoot && !isThisTheExecutionRoot()) {
            log.info("Skipping the announcement mail in this project because it's not the Execution Root");
        } else {
            for (File file : attachments) {
                addAttachment(page, file);
            }
        }
    }

    private void addAttachment(PageDescriptor page, File file) throws MojoFailureException {
        try {
            // configure page
            ContentResultsBean contentResult = getClientFactory().getContentClient().getContent(ContentType.PAGE, page.getSpace(), page.getTitle(), null, null, null, 0, 0).get();
            // getContentBySpaceKeyAndTitle(page.getSpace(), page.getTitle());
            ContentBean parent = contentResult.getResults().get(0);
            AttachmentBean attachment = new AttachmentBean(file, null);
            attachment.setSpace(new SpaceBean(page.getSpace()));
            attachment.setTitle(page.getTitle());
            List<AncestorBean> ancestors = new ArrayList<>();
            AncestorBean ancestor = new AncestorBean();
            ancestor.setId(parent.getId());
            ancestors.add(ancestor);
            attachment.setAncestors(ancestors);
            
            //Content content = new Content();
            // content.setType(Type.ATTACHMENT);
            // content.setSpace(new Space(page.getSpace()));
            //content.setTitle(page.getTitle());
            //content.setAncestors(new Parent[]{parentPage});
            // content.setBody(new Body(new Storage(FileUtils.readFileToString(file), Storage.Representation.STORAGE.toString())));
            getClientFactory().getContentClient().uploadAttachment(attachment, parent);
            // postContent(content);
        } catch (ExecutionException | InterruptedException | IOException e) {
            throw fail("Unable to upload attachment", e);
        }
    }
}
