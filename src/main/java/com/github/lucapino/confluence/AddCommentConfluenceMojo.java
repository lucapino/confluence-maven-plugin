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
import com.github.lucapino.confluence.rest.core.api.domain.content.CommentBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentResultsBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.StorageBean;
import com.github.lucapino.confluence.rest.core.api.domain.space.SpaceBean;
import com.github.lucapino.confluence.rest.core.api.misc.ContentType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 *
 */
@Mojo(name = "add-comment", requiresProject = false)
public class AddCommentConfluenceMojo extends AbstractConfluenceMojo {

    /**
     * Space id
     *
     */
    @Parameter(required = true)
    private PageDescriptor page;
    /**
     * Comment
     *
     */
    @Parameter(required = true)
    private File commentBody;

    @Override
    public void doExecute() throws Exception {
        Log log = getLog();
        // Run only at the execution root
        if (runOnlyAtExecutionRoot && !isThisTheExecutionRoot()) {
            log.info("Skipping the announcement mail in this project because it's not the Execution Root");
        } else {// parse template
            String evaluate = processContent(commentBody);
            try {
                // configure page
                ContentResultsBean contentResult = getClientFactory().getContentClient().getContent(ContentType.PAGE, page.getSpace(), page.getTitle(), null, null, null, 0, 0).get();
                ContentBean parent = contentResult.getResults().get(0);
                CommentBean comment = new CommentBean();
                comment.setSpace(new SpaceBean(page.getSpace()));
                comment.setType(ContentType.COMMENT.getName());
                List<AncestorBean> ancestors = new ArrayList<>();
                AncestorBean ancestor = new AncestorBean();
                ancestor.setId(parent.getId());
                ancestors.add(ancestor);
                comment.setAncestors(ancestors);
                BodyBean body = new BodyBean();
                StorageBean storage = new StorageBean();
                storage.setRepresentation(Storage.Representation.STORAGE.toString());
                storage.setValue(evaluate);
                body.setStorage(storage);
                comment.setBody(body);      
                getClientFactory().getContentClient().createComment(comment);
                
            } catch (MojoFailureException e) {
                throw fail("Unable to upload blog entry", e);
            }
        }
    }
}
