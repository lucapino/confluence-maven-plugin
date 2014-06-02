Available goals:
================
* **add-page** - adds new page (with attachments) and optionally exports it
* **add-blog-entry** - adds new blog entry
* **add-comment** - adds comment to existing page
* **add-attachment** - adds attachment to existing page
* **export-page** - exports existing page to several supported formats (pdf, doc)
* **update-page** - updates existing page and optionally exports it

Example plugin definition:
==========================
    <plugin>
        <groupId>it.peng.maven.plugin</groupId>
        <artifactId>confluence-maven-plugin</artifactId>
        <version>1.0.0</version>
        <configuration>
            <serverId>confluence-server</serverId>
            <url>https://confluence.example.org/confluence/ </url>
        </configuration>
    </plugin>

Example _add-page_ goal configuration:
-------------------------------------
    <configuration>
        <parent>
            <space>Example Space</space>
            <title>Parent page name</title>
        </parent>
        <pageTitle>Hello world</pageTitle>
        <inputFile>${basedir}/pagetemplate.txt</inputFile>
        <outputFile>${basedir}/HelloWorld.pdf</outputFile>
        <attachments>
            <value>${basedir}/image.gif</value>
            <value>${basedir}/document.doc</value>
        </attachments>
    </configuration>

Example _add-blog-entry_ goal configuration:
------------------------------------------
    <configuration>
        <space>Example space</space>
        <entryTitle>My First Post</entryTitle>
        <entryFile>${basedir}/blogentry.txt</entryFile>
    </configuration>

Example _add-comment_ goal configuration:
----------------------------------------
    <configuration>
        <page>
            <space>Example space</space>
            <title>My comments</title>
        </page>
        <commentBody>${basedir}/comment.txt</commentBody>
    </configuration>

Example _add-attachment_ goal configuration:
-------------------------------------------
    <configuration>
        <page>
            <space>Example space</space>
            <title>My attachments</title>
        </page>
        <comment>Add attachment example</comment>
        <attachments>
            <value>${basedir}/document.pdf</value>
            <value>${basedir}/picture.png</value>
        </attachments>
    </configuration>

Example _export-page_ goal configuration:
----------------------------------------
    <configuration>
        <page>
            <space>Example space</space>
            <title>My page</title>
        </page>
        <outputFile>${basedir}/MyPage.pdf</outputFile>
    </configuration>

Example _update-page_ goal configuration:
-------------------------------------
    <configuration>
        <parent>
            <space>Example Space</space>
            <title>Parent page name</title>
        </parent>
        <pageTitle>Hello world</pageTitle>
        <inputFile>${basedir}/pagetemplate.txt</inputFile>
        <outputFile>${basedir}/HelloWorld.pdf</outputFile>
    </configuration>