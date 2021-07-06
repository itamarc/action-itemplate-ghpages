# itamarc/action-itemplate-ghpages

:warning: :warning: :warning: **This description is still in development! See the [issues](https://github.com/itamarc/action-itemplate-ghpages/issues) to keep track of the progress.** :warning: :warning: :warning:

Action to publish GitHub Pages using ITemplate

## Configuration

The recomended configuration for your GitHub Pages:
- put your templates and your pages in a branch of their own (for example: `gh-pages`)
- point your pages to the `docs` folder in the settings
- put your templates in a folder outside the `docs` folder (for example: `templates`)
- if you want to use snippets, put them in a separate folder (for example: `snippets`)

To avoid the Jekyll processing of your GitHub Pages, you need to create an empty file named `.nojekyll` in the root of your pages folder.
If you followed the recomendations above, it will be in `docs/.nojekyll`.

## Templates

The templates are in the format supported by [ITemplate](https://itamarc.github.io/itemplate/).

If your template files have the sequence ".tmpl" in the name, it will be removed.
For example, if you have a template file called "index.tmpl.html", the resulting file will have the name "index.html".
Otherwise, the name is not changed.

This project have some **templates sets** that can be used.
To use them, in the `templates_folder` input, you put only the name of the
templates set you want to use enclosed by ":".

For example: to use the 'light' templates set, you need to set:

    templates_folder: ':light:'

The templates can use any of the keys in the next sections.

### From the GitHub API

Key | Description | Example
----|-------------|--------
repository_name | Repository name | myrepository
repository_nameWithOwner | Repository name with owner | mylogin/myrepository
repository_description | Description of the repository | Repository to test GitHub functionality.
repository_shortDescriptionHTML | The short description of the repository | Repository to test GitHub functionality.
repository_createdAt | Repository creation date | 2021-06-13T19:54:12Z
repository_updatedAt | Last time the repository was updated | 2021-07-04T02:17:15Z
repository_url | Repository's URL | https://github.com/mylogin/myrepository
repository_homepageUrl | If it's configured, the homepage URL for this repository | https://itamarc.github.io/githubtest/
repository_forkCount | How many times this repository was forked | 2
repository_stargazerCount | How many times the repository was starred? | 1000
repository_watchers_totalCount | How many users are whatching the repository | 1234
repository_repositoryTopics | The topics of the repository | [{"name":"java","url":"https://github.com/topics/java"},<br>{"name":"text","url":"https://github.com/topics/text"},<br>{"name":"templates","url":"https://github.com/topics/templates"},<br>{"name":"library","url":"https://github.com/topics/library"}]
repository_collaborators | Data of the colaborators of the repository | [{"name":"John Constantine","login":"mylogin","url":"https://github.com/mylogin"}]
repository_issues | Last 5 issues (max) with general data | [{"number":"7","createdAt":"2021-06-29T23:11:19Z","comments_totalCount":0},"author":{"login":"mylogin","url":"https://github.com/mylogin"},"titleHTML":"Support file format ABC","url":"https://github.com/mylogin/myrepository/issues/7"},<br>{"number":"8","createdAt":"2021-06-30T21:37:25Z","comments_totalCount":0},"author":{"login":"mylogin","url":"https://github.com/mylogin"},"titleHTML":"NullPointerException in class Aaaaa","url":"https://github.com/mylogin/myrepository/issues/8"},<br>{"number":"9","createdAt":"2021-07-01T03:37:28Z","comments_totalCount":0},"author":{"login":"mylogin","url":"https://github.com/mylogin"},"titleHTML":"Describe the API in the documentation","url":"https://github.com/mylogin/myrepository/issues/9"}]
repository_languages | Programming languages present in the repository with the respective sizes | [{"color":"#384d54","size":640,"name":"Dockerfile"},<br>{"color":"#b07219","size":18636,"name":"Java"},<br>{"color":"#89e051","size":661,"name":"Shell"}]
repository_languages_totalSize | Total size of the artifacts in the repository | 19937
repository_latestRelease_name | Name of the last release in the repository | New version with more features
repository_latestRelease_description | Description of the last release in the repository | This is version v2 of the software, with the new features X, Y and Z and with bug fixes #1, #2 and #3.
repository_latestRelease_author_login | Login of the author of the last release in the repository | mylogin
repository_latestRelease_author_name | Name of the author of the last release in the repository | John Constantine
repository_latestRelease_createdAt | Creation date of the last release in the repository | 2021-06-23T15:14:16Z
repository_latestRelease_isPrerelease | Is the last release in the repository a pre-release? | false
repository_latestRelease_tagName | Tag name of the last release in the repository | v1.2
repository_latestRelease_url | URL for the last release in the repository | https://github.com/itamarc/itemplate/releases/tag/v1.2
repository_licenseInfo_conditions | Conditions of the repository's licence | ["License and copyright notice","State changes","Disclose source","Same license"]
repository_licenseInfo_name | Name of the repository's licence | GNU General Public License v3.0
repository_licenseInfo_nickname | Nickname of the repository's licence | GNU GPLv3
repository_licenseInfo_url | URL for the repository's licence text | http://choosealicense.com/licenses/gpl-3.0/
repository_owner_avatarUrl | URL for the repository's owner avatar | https://avatars.githubusercontent.com/u/00010001?u=aaaabbbbcccc111122223333&v=4
repository_owner_login | Repository's owner login | ownerlogin
repository_owner_url | Repository's owner URL | https://github.com/mylogin

