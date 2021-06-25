# GitHub GraphQL API How To

(documenting my path to understand how to use this API)

To make queries I'll use the Explorer:

https://docs.github.com/en/graphql/overview/explorer

To get the first level of the schema using GraphQL Introspection:

```graphql
{
  __schema {
    types {
      name
    }
  }
}
```

To get the fields of the Repository object:

```graphql
{
  __type(name: "Repository") {
    name
    description
    kind
    fields {
      name
      type {
        name
        kind
      }
    }
  }
}
```

To find the latest release in the repository:

```graphql
{
  repository (owner: "itamarc", name: "itemplate") {
    name
    createdAt
    releases (last: 1) {
      nodes {
        createdAt
        tagName
        isLatest
        isPrerelease
        url
        author {
          name
          login
        }
      }
    }
  }
}
```

The result of this query is:

```graphql
{
  "data": {
    "repository": {
      "name": "itemplate",
      "createdAt": "2021-05-04T00:42:29Z",
      "releases": {
        "nodes": [
          {
            "createdAt": "2021-06-23T15:14:16Z",
            "tagName": "v1.2",
            "isLatest": true,
            "isPrerelease": false,
            "url": "https://github.com/itamarc/itemplate/releases/tag/v1.2",
            "author": {
              "name": "Itamar Carvalho",
              "login": "itamarc"
            }
          }
        ]
      }
    }
  }
}
```

An almost complete query with the data we need from a repository:

```graphql
{
  repository(owner: "itamarc", name: "itemplate") {
    name
    createdAt
    issues(last: 5, filterBy: {states: OPEN}) {
      nodes {
        id
        titleHTML
        url
        lastEditedAt
        createdAt
        comments {
          totalCount
        }
        author {
          login
          url
        }
      }
    }
    licenseInfo {
      name
      nickname
      url
      conditions {
        label
      }
    }
    latestRelease {
      createdAt
      tagName
      isPrerelease
      url
      author {
        name
        login
      }
    }
    collaborators(first: 100) {
      nodes {
        login
        url
        name
      }
    }
    languages(last: 100) {
      edges {
        node {
          color
          name
        }
        size
      }
      totalSize
    }
    nameWithOwner
    owner {
      login
      avatarUrl
      url
    }
    stargazerCount
    url
    watchers {
      totalCount
    }
  }
}
```

The result of the query above:

```graphql
{
  "data": {
    "repository": {
      "name": "itemplate",
      "createdAt": "2021-05-04T00:42:29Z",
      "issues": {
        "nodes": []
      },
      "licenseInfo": {
        "name": "GNU Lesser General Public License v3.0",
        "nickname": "GNU LGPLv3",
        "url": "http://choosealicense.com/licenses/lgpl-3.0/",
        "conditions": [
          {
            "label": "License and copyright notice"
          },
          {
            "label": "Disclose source"
          },
          {
            "label": "State changes"
          },
          {
            "label": "Same license (library)"
          }
        ]
      },
      "latestRelease": {
        "createdAt": "2021-06-23T15:14:16Z",
        "tagName": "v1.2",
        "isPrerelease": false,
        "url": "https://github.com/itamarc/itemplate/releases/tag/v1.2",
        "author": {
          "name": "Itamar Carvalho",
          "login": "itamarc"
        }
      },
      "collaborators": {
        "nodes": [
          {
            "login": "itamarc",
            "url": "https://github.com/itamarc",
            "name": "Itamar Carvalho"
          }
        ]
      },
      "languages": {
        "edges": [
          {
            "node": {
              "color": "#b07219",
              "name": "Java"
            },
            "size": 21140
          },
          {
            "node": {
              "color": "#427819",
              "name": "Makefile"
            },
            "size": 145
          },
          {
            "node": {
              "color": "#e34c26",
              "name": "HTML"
            },
            "size": 2333
          }
        ],
        "totalSize": 23618
      },
      "nameWithOwner": "itamarc/itemplate",
      "owner": {
        "login": "itamarc",
        "avatarUrl": "https://avatars.githubusercontent.com/u/19577272?u=2bf4a3411aae650b4a5ac645845ae87ddbaad593&v=4",
        "url": "https://github.com/itamarc"
      },
      "stargazerCount": 1,
      "url": "https://github.com/itamarc/itemplate",
      "watchers": {
        "totalCount": 1
      }
    }
  }
}
```
