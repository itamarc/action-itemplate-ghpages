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
                langDiv += " (" + Math.round(lang.size/langTotalSize*1000)/10 + "%)\n"
            }
        }
        langDiv += "</ul>\n"
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
            issuesDiv += '<div class="issue">\n'
            issuesDiv += '<span class="issueIdSpan">Issue: <a href="' + issue.url + '">#' + issue.number + "</a> - " + issue.titleHTML + '</span>\n'
            issuesDiv += '<span class="issueCreatedAt">Created at: ' + issue.createdAt + '</span>\n'
            issuesDiv += '<span class="issueAuthor">Author: <a href="' + issue.author.url + '">' + issue.author.login + '</a></span>\n'
            issuesDiv += '<span class="issuesCommentsCount">Comments count: ' + issue.comments_totalCount + '</span>\n'
            issuesDiv += '</div>\n'
        }
        document.getElementById("issues").innerHTML = issuesDiv
    }
}

function latestReleaseAsUL(name, description, tagName, createdAt, isPrerelease, url, author_login, author_name) {
    if (createdAt) {
        let latRelDiv = "<h3>Latest release</h3>\n<ul>\n"
        latRelDiv += '<li>Name: <a href="' + url + '">' + name + '</a>\n'
        latRelDiv += '<li>Description: '+description+'\n'
        latRelDiv += '<li>Author: '+author_name + ' (' + author_login + ')\n'
        latRelDiv += '<li>Created at: '+ createdAt +'\n'
        latRelDiv += '<li>Tag: '+ tagName +'\n'
        if (isPrerelease == 'true') {
            latRelDiv += '<li>Pre-release\n'
        }
        latRelDiv += '</ul>\n'
        document.getElementById("latestRelease").innerHTML = latRelDiv
    }
}

// repository_collaborators
// [{"name":"Itamar Carvalho","login":"itamarc","url":"https://github.com/itamarc"}]
function collaboratorsAsUL(collabs) {
    if (collabs.length != 0) {
        let collabDiv = "<h2>Collaborators</h2>\n<UL>\n"
        for (let i = 0; i < collabs.length; i++) {
            const coll = collabs[i]
            collabDiv += '<LI>' + coll.name + ' (<a href="' + coll.url + '">' + coll.login + '</a>)\n'
        }
        collabDiv += "</UL>\n"
        document.getElementById("collaborators").innerHTML = collabDiv
    }
}

// repository_licenseInfo_conditions
// ["License and copyright notice","State changes","Disclose source","Same license"]
function licenseCondAsUL(conditions) {
    if (conditions.length != 0) {
        let licCondUl = "<h3>License conditions:</h3>\n<UL>\n"
        for (let i = 0; i < conditions.length; i++) {
            licCondUl += '<LI>' + conditions[i] + '\n'
        }
        licCondUl += "</UL>\n"
        document.getElementById("licenceConditions").innerHTML = licCondUl
    }
}

function readmeAsP(publishReadme) {
    if (publishReadme == 'true') {
        document.getElementById("readme").innerHTML = 'For more information, see the <a href="README.html">README</a>.'
    } else {
        document.getElementById("readme").hidden = true
    }
}
