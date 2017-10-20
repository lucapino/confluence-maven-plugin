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
     *
     */
    @Parameter
    Settings settings;
    /**
     * Server id corresponding to entry within <i>settings.xml</i>
     */
    @Parameter(name = "confluence.server")
    protected String serverId;
    /**
     * URL pointing to Confluence server, i.e:
     * <ul>
     * <li>https://developer.atlassian.com</li>
     * <li>http://www.example.org/confluence/</li>
     * </ul>
     */
    @Parameter(name = "confluence.url", required = true)
    protected String url;
    /**
     * Confluence Authentication User.
     */
    @Parameter(defaultValue = "${scmUsername")
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
//        this.setLog(mojo.getLog());
//        this.setPluginContext(mojo.getPluginContext());
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
            getLog().debug("Connecting to Confluence server");
            try {
                client = new ConfluenceClient(username, password, url);
                getLog().info("Successfuly connected to JIRA server");
            } catch (Exception e) {
                getLog().error("Unable to connect to JIRA server", e);
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

    public abstract void doExecute() throws Exception;

    protected MojoFailureException fail(String message, Exception e) {
        getLog().error(message, e);
        return new MojoFailureException(e, message, e.getMessage());
    }
}
