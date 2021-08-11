// repository_languages
// repository_languages_totalSize
function languagesAsUL(languages, langTotalSize) {
    if (languages.length != 0) {
        // sort languages array by size
        languages.sort(function (a, b) {
            return b.size - a.size
        })
        let langDiv = ""
        if (languages.length > 1) {
            let langBar = '<div class="languagesBar">'
            langDiv += "<ul>\n"
            for (let i = 0; i < languages.length; i++) {
                const lang = languages[i]
                langDiv += '<li style="color: ' + lang.color + '; list-style: square;"><span class="languageName">' + lang.name
                perc = Math.round(lang.size/langTotalSize*1000)/10
                langDiv += " (" + perc + "%)</span>\n"
                langBar += '<span style="background-color: '+lang.color+'; width: ' + perc + '%;"></span>'
            }
            langDiv += "</ul>\n"
            langDiv = "<h3>Languages</h3><br>\n" + langBar + "</div>\n" + langDiv
        } else {
            langDiv += "<h3>Language</h3><br>\n<ul>\n"
            langDiv += "<li>" + languages[0].name
            langDiv += "</ul>\n"
        }
        document.getElementById("languages").innerHTML = langDiv
    }
}

// repository_repositoryTopics
function topicsAsSPAN(topics) {
    if (topics.length != 0) {
        let topicsDiv = ""
        if (topics.length > 1) {
            topicsDiv += '<h3>Topics</h3><br><div class="topics">\n'
        } else {
            topicsDiv += '<h3>Topic</h3><br><div class="topics">\n'
        }
        for (let i = 0; i < topics.length; i++) {
            const topic = topics[i]
            topicsDiv += '<span><a href="' + topic.url + '">' + topic.name + '</a></span>'
        }
        topicsDiv += "\n</div>"
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
            issuesDiv += '<span class="issuesCommentsCount">Comments: ' + issue.comments_totalCount + '</span>\n'
            issuesDiv += '</div>\n'
        }
        document.getElementById("issues").innerHTML = issuesDiv
    } else {
        document.getElementById("issues").hidden = true
    }
}

function latestReleaseAsUL(name, description, tagName, createdAt, isPrerelease, url, author_login, author_name) {
    if (createdAt) {
        let latRelDiv = "<h3>"
        if (name == "") { // if it's a tag
            latRelDiv += "Latest tag</h3>\n<ul>\n"
            latRelDiv += '<li>Tag: <a href="' + url + '">' + tagName + '</a>\n'
        } else { // it's a release
            latRelDiv += "Latest release</h3>\n<ul>\n"
            latRelDiv += '<li>Name: <a href="' + url + '">' + name + '</a>\n'
            latRelDiv += '<li>Description: '+description+'\n'
            latRelDiv += '<li>Tag: '+ tagName +'\n'
        }
        latRelDiv += '<li>Author: '+author_name + ' (' + author_login + ')\n'
        latRelDiv += '<li>Created at: '+ createdAt +'\n'
        if (isPrerelease == 'true') {
            latRelDiv += '<li>Pre-release\n'
        }
        latRelDiv += '</ul>\n'
        document.getElementById("latestRelease").innerHTML = latRelDiv
    } else {
        document.getElementById("latestRelease").hidden = true
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

function readmePublished(publishReadme) {
    if (publishReadme == 'true') {
        document.getElementById("readme").innerHTML = 'For more information, see the <a href="README.html">README</a>.'
    } else {
        document.getElementById("readme").hidden = true
    }
    if (publishReadme != 'inline') {
        document.getElementById("readme_inline").hidden = true
    }
}
