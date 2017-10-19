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

import com.atlassian.confluence.rpc.soap.beans.RemoteBlogEntry;
import java.io.File;
import java.rmi.RemoteException;

/**
 * @goal add-blog-entry
 * @requiresProject false
 */
public class AddBlogEntryConfluenceMojo extends AbstractConfluenceMojo {

    /**
     * Space id
     *
     * @parameter
     * @required
     */
    private String space;
    /**
     * Entry title
     *
     * @parameter
     * @required
     */
    private String entryTitle;
    /**
     * Text file with page content
     *
     * @parameter
     * @required
     */
    private File entryFile;

    @Override
    public void doExecute() throws Exception {
        RemoteBlogEntry entry = new RemoteBlogEntry();
        entry.setSpace(space);
        entry.setTitle(entryTitle);
        entry.setContent(getEvaluator().evaluate(entryFile, null));
        try {
            getClient().getService().storeBlogEntry(getClient().getToken(), entry);
        } catch (RemoteException e) {
            throw fail("Unable to upload blog entry", e);
        }
    }
}
