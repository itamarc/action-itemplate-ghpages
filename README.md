# itamarc/action-itemplate-ghpages

:warning: :warning: :warning: **This description is still in development!** :warning: :warning: :warning:

Action to publish GitHub Pages using ITemplate

## Templates

The templates are in the format supported by [ITemplate](https://itamarc.github.io/itemplate/).

This project have some sample templates that can be used as a base.

The templates can use any of this keys:

| Key | Description
|-----|------------
| latestreleasetag | Tag associated with the latest release
| latestreleasedate | Date when the latest release was created
| ... | ...

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
    - uses: itamarc/action-itemplate-ghpages@v1
      with:
        # Needed to publish
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        # The branch where the templates are stored
        # (default: 'master' or 'main' if 'master' does not exist)
        templates_branch: master
        # The relative path to the folder that contains your site's templates
        templates_folder: docs/templates
        # The relative path to the folder that contains your site's snippets, if any
        snippets_folder: docs/templates/snippets
        # Branch name for storing github pages
        pages_branch: gh-pages
        # Name of the output folder where generated html will be stored.
        pages_folder: docs
```
