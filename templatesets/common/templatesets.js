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
            const lang = languages[i];
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
            const topic = topics[i];
            topicsDiv += '<li><a href="' + topic.url + '">' + topic.name + '</a>'
        }
        topicsDiv += "\n</ul>"
        document.getElementById("topics").innerHTML = topicsDiv
    }
}
