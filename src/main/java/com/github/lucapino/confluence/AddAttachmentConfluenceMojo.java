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
import com.github.lucapino.confluence.rest.core.api.domain.content.AttachmentBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentBean;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.maven.plugin.MojoFailureException;
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

    public AddAttachmentConfluenceMojo(AbstractConfluenceMojo mojo, String pageId, File[] attachments) {
        super(mojo);
        this.page = new PageDescriptor(pageId);
        this.attachments = attachments;
    }

    @Override
    public void doExecute() throws MojoFailureException {
        String pageId = getClient().getPageId(page);
        for (File file : attachments) {
            addAttachment(pageId, file);
        }
    }

    private void addAttachment(String pageId, File file) throws MojoFailureException {
        try {
            AttachmentBean attachmentBean = new AttachmentBean(file, comment);
            ContentBean parentContent = new ContentBean(pageId);
            getClient().getClientFactory().getContentClient().uploadAttachment(attachmentBean, parentContent);
        } catch (FileNotFoundException e) {
            throw fail("Unable to upload attachment", e);
        }
    }
}
