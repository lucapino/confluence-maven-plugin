<h1>Maven Confluence Plugin</h1><br>

Maven plugin for accessing Atlassian Confluence

[![][Build Status img]][Build Status]
[![][Coverage Status img]][Coverage Status]
[![][Dependency Status img]][Dependency Status]
[![][license img]][license]
[![][Maven Central img]][Maven Central]
[![][Javadocs img]][Javadocs]

Plugin documentation can be found at https://lucapino.github.io/confluence-maven-plugin

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
        <version>2.0.1</version>
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

[Build Status]:https://travis-ci.org/lucapino/confluence-maven-plugin
[Build Status img]:https://travis-ci.org/lucapino/confluence-maven-plugin.svg?branch=master

[Coverage Status]:https://codecov.io/gh/lucapino/confluence-maven-plugin
[Coverage Status img]:https://codecov.io/gh/lucapino/confluence-maven-plugin/branch/master/graph/badge.svg

[Dependency Status]:https://snyk.io/test/github/lucapino/confluence-maven-plugin
[Dependency Status img]:https://snyk.io/test/github/lucapino/confluence-maven-plugin/badge.svg?style=flat

[license]:LICENSE
[license img]:https://img.shields.io/badge/license-Apache%202-blue.svg

[Maven Central]:https://maven-badges.herokuapp.com/maven-central/com.github.lucapino/confluence-maven-plugin
[Maven Central img]:https://maven-badges.herokuapp.com/maven-central/com.github.lucapino/confluence-maven-plugin/badge.svg

[Javadocs]:http://www.javadoc.io/doc/com.github.lucapino/confluence-maven-plugin
[Javadocs img]:http://javadoc.io/badge/com.github.lucapino/confluence-maven-plugin.svg
