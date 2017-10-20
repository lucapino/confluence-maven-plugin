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
import com.github.lucapino.confluence.rest.core.api.RequestException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal export-page
 * @requiresProject false
 */
public class ExportPageConfluenceMojo extends AbstractConfluenceMojo {

    /**
     * Page description.
     *
     * @parameter
     * @required
     */
    private PageDescriptor page;
    /**
     * File to save pdf verion of newly created page.
     *
     * @parameter
     * @required
     */
    private File outputFile;

    public ExportPageConfluenceMojo() {
    }

    public ExportPageConfluenceMojo(AbstractConfluenceMojo mojo, String pageId, File outputFile) {
        super(mojo);
        this.page = new PageDescriptor(pageId);
        this.outputFile = outputFile;
    }

    @Override
    public void doExecute() throws MojoFailureException {
        String extension = FilenameUtils.getExtension(outputFile.getPath());
        ExportPageConfluenceMojo.Format format = selectFormat(extension);
        if (format == null) {
            throw new MojoFailureException("Format " + format + " is not suported");
        } else {
            String pageId = getClient().getPageId(page);
            downloadFile(format, pageId);
        }
    }

    private ExportPageConfluenceMojo.Format selectFormat(String extension) {
        for (ExportPageConfluenceMojo.Format format : ExportPageConfluenceMojo.Format.values()) {
            if (format.name().equalsIgnoreCase(extension)) {
                return format;
            }
        }
        return null;
    }

    private void downloadFile(ExportPageConfluenceMojo.Format format, String pageId) throws MojoFailureException {
        FileOutputStream out = null;
        InputStream in = null;
        try {
            in = getClient().getRequestService().executeGetRequestForDownload(new URI(url + format.url + "?pageId=" + pageId));
            out = new FileOutputStream(outputFile);
            IOUtils.copy(in, out);
        } catch (URISyntaxException | RequestException | IOException | IllegalStateException e) {
            throw fail("Unable to download page", e);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    static enum Format {

        PDF("/spaces/flyingpdf/pdfpageexport.action"), DOC("/exportword");
        private String url;

        private Format(String url) {
            this.url = url;
        }
    }
}
