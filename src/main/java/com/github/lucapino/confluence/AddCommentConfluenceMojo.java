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
import com.github.lucapino.confluence.rest.core.api.domain.content.ContentResultsBean;
import com.github.lucapino.confluence.rest.core.api.domain.content.StorageBean;
import com.github.lucapino.confluence.rest.core.api.misc.ContentStatus;
import com.github.lucapino.confluence.rest.core.api.misc.ContentType;
import java.io.File;
import java.rmi.RemoteException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 *
 */
@Mojo(name = "add-comment", requiresProject = false)
public class AddCommentConfluenceMojo extends AbstractConfluenceMojo {

    /**
     * Space id
     *
     * @parameter
     * @required
     */
    private PageDescriptor page;
    /**
     * Comment
     *
     * @parameter
     * @required
     */
    private File commentBody;

    @Override
    public void doExecute() throws Exception {
        // TODO
    }
}
