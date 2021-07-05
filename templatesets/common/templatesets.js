// repository_languages
// repository_languages_totalSize
function languagesAsUL(languages, langTotalSize) {
    if (languages.length != 0) {
        let langDiv = ""
        if (languages.length > 1) {
            langDiv += "This repository have code written in these languages:<br><ul>\n"
        } else {
            langDiv += "This repository have code written in:<br><ul>\n"
        }
        for (let i = 0; i < languages.length; i++) {
            const lang = languages[i]
            langDiv += "<li>" + lang.name
            if (languages.length > 1) {
                langDiv += " (" + Math.round(lang.size/langTotalSize*1000)/10 + "%)"
            }
        }
        langDiv += "\n</ul>"
        document.getElementById("languages").innerHTML = langDiv
    }
}

// repository_repositoryTopics
function topicsAsUL(topics) {
    if (topics.length != 0) {
        let topicsDiv = ""
        if (topics.length > 1) {
            topicsDiv += "Topics:<br><ul>\n"
        } else {
            topicsDiv += "Topic:<br><ul>\n"
        }
        for (let i = 0; i < topics.length; i++) {
            const topic = topics[i]
            topicsDiv += '<li><a href="' + topic.url + '">' + topic.name + '</a>'
        }
        topicsDiv += "\n</ul>"
        document.getElementById("topics").innerHTML = topicsDiv
    }
}

// repository_issues
// [{
//  "createdAt":"2021-07-03T13:36:19Z",
//  "comments":{"totalCount":0},
//  "author":{
//      "login":"itamarc",
//      "url":"https://github.com/itamarc"
//      },
//  "number": 4,
//  "titleHTML":"Test issue 1 with enhancement",
//  "url":"https://github.com/itamarc/githubtest/issues/4"
//  },
// {"createdAt":"2021-07-03T13:37:31Z","comments":{"totalCount":0},"author":{"login":"itamarc","url":"https://github.com/itamarc"},
//  "number": 5, "titleHTML":"Test issue 2 with a bug","url":"https://github.com/itamarc/githubtest/issues/5"}]
function issuesAsSPAN(issues) {
    if (issues.length != 0) {
        let issuesDiv = ""
        issuesDiv += "<h3>Last open issues</h3>\n"
        for (let i = 0; i < issues.length; i++) {
            const issue = issues[i]
            issuesDiv += '<span class="issueIdSpan">Issue: <a href="' + issue.url + '">#' + issue.number + "</a> - " + issue.titleHTML + '</span>\n'
            issuesDiv += '<span class="issueCreatedAt">Created at: ' + issue.createdAt + '</span>\n'
            issuesDiv += '<span class="issueAuthor">Author: <a href="' + issue.author.url + '">' + issue.author.login + '</a></span>\n'
            issuesDiv += '<span class="issuesCommentsCount">Comments count: ' + issue.comments_totalCount + '</span>\n'
        }
        document.getElementById("issues").innerHTML = issuesDiv
    }
}

// repository_collaborators
// [{"name":"Itamar Carvalho","login":"itamarc","url":"https://github.com/itamarc"}]
function collaboratorsAsUL(collabs) {
    if (collabs.length != 0) {
        let collabDiv = "<h2>Collaborators</h2>\n<UL>\n"
        for (let i = 0; i < collabs.length; i++) {
            const coll = collabs[i]
            collabDiv += '<LI>' + coll.name + ' (<a href="' + coll.url + '">' + coll.login + '</a>)</span>\n'
        }
        collabDiv += "</UL>\n"
        document.getElementById("collaborators").innerHTML = collabDiv
    }
}
