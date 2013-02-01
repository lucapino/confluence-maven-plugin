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
package it.peng.maven.confluence;

import com.atlassian.confluence.rpc.soap.beans.RemotePage;
import it.peng.maven.confluence.model.PageDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * Updates the content of an existing page.
 * If the user don't specify append or prepend, the new content will substitute the whole page.
 * @goal update-page
 * @requiresProject false
 */
public class UpdatePageConfluenceMojo extends AbstractConfluenceMojo {

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
     * File to save exported verion of updated page.
     *
     * @parameter
     */
    private File outputFile;
    /**
     * Prepend content to existing page.
     *
     * @parameter default-value="false"
     * @required
     */
    private boolean prepend;
    
    /**
     * Append content to existing page.
     *
     * @parameter default-value="false"
     * @required
     */
    private boolean append;
    
    @Override
    public void doExecute() throws Exception {
        Log log = getLog();
        if (!inputFile.exists()) {
            log.warn("No template file found. Mojo skipping.");
            return;
        }
        String content = preparePageContent();
        if (wikiFormat) {
            try {
                content = getClient().getService().convertWikiToStorageFormat(getClient().getToken(), content);
            } catch (RemoteException e) {
                throw fail("Unable to convert content from wiki format to storage format", e);
            }
        }
        RemotePage page = createPageObject(content);
        updatePage(page);
    }

    private String preparePageContent() throws MojoFailureException {
        try {
            return getEvaluator().evaluate(inputFile, null);
        } catch (FileNotFoundException e) {
            throw fail("Unable to evaluate page content", e);
        } catch (UnsupportedEncodingException e1) {
            throw fail("Unable to evaluate page content", e1);
        }
    }

    private RemotePage createPageObject(String content) throws MojoFailureException {
        RemotePage page;
        String pageContent;
        try {
            page = getClient().getService().getPage(getClient().getToken(), parent.getSpace(), pageTitle);
            pageContent = page.getContent();
        } catch (RemoteException e) {
            throw fail("Unable to retrieve page to update", e);
        }
        if (append) {
            pageContent += content;
        } else if (prepend) {
            pageContent = content + pageContent;
        } else {
            pageContent = content;
        }
        page.setContent(pageContent);
        return page;
    }

    private void updatePage(RemotePage page) throws Exception {
        try {
            RemotePage created = getClient().getService().storePage(getClient().getToken(), page);
            if (outputFile != null) {
                new ExportPageConfluenceMojo(this, created.getId(), outputFile).execute();
            }
        } catch (RemoteException e) {
            throw fail("Unable to update page", e);
        }
    }
}
