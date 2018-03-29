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

import com.github.lucapino.confluence.helpers.ConfluenceClient;
import com.github.lucapino.confluence.helpers.TemplateEvaluator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;

public abstract class AbstractConfluenceMojo extends AbstractMojo {

    /**
     * The current project base directory.
     */
    @Parameter(property = "basedir", required = true)
    protected String basedir;
    /**
     * The Maven Session.
     */
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession mavenSession;

    @Parameter(defaultValue = "${settings}", readonly = true, required = true)
    Settings settings;
    /**
     * Server id corresponding to entry within <i>settings.xml</i>
     */
    @Parameter
    protected String serverId;
    /**
     * URL pointing to Confluence server, i.e:
     * <ul>
     * <li>https://developer.atlassian.com</li>
     * <li>http://www.example.org/confluence/</li>
     * </ul>
     */
    @Parameter(required = true)
    protected String url;
    /**
     * Confluence Authentication User.
     */
    @Parameter(defaultValue = "${scmUsername}")
    protected String username;
    /**
     * Confluence Authentication Password.
     */
    @Parameter(defaultValue = "${scmPassword}")
    protected String password;
    /**
     * The Maven project
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;
    /**
     * Returns if this plugin is enabled for this context
     */
    @Parameter(defaultValue = "false")
    protected boolean skip;
    /**
     * This will cause the execution to be run only at the top of a given module
     * tree. That is, run in the project contained in the same folder where the
     * mvn execution was launched.
     */
    @Parameter(defaultValue = "false")
    protected boolean runOnlyAtExecutionRoot;

    /**
     * Map of custom parameters for the release notes. This Map will be passed
     * to the template.
     */
    @Parameter
    protected HashMap announceParameters;

    @Parameter(defaultValue = "false")
    protected boolean verbose;

    private TemplateEvaluator evaluator;
    private ConfluenceClient client;

    public AbstractConfluenceMojo() {
    }

    public AbstractConfluenceMojo(AbstractConfluenceMojo mojo) {
        this.serverId = mojo.serverId;
        this.url = mojo.url;
        this.project = mojo.project;
        this.evaluator = mojo.evaluator;
        this.client = mojo.client;
    }

    public TemplateEvaluator getEvaluator() {
        if (evaluator == null) {
            getLog().debug("Initializing Template Helper...");
            evaluator = new TemplateEvaluator(project);
            getLog().debug("Template Helper initialized");
        }
        return evaluator;
    }

    public ConfluenceClient getClient() throws MojoFailureException {
        if (client == null) {
            loadUserCredentials();
            getLog().debug("Connecting to Confluence server");
            try {
                ConfluenceClient.Builder builder = ConfluenceClient.builder().baseURL(url).username(username).password(password);
                if (verbose) {
                    builder = builder.verbose();
                }
                client = builder.build();
                getLog().info("Successfuly connected to Confluence server");
            } catch (Exception e) {
                getLog().error("Unable to connect to Confluence server", e);
            }
        }
        return client;
    }

    private void loadUserCredentials() {
        if (serverId == null) {
            serverId = url;
        }
        // read credentials from settings.xml if user has not set them in configuration
        if ((username == null || password == null) && settings != null) {
            Server server = settings.getServer(serverId);
            if (server != null) {
                if (username == null) {
                    username = server.getUsername();
                }
                if (password == null) {
                    password = server.getPassword();
                }
            }
        }
    }

    public boolean isSkip() {
        return skip;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        // Run only at the execution root
        if (runOnlyAtExecutionRoot && !isThisTheExecutionRoot()) {
            log.info("Skipping the announcement mail in this project because it's not the Execution Root");
        } else {
            if (isSkip()) {
                log.info("Skipping Plugin execution.");
                return;
            }
            try {
                loadUserCredentials();
                doExecute();
            } catch (Exception e) {
                log.error("Error when executing mojo", e);
            }
        }
    }

    public abstract void doExecute() throws Exception;

    protected MojoFailureException fail(String message, Exception e) {
        getLog().error(message, e);
        return new MojoFailureException(e, message, e.getMessage());
    }

    /**
     * Returns <code>true</code> if the current project is located at the
     * Execution Root Directory (where mvn was
     * launched).
     *
     * @return <code>true</code> if the current project is at the Execution Root
     */
    protected boolean isThisTheExecutionRoot() {
        getLog().debug("Root Folder:" + mavenSession.getExecutionRootDirectory());
        getLog().debug("Current Folder:" + basedir);
        boolean result = mavenSession.getExecutionRootDirectory().equalsIgnoreCase(basedir);
        if (result) {
            getLog().debug("This is the execution root.");
        } else {
            getLog().debug("This is NOT the execution root.");
        }
        return result;
    }

    protected String processContent(File inputFile) throws FileNotFoundException, UnsupportedEncodingException {
        HashMap parameters = new HashMap();
        parameters.put("announceParameters", announceParameters);
        return getEvaluator().evaluate(inputFile, parameters);
    }
}
