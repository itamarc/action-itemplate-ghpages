# GitHub Action: action-itemplate-ghpages

:warning: :warning: :warning: **This description is still in development! See the [issues](https://github.com/itamarc/action-itemplate-ghpages/issues) to keep track of the progress.** :warning: :warning: :warning:

This action can automatically publish auto-filled GitHub Pages using basic data
from your repository.

It can be used with some pre-build themes or you can customize the generated
pages with your own custom templates (using
[ITemplate](https://itamarc.github.io/itemplate/)).

## GitHub Pages

GitHub Pages can be stored on any branch and in the root folder or in `/docs` folder.
This action supports all of these options, but it is recommended to use the
`gh-pages` branch and `/docs` folder.

## Simple usage: themes

This action have some pre-build **themes** that can be used. This is the simplest way to use it.

To use a theme, in the `templates_folder` input (see the section **Workflow**
below), you put only the name of the theme you want to use enclosed by ":".

For example: to use the 'light' theme, you need to set:

    templates_folder: ':light:'

Available themes are:
- `:reference:`
- `:light:`
- `:dark:`

### Configuration

The recomended configuration for your GitHub Pages:
- create a branch named `gh-pages` for your pages
- configure your pages in the `gh-pages` branch (Repository Settings > Pages > Source)
- point your pages to the `docs` folder in the settings

To avoid the Jekyll processing of your GitHub Pages, you need to create an
empty file named `.nojekyll` in the root of your pages folder.
If you followed the recomendations above, it needs to be in `docs/.nojekyll` at
the `gh-pages` branch.

### Workflow

You need to create a workflow in your repository Actions to use this action,
like this one:

```yaml
name: Automatic GitHub Pages generation

on:
  # Trigger on page_build, as well as release created events
  page_build:
  release:
    types:
      - created
jobs:
  deploy-pages:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
      with:
        # The branch where the templates are stored
        # (if not present, uses the 'master' branch)
        # (recomended: use the same branch configured to be used in GH Pages)
        ref: 'gh-pages'
    - uses: itamarc/action-itemplate-ghpages@v1
      with:
        # The relative path to the folder that contains your site's templates
        # or the identification of the theme you want to use (required)
        templates_folder: ':light:'
        # Branch name for storing github pages (required)
        pages_branch: 'gh-pages'
        # Name of the output folder where generated html will be stored (required)
        pages_folder: 'docs'
        # Time zone to calculate the update time (required)
        # (default: America/Sao_Paulo, which is GMT-3 - sorry, I'm brazilian =) )
        timezone: 'America/Sao_Paulo'
        # Publish the README.md from the repository root in the generated page
        # as README.html (not required, default 'false')
        # The themes will automatically insert a link to the README.html file
        # in the index.html file if this option is set to 'true'.
        # If this option is set to 'inline', the README.html file will be
        # included in the generated index.html file.
        #publish_readme_md: 'true'
        # Folders or files to copy when publishing, keeping the relative path,
        # separated by spaces (not required, default '' - meaning none).
        # These files will be copied to the output folder without any change.
        #content_to_copy: ''
      env:
        # Needed to publish the pages
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

### Optional features

If you want to publish your `README.md` file, you need to enable the
`publish_readme_md` option. If set to 'true', the action will publish the
`README.md` file from the repository root in the generated page as
`README.html` and put a link to it in the generated page.
If set to 'inline', the `README.md` file will be included in the
generated `index.html` file.

If you want to publish some additional content, you need to add them to the
`content_to_copy` option. This can be used, for example, to copy images
referenced in the `README.md` file.

You can also want to automatically copy the `README.md` file from the `master`
branch to the `gh-pages` branch. To do that, see the **Tips** section below.

## Advanced usage: custom templates

You can create your own templates and use them with this action.

*Suggestion:* you can copy a pre-built theme you want to use and modify it.
In the action repository, you can find the pre-built themes in the `themes`
folder. Look at the folder with the name of the theme you want to use and also
the files in `common` folder.

### Configuration

The recomended configuration for your GitHub Pages:
- put your templates and your pages in a branch of their own (for example: `gh-pages`)
- point your pages to the `docs` folder in the settings
- put your templates in a folder outside the `docs` folder (for example: `templates`)
- if you want to use snippets, put them in a separate folder (for example: `snippets`)

To avoid the Jekyll processing of your GitHub Pages, you need to create an empty file named `.nojekyll` in the root of your pages folder.
If you followed the recomendations above, it will be in `docs/.nojekyll`.

### Workflow

To use the action, you need to create a workflow like the one below.
The checkout step is required to the action to have access to your templates
and to be able to commit back the changed pages.

```yaml
name: Automatic GitHub Pages generation

on:
  # Trigger on page_build, as well as release created events
  page_build:
  release:
    types:
      - created
jobs:
  deploy-pages:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
      with:
        # The branch where the templates are stored
        # (if not present, uses the 'master' branch)
        # (recomended: use the same branch configured to be used in GH Pages)
        ref: 'gh-pages'
    - uses: itamarc/action-itemplate-ghpages@v1
      with:
        # The relative path to the folder that contains your site's templates
        # or the identification of the theme you want to use (required)
        templates_folder: 'templates'
        # Allow the templates to be stored in subfolders under templates_folder (not required, default 'false').
        # The output folders tree will map the input.
        allow_templates_subfolders: 'false'
        # The relative path to the folder that contains your site's snippets, if any (not required)
        # If this is set and allow_templates_subfolders is true, can't be inside de templates tree.
        snippets_folder: 'snippets'
        # Branch name for storing github pages (required)
        pages_branch: 'gh-pages'
        # Name of the output folder where generated html will be stored (required)
        pages_folder: 'docs'
        # Time zone to calculate the update time (required)
        # (default: America/Sao_Paulo, which is GMT-3 - sorry, I'm brazilian =) )
        timezone: 'America/Sao_Paulo'
        # Publish the README.md from the repository root in the generated page
        # as README.html (not required, default 'false')
        # The themes will automatically insert a link to the README.html file
        # in the index.html file if this option is set to 'true'.
        # If this option is set to 'inline', the README.html file will be
        # included in the generated index.html file.
        publish_readme_md: 'true'
        # Folders or files to copy when publishing, keeping the relative path, separated by spaces (not required, default '' - meaning none).
        # These files will be copied to the output folder without any change.
        content_to_copy: ''
        # The log level, according to the ones defined in java.util.logging.Level. (not required, default 'WARNING')
        log_level: 'INFO'
      env:
        # Needed to publish the pages
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

### Template files

The templates are in the format supported by [ITemplate](https://itamarc.github.io/itemplate/).

If your template files have the sequence ".tmpl" in the name, it will be removed.
For example, if you have a template file called `index.tmpl.html`, the
resulting file will have the name `index.html`.
Otherwise, the name is not changed.

#### Snippets

If the parameter `snippets_foler` is set, the files in this folder will be
processed first and their filled content will be available to the templates using as 
key the file name until the first character "." (dot) preceded by `SNP_`.

Example: if there is in the snippets folder a file named `RELEASES.html`
(or `RELEASES.tmpl.html` or `RELEASES.snp.html`), it will be processed
and its content will be available when processing the templates with the
key `SNP_RELEASES`.

The snippets folder will not be recursive, all snippets must be in the same
folder, without subfolders.

Snippets will not be saved in any way to the destination folder.

#### Markdown

If you have a template or snippet with extension `.md`, it will:
1. Be filled using ITemplate
2. Be converted to HTML
3. If it's a template, be saved in the destination with `.html` extension instead of `.md`

If it's a template, the resulting HTML file will be completed with header and footer
(the footer will be only `</body></html>`).

If you're using a built-in theme, it will use the theme's CSS.

If you're using a custom set of templates and you want a custom header for your
Markdown files (for example, to define a specific CSS), create a snippet with
name `MARKDOWN_HEADER` (it will be used internally using the key
`SNP_MARKDOWN_HEADER` and will be available to be used in the templates).
Include in this file everything that you want including a `<body>` tag.

### Publishing the `README.md`

You can export the `README.md` file from **your GitHub Pages branch** (be
careful to don't edit only the one in `master` or `main` branch, unless this
is the source for your Pages - ***not recommended***) using the action
parameter `publish_readme_md`.

This parameter can have the following values:
- `true`: the `README.md` will be converted and published as a separate file `README.html`.
  If you're using a built-in theme, the generated page will have a link to it.
- `inline`: the `README.md` will be exported available as a snippet with key `SNP_README`.
  If you're using a built-in theme, the content will be included in the generated page.
- `false`: the `README.md` will not be published

If set to `true`, it will be completed with header and footer as described in
**Markdown** section. If you want a custom header, you can use the
`MARKDOWN_HEADER` snippet or an specific one with the name `README_HEADER`
(this last one will have precedence if it exists).

To automatically copy the `README.md` file from the `master` branch to your
pages branch, you can use the `planetoftheweb/copy-to-branches` action.
See the **Tips** section below for details.

### Publishing additional content

If you set the `content_to_copy` parameter, the files or folders specified
will be copied when publishing, keeping the relative path, without change.

This input parameter needs to have the relative path to the files or folders
you want to copy separated by spaces.

For example, if you want to copy the `images` folder and the `help.html` file
from the `www` folder, you can set the parameter as:

    content_to_copy: 'images www/help.html'

### Available data

The templates and the snippets can use any of the keys in the next sections.

#### From the GitHub API

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
repository_latestRelease_description | Description of the last release in the repository. If the description have multiple lines, the lines will be separated by `<br>` and put in a single line. | This is version v2 of the software, with the new features X, Y and Z and with bug fixes #1, #2 and #3.
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

#### Calculated

Key | Description | Example
----|-------------|--------
TMPL_LASTUPDATE | Date and time in ISO format of the action current run | 2021-06-27 18:02:03

#### From the parameters

Those keys are the parameters you passed to the action, and you can find an utility for them in your templates.

Key | Description | Example
----|-------------|--------
INPUT_TEMPLATES_FOLDER | The folder where the templates are or the theme id | 'templates'
INPUT_ALLOW_TEMPLATES_SUBFOLDERS | Allow the templates to be stored in subfolders under templates_folder | 'false'
INPUT_SNIPPETS_FOLDER | The folder where the snippets are | 'snippets'
INPUT_PAGES_BRANCH | The branch configured as the source for your GitHub Pages | 'gh-pages'
INPUT_PAGES_FOLDER | The folder configured as the source for your GitHub Pages | 'docs'
INPUT_TIMEZONE | The timezone that will be used to calculate TMPL_LASTUPDATE | 'America/Sao_Paulo'
INPUT_PUBLISH_README_MD | Publish the README.md from the repository root in the generated page as README.html | 'true'
INPUT_CONTENT_TO_COPY | List of content (files or folders) to copy from the repository to the generated page, separated by commas | 'images,LICENSE.txt'
INPUT_LOG_LEVEL | The log level that will be used to log the action execution | 'INFO'

#### From the environment

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

## Tips

* If you're maintaing your Pages in the 'gh-pages' branch, you can use the
[planetoftheweb/copy-to-branches](https://github.com/marketplace/actions/copy-to-branches-action)
action to copy your `README.md` and other files from `master` to the `gh-pages`
branch before generating the pages. To do so, you can add the following job to
your workflow before the job that generates the pages:

```yaml
  update-docs-on-gh-pages-branch:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
        with:
          # Adjust the depth as needed according to the files you want to copy
          fetch-depth: 0
      - name: Copy To Branches Action
        uses: planetoftheweb/copy-to-branches@v1.1
        env:
          # The branch where the files will be copied from
          key: 'master'
          # The destination branch
          branches: 'gh-pages'
          # The files that will be copied with the relative path
          files: 'README.md'
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
- [git](https://git-scm.com/)

## Licence

This project is under [GNU GPL v3.0](https://choosealicense.com/licenses/gpl-3.0/)
