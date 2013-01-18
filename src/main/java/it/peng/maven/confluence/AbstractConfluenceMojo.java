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

import it.peng.maven.confluence.helpers.ConfluenceClient;
import it.peng.maven.confluence.helpers.TemplateEvaluator;
import java.net.URL;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

public abstract class AbstractConfluenceMojo extends AbstractMojo {

    /**
     * @parameter parameter="settings"
     */
    Settings settings;
    /**
     * Server id corresponding to entry within <i>settings.xml</i>
     *
     * @parameter parameter="confluence.server"
     */
    protected String serverId;
    /**
     * URL pointing to Confluence server, i.e:
     * <ul>
     * <li>https://developer.atlassian.com</li>
     * <li>http://www.example.org/confluence/</li>
     * </ul>
     *
     * @parameter parameter="confluence.url"
     * @required
     */
    protected URL url;
    /**
     * Whether to use v2 API instead of v1 which is the default one
     *
     * @parameter parameter="confluence.v2api" default-value="false"
     */
    protected boolean v2api;
    /**
     * Confluence Authentication User.
     *
     * @parameter parameter="username" default-value="${scmUsername}"
     */
    protected String username;
    /**
     * Confluence Authentication Password.
     *
     * @parameter parameter="password" default-value="${scmPassword}"
     */
    protected String password;
    /**
     * The Maven Wagon manager to use when obtaining server authentication
     * details.
     *
     * @component role="org.apache.maven.artifact.manager.WagonManager"
     * @required
     * @readonly
     */
    protected WagonManager wagonManager;
    private TemplateEvaluator evaluator;
    private ConfluenceClient client;
    /**
     * The Maven project
     *
     * @parameter default-value="${project}"
     * @readonly
     */
    protected MavenProject project;
    /**
     * Returns if this plugin is enabled for this context
     *
     * @parameter parameter="skip"
     */
    protected boolean skip;

    public AbstractConfluenceMojo() {
    }

    public AbstractConfluenceMojo(AbstractConfluenceMojo mojo) {
        this.serverId = mojo.serverId;
        this.url = mojo.url;
        this.v2api = mojo.v2api;
        this.project = mojo.project;
        this.wagonManager = mojo.wagonManager;
        this.evaluator = mojo.evaluator;
        this.client = mojo.client;
        this.setLog(mojo.getLog());
        this.setPluginContext(mojo.getPluginContext());
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
                AuthenticationInfo info = wagonManager.getAuthenticationInfo(serverId);
                client = new ConfluenceClient(info.getUserName(), info.getPassword(), url, v2api);
                getLog().info("Successfuly connected to Confluence server");
            } catch (Exception e) {
                throw fail("Unable to connect to Confluence server", e);
            }
        }
        return client;
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
            try {
                doExecute();
            } finally {
                log.debug("Disconnecting from Confluence server");
                getClient().getService().logout(getClient().getToken());
                log.debug("Disconnected from Confluence server");
            }
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