### Calculated

Key | Description | Example
----|-------------|--------
TMPL_LASTUPDATE | Date and time in ISO format of the action current run | 2021-06-27 18:02:03

### From the parameters

Those keys are the parameters you passed to the action, and you can find an utility for them in your templates.

Key | Description | Example
----|-------------|--------
INPUT_TEMPLATES_FOLDER | The folder where the templates are | templates
INPUT_ALLOW_TEMPLATES_SUBFOLDERS | Allow the templates to be stored in subfolders under templates_folder | 'false'
INPUT_SNIPPETS_FOLDER | The folder where the snippets are | snippets
INPUT_PAGES_BRANCH | The branch configured as the source for your GitHub Pages | gh-pages
INPUT_PAGES_FOLDER | The folder configured as the source for your GitHub Pages | docs
INPUT_TIMEZONE | The timezone that will be used to calculate TMPL_LASTUPDATE | America/Sao_Paulo
INPUT_PUBLISH_README_MD | Publish the README.md from the repository root in the generated page as README.html | 'true'

### From the environment

Those keys are mostly for the use of the action itself, but since it's there, maybe you can use them somehow.

Key | Description | Example
----|-------------|--------
GITHUB_WORKSPACE | The workspace where your code is, obtained with action/checkout | /github/workspace
GITHUB_EVENT_PATH | The path of the file with the complete webhook event payload. | /github/workflow/event.json
GITHUB_GRAPHQL_URL | The URL to the GitHub GraphQL API | https://api.github.com/graphql
GITHUB_SERVER_URL | The URL to the GitHub site | https://github.com
GITHUB_REPOSITORY_OWNER | The owner of the repository | itamarc
GITHUB_REPOSITORY | The owner and repository name. | itamarc/githubtest
GITHUB_ACTOR | The name of the person or app that initiated the workflow. | itamarc
GITHUB_REF | The branch or tag ref that triggered the workflow. | refs/heads/master

### Snippets

If the parameter SNIPPETS_FOLDER is set, the files in this folder will be
processed first and their filled content will be available using as key the
file name until the first character "." (dot) preceded by `SNP_`.

The snippets folder will not be recursive, all snippets must be in the same
folder, without subfolders.

Example: if there is in the snippets folder a file named `RELEASES.html`
(or `RELEASES.tmpl.html` or `RELEASES.snp.html`), it will be processed
and its content will be available when processing the templates with the
key `SNP_RELEASES`.

Snippets will not be saved in any way to the destination folder.

## Markdown

If you have a template or snippet with extension `.md`, it will:
1. Be filled using ITemplate
2. Be converted to HTML
3. Be saved in the destination with `.html` extension instead of `.md`

## GitHub Pages

GitHub Pages can be stored on any branch and in the root folder or in `/docs` folder.
This action supports all of these options.

## Action Use

To use the action, you need to create a workflow like the one below.
The checkout step is required to the action to have access to your templates
and to be able to commit back the changed pages.

```yaml
name: Page generation from templates

on:
  # Trigger on page_build, as well as release created events
  page_build:
  release:
    types:
      - created
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    with:
      # The branch where the templates are stored
      # (if not present, uses the 'master' branch)
      # (recomended: use the same branch configured to be used in GH Pages)
      ref: gh-pages
    - uses: itamarc/action-itemplate-ghpages@v1
      with:
        # The relative path to the folder that contains your site's templates
        # or the identification of the template set you want to use (required)
        templates_folder: templates
        # Allow the templates to be stored in subfolders under templates_folder (not required, default 'false').
        # The output folders tree will map the input.
        allow_templates_subfolders: 'false'
        # The relative path to the folder that contains your site's snippets, if any (not required)
        # If this is set and allow_templates_subfolders is true, can't be inside de templates tree.
        snippets_folder: snippets
        # Branch name for storing github pages (required)
        pages_branch: gh-pages
        # Name of the output folder where generated html will be stored (required)
        pages_folder: docs
        # Time zone to calculate the update time (required)
        # (default: America/Sao_Paulo, which is GMT-3 - sorry, I'm brazilian =) )
        timezone: America/Sao_Paulo
        # Publish the README.md from the repository root in the generated page as README.html (not required, default 'false')
        publish_readme_md: 'true'
      env:
        # Needed to publish
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

## Credits

(c) 2021 [Itamar Carvalho](https://github.com/itamarc)

This work uses the following packages and softwares:
- [`io.github.itamarc:itemplate`](https://github.com/itamarc/itemplate/) version 1.2
- [`org.json:json`](https://github.com/stleary/JSON-java) version 20210307
- [`org.apache.httpcomponents:httpclient`](https://hc.apache.org/) version 4.5.13
- [`com.vladsch.flexmark:flexmark-all`](https://github.com/vsch/flexmark-java/) version 0.62.2
- [Docker](https://www.docker.com/)
- [Apache Maven](https://maven.apache.org/)
- [GitHub GraphQL API](https://docs.github.com/en/graphql)
- [GitHub Actions](https://docs.github.com/en/actions)
- [bash](https://www.gnu.org/software/bash/)

## Licence

This project is under [GNU GPL v3.0](https://choosealicense.com/licenses/gpl-3.0/)
