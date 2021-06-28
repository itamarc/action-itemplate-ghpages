# itamarc/action-itemplate-ghpages

:warning: :warning: :warning: **This description is still in development!** :warning: :warning: :warning:

Action to publish GitHub Pages using ITemplate

## Templates

The templates are in the format supported by [ITemplate](https://itamarc.github.io/itemplate/).

If your template files have the sequence ".tmpl" in the name, it will be removed.
For example, if you have a template file called "index.tmpl.html", the resulting file will have the name "index.html".
Otherwise, the name is not changed.

This project have some sample templates that can be used as a base.

The templates can use any of the keys in the next sections.

### From the GitHub API

:warning: **This section only have sample fake data. TBD** :warning:

Key | Description | Example
----|-------------|--------
latestreleasetag | Tag associated with the latest release | 
latestreleasedate | Date when the latest release was created | 
... | ... | ...

### Calculated

Key | Description | Example
----|-------------|--------
TMPL_LASTUPDATE | Date and time in ISO format of the action current run | 2021-06-27 18:02:03

### From the parameters

Those keys are the parameters you passed to the action, and you can find an utility for them in your templates.

Key | Description | Example
----|-------------|--------
INPUT_TEMPLATES_FOLDER |  | docs/templates
INPUT_SNIPPETS_FOLDER |  | docs/templates/snippets
INPUT_PAGES_BRANCH |  | gh-pages
INPUT_PAGES_FOLDER |  | docs
INPUT_TIMEZONE |  | America/Sao_Paulo

### From the environment

Those keys are mostly for the use of the action itself, but since it's there, maybe you can use them somehow.

Key | Description | Example
----|-------------|--------
GITHUB_WORKSPACE |  | /github/workspace
GITHUB_EVENT_PATH |  | /github/workflow/event.json
GITHUB_GRAPHQL_URL |  | https://api.github.com/graphql
GITHUB_SERVER_URL |  | https://github.com
GITHUB_REPOSITORY_OWNER |  | itamarc
GITHUB_REPOSITORY |  | itamarc/githubtest
GITHUB_ACTOR |  | itamarc
GITHUB_REF |  | refs/heads/master

### Snippets

If the parameter SNIPPETS_FOLDER is set, the files in this folder will be
processed first and their filled content will be available using as key the
file name until the first character "." (dot) preceded by `SNP_`.

Example: if there is in the snippets folder a file named `RELEASES.html`, it
will be processed and its content will be available when processing the
templates with the key `SNP_RELEASES`.

Snippets will not be saved in any way to the destination folder.

## GitHub Pages

GitHub Pages can be stored on any branch and in the root folder or in `/docs` folder.
This action supports all of these options.

## Action Use

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
      # (if not present, uses the default branch)
      ref: gh-pages
    - uses: itamarc/action-itemplate-ghpages@v1
      with:
        # The relative path to the folder that contains your site's templates (required)
        templates_folder: docs/templates
        # The relative path to the folder that contains your site's snippets, if any (not required)
        snippets_folder: docs/templates/snippets
        # Branch name for storing github pages (required)
        pages_branch: gh-pages
        # Name of the output folder where generated html will be stored (required)
        pages_folder: docs
        # Time zone to calculate the update time (required)
        # (default: America/Sao_Paulo, which is GMT-3 - sorry, I'm brazilian =) )
        timezone: America/Sao_Paulo
      env:
        # Needed to publish
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```
