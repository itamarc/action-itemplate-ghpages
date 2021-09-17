package io.github.itamarc.tmplpages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gjt.itemplate.ITemplate;

public class TemplateProcessor {
    static String templatesPath = null;
    static String destinationPath = null;
    static String destRootPath = null;
    static String snippetsPath = null;
    static boolean allowSubfolders = false;
    static String githubWkSpc = null;
    static boolean themesOn = false;
    private boolean publishReadme = false;
    private boolean readmeInline = false;
    private boolean syntaxHighlightEnabled = false;
    private String syntaxHighlightTheme = "default";
    private String[] contentToCopy = {};
    private boolean convertMdToHtml = false;
    private MarkdownProcessor mdProcessor = null;
    private static String THEMES_PATH = "/opt/action-itemplate-ghpages/themes";

    /**
     * Class responsible to make the processing of the templates.
     * 
     * @param ghWkSpc GitHub Workspace - where the repository code is
     * @param tmplPath Path to the folder containing the templates
     * @param destPath Path to the folder where the generated pages will reside
     * @param allowSubdirs Allow the templates to be stored in subdirs of tmplPath
     * @param enableSyntaxHighlight Enable syntax highlighting of converted Markdown
     */
    public TemplateProcessor(String ghWkSpc, String tmplPath, String destPath, boolean allowSubdirs, boolean enableSyntaxHighlight) {
        mdProcessor = new MarkdownProcessor();
        githubWkSpc = ghWkSpc;
        templatesPath = tmplPath;
        destinationPath = destPath;
        allowSubfolders = allowSubdirs;
        syntaxHighlightEnabled = enableSyntaxHighlight;
        // If the templatesPath is in the form ":something:", then it is a theme name
        themesOn = templatesPath.toLowerCase().matches("^:\\p{Lower}+:$");
        if (themesOn) {
            templatesPath = templatesPath.toLowerCase().substring(1, templatesPath.length() - 1);
            allowSubfolders = false;
            ActionLogger.info("TemplateProcessor created - using theme: '" + templatesPath + "'");
        }
    }

    public int run(HashMap<String, String> valuesMap) {
        if (valuesMap.containsKey("THEMES_PATH")) {
            // This code only exists to allow me to test this class without changing the constant.
            // I know that this should not be hard coded, but I don't have time to change it now.
            THEMES_PATH = valuesMap.get("THEMES_PATH");
        }
        if (themesOn) {
            valuesMap.put("THEME", templatesPath);
        }
        // Process syntax highlighting snippets first
        processSyntaxHighlighting(valuesMap);
        ActionLogger.info("Processing snippets.");
        processSnippets(valuesMap);
        String tmplFullPath = getTmplFullPath();
        if (themesOn) {
            ActionLogger.info("Processing templates with theme '" + templatesPath + "'.");
            copyCommonFiles();
        } else {
            ActionLogger.info("Processing templates from: " + tmplFullPath);
        }
        if (publishReadme) {
            ActionLogger.fine("Publish readme TRUE!");
            publishReadmeMdFile(valuesMap);
        }
        if (contentToCopy.length > 0) {
            ActionLogger.info("Copying content: " + Arrays.toString(contentToCopy));
            copyContent(valuesMap);
        }
        if (themesOn && syntaxHighlightEnabled && !readmeInline) {
            // When using built-in themes, if the readme is not inline, but the
            // syntax highlighting is enabled, remove the snippets that insert
            // the syntax highlighting JS and CSS in the index, to avoid
            // loading unncessary heavy JS/CSS.
            cleanSyntaxHighlightSnippets(valuesMap);
        }
        return processTmplFolder(tmplFullPath, valuesMap);
    }
    
    /**
     * Copy the common files to the destination folder.
     * Only valid when using a theme.
     */
    private void copyCommonFiles() {
        ActionLogger.info("Copying common theme files.");
        try {
            String commonDirPath = THEMES_PATH + File.separator + "common";
            copyFilesInDir(commonDirPath, getDestRootPath());
        } catch (IOException e) {
            ActionLogger.severe(e.getMessage(), e);
        }
    }
    
    private void copyFilesInDir(String originDirPath, String destDirPath) throws IOException {
        copyFilesInDir(originDirPath, destDirPath, false, null);
    }

    private void copyFilesInDir(String originDirPath, String destDirPath, boolean convertMdFiles, HashMap<String, String> valuesMap)
    throws IOException {
        ActionLogger.fine("Copying files from " + originDirPath + " to " + destDirPath);
        assureDestinationExists(destDirPath);
        List<String> files = listFilesInDir(new File(originDirPath));
        for (String fileName : files) {
            if (convertMdFiles && fileName.endsWith(".md")) {
                ActionLogger.finer("Converting Markdown file to HTML: " + fileName);
                // Convert Markdown files to HTML
                String mdString = readTextFile(originDirPath + File.separator + fileName);
                String htmlString = convertMarkdownToHtml(fileName, mdString, valuesMap);
                writeFile(htmlString, destDirPath + File.separator + fileName.replace(".md", ".html"));
            } else {
                Path from = Paths.get(originDirPath + File.separator + fileName);
                Path dest = Paths.get(destDirPath + File.separator + fileName);
                Files.copy(from, dest, StandardCopyOption.REPLACE_EXISTING);
                ActionLogger.finer("File '" + from.toString() + "' copied to '"+dest.toString() + "'");
            }
        }
    }

    private void publishReadmeMdFile(HashMap<String, String> valuesMap) {
        ActionLogger.info("Trying to publish README.md file.");
        // Check if there is a README.md
        String readmePath = githubWkSpc + File.separator + "README.md";
        File readmeFile = new File(readmePath);
        ActionLogger.fine("Checking if README.md exists at '" + readmePath + "': " + readmeFile.exists());
        if (readmeFile.exists()) {
            try {
                // Get the file and convert to HTML
                String readmeMd = readTextFile(readmePath);
                if (convertMdToHtml) {
                    readmeMd = convertMdLinks(readmeMd);
                }
                String readmeHtml = mdProcessor.processMarkdown(readmeMd);
                if (readmeInline) {
                    // Save as snippet
                    valuesMap.put("SNP_README", readmeHtml);
                    ActionLogger.info("Readme set to be inline, available as snippet with key 'SNP_README'");
                } else {
                    // Complete the HTML
                    String readmeHeader = """
                    <html><head>
                    <title>README.md</title>
                    </head><body>
                    """;
                    if (valuesMap.containsKey("SNP_README_HEADER")) {
                        readmeHeader = valuesMap.get("SNP_README_HEADER");
                    } else if (valuesMap.containsKey("SNP_MARKDOWN_HEADER")) {
                        readmeHeader = valuesMap.get("SNP_MARKDOWN_HEADER");
                    }
                    String readmeFooter = """
                    </body>
                    </html>
                    """;
                    if (valuesMap.containsKey("SNP_README_FOOTER")) {
                        readmeFooter = valuesMap.get("SNP_README_FOOTER");
                    } else if (valuesMap.containsKey("SNP_MARKDOWN_FOOTER")) {
                        readmeFooter = valuesMap.get("SNP_MARKDOWN_FOOTER");
                    }
                    readmeHtml = readmeHeader + readmeHtml + readmeFooter;
                    // Save in destination as README.html
                    String readmeHtmlPath = getDestRootPath() + File.separator + "README.html";
                    writeFile(readmeHtml, readmeHtmlPath);
                    ActionLogger.info("'README.html' written in "+readmeHtmlPath);
                    ActionLogger.finer("'README.html' content:\n" + readmeHtml);
                }
            } catch (IOException e) {
                ActionLogger.severe(e.getMessage(), e);
            }
        }
    }

    private void copyContent(HashMap<String, String> valuesMap) {
        for (String content : contentToCopy) {
            String contentFromPath = githubWkSpc + File.separator + content;
            Path from = Paths.get(contentFromPath);
            File contentFile = from.toFile();
            String destRootPath = getDestRootPath();
            ActionLogger.fine("Copying content from '" + contentFromPath + "' to '" + destRootPath + "'");
            try {
                if (contentFile.exists()) {
                    if (contentFile.isFile()) {
                        if (contentFile.getName().endsWith(".md") && convertMdToHtml) {
                            ActionLogger.fine("Converting Markdown file to HTML: " + contentFile.getName());
                            // Convert Markdown file to HTML
                            String mdString = readTextFile(contentFromPath);
                            String htmlString = convertMarkdownToHtml(contentFile.getName(), mdString, valuesMap);
                            writeFile(htmlString, destRootPath + File.separator + content.replace(".md", ".html"));
                        } else {
                            Path dest = Paths.get(destRootPath + File.separator + content);
                            Files.copy(from, dest, StandardCopyOption.REPLACE_EXISTING);
                            ActionLogger.finer("File '" + from.toString() + "' copied to '"+dest.toString() + "'");
                        }
                    } else if (contentFile.isDirectory()) {
                        copyFilesInDir(contentFromPath, destRootPath + File.separator + content, convertMdToHtml, valuesMap);
                    } else {
                        ActionLogger.warning("Content to copy '" + contentFromPath + "' is not a file or directory. Ignoring.");
                    }
                }
            } catch (IOException e) {
                ActionLogger.severe(e.getMessage(), e);
            }
        }
    }

    private String getTmplFullPath() {
        String tmplFullPath = null;
        if (themesOn) {
            tmplFullPath = THEMES_PATH + File.separator + templatesPath;
        } else {
            tmplFullPath = githubWkSpc + File.separator + templatesPath;
        }
        return tmplFullPath;
    }

    private String getDestRootPath() {
        if (destRootPath == null) {
            destRootPath = githubWkSpc + File.separator + destinationPath;
            assureDestinationExists(destRootPath);
        }
        return destRootPath;
    }

    /**
     * This method activate the syntax highlighting for Markdown converted
     * files if it's used a pre-build theme. For custom templates, it process the
     * snippets needed to activate the syntax highlighting and copy the appropriate
     * files.
     * @param valuesMap The map of values to be used in the templates.
     */
    private void processSyntaxHighlighting(HashMap<String, String> valuesMap) {
        if (syntaxHighlightEnabled) {
            ActionLogger.info("Syntax highlighting enabled: processing.");
            String snippetsPath = THEMES_PATH + File.separator + "common" + File.separator + "syntaxhlsnpt";
            processSnippetsInDir(snippetsPath, valuesMap);
            copySyntaxHighlightingFile("prism.js");
            copySyntaxHighlightingFile("prism-" + syntaxHighlightTheme + ".css");
        }
    }

    private void cleanSyntaxHighlightSnippets(HashMap<String, String> valuesMap) {
        valuesMap.remove("SNP_SYNTAX_HIGHLIGHT_CSS");
        valuesMap.remove("SNP_SYNTAX_HIGHLIGHT_JS");
    }

    private void copySyntaxHighlightingFile(String fileName) {
        ActionLogger.fine("Copying syntax highlighting file '" + fileName + "'");
        String syntaxFilesPath = THEMES_PATH + File.separator + "common" + File.separator + "syntaxhlfiles";
        Path from = Paths.get(syntaxFilesPath + File.separator + fileName);
        Path dest = Paths.get(getDestRootPath() + File.separator + fileName);
        try {
            Files.copy(from, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            ActionLogger.severe(e.getMessage(), e);
        }
    }

    private void processSnippets(HashMap<String, String> valuesMap) {
        String snippetsFullPath = null;
        if (themesOn) {
            // Process common snippets first
            snippetsFullPath = THEMES_PATH + File.separator + "common" + File.separator + "snippets";
            processSnippetsInDir(snippetsFullPath, valuesMap);
            // Process theme snippets:
            // if the name is the same as one in common folder, will over-write common one
            snippetsFullPath = THEMES_PATH + File.separator + templatesPath + File.separator + "snippets";
            processSnippetsInDir(snippetsFullPath, valuesMap);
        } else {
            snippetsFullPath = githubWkSpc + File.separator + snippetsPath;
            processSnippetsInDir(snippetsFullPath, valuesMap);
        }
    }

    private void processSnippetsInDir(String snippetsFullPath, HashMap<String, String> valuesMap) {
        List<String> snptsFiles = listFilesInDir(new File(snippetsFullPath));
        for (String snptFile : snptsFiles) {
            try {
                // The snippet key will be 'SNP_' followed by the file name until the first '.'
                String snptKey = "SNP_"+snptFile.split("\\.")[0];
                ITemplate snpt = new ITemplate(snippetsFullPath + File.separator + snptFile, "path");
                String filledSnpt = snpt.fill(valuesMap);
                // Snippets with filename ending in ".md", treat as Markdown
                if (snptFile.endsWith(".md")) {
                    filledSnpt = mdProcessor.processMarkdown(filledSnpt);
                    ActionLogger.fine("Identified markdown snippet: "+snptFile);
                    ActionLogger.finer("Snippet filled and converted from Markdown to HTML:\n"+filledSnpt);
                } else {
                    ActionLogger.fine("Snippet '"+snptKey+"': "+snptFile);
                    ActionLogger.finer("Snippet '"+snptKey+"' Filled:\n"+filledSnpt);
                }
                valuesMap.put(snptKey, filledSnpt);
            } catch (Exception e) {
                ActionLogger.severe(e.getMessage(), e);
            }
        }
    }

    private int processTmplFolder(String tmplFullPath, HashMap<String, String> valuesMap) {
        int result = 0;
        ITemplate tmpl = null;
        File tmplDir = new File(tmplFullPath);
        List<String> tmplFiles = listFilesInDir(tmplDir);
        if (tmplFiles.size() == 0) {
            ActionLogger.warning("No templates found in '" + tmplFullPath + "'.");
        }
        for (String tmplFile : tmplFiles) {
            try {
                tmpl = new ITemplate(tmplFullPath + File.separator + tmplFile, "path");
                String filledTmpl = tmpl.fill(valuesMap);
                ActionLogger.fine("Processing template file: "+tmplFile);
                ActionLogger.finer("'" + tmplFile + "' filled:\n"+filledTmpl);
                String destfile = tmplFile.replaceFirst("\\.tmpl", "");
                // For .md files, treat as Markdown
                if (tmplFile.endsWith(".md")) {
                    filledTmpl = convertMarkdownToHtml(tmplFile, filledTmpl, valuesMap);

                    destfile = destfile.replaceAll("\\.md", "\\.html");
                    ActionLogger.fine("Identified markdown template: "+tmplFile);
                    ActionLogger.finer("Converted Markdown to HTML:\n"+filledTmpl);
                }
                String destFullPath = tmplFullPath.replace(getTmplFullPath(), githubWkSpc + File.separator + destinationPath);
                ActionLogger.fine("Destination full path: "+destFullPath);
                assureDestinationExists(destFullPath);
                writeFile(filledTmpl, destFullPath + File.separator + destfile);
            } catch (Exception e) {
                ActionLogger.severe(e.getMessage(), e);
                result = 1;
            }
        }
        if (allowSubfolders) {
            List<String> subdirs = listSubDirs(tmplDir);
            for (String dir : subdirs) {
                processTmplFolder(tmplFullPath + File.separator + dir, valuesMap);
            }
        }
        return result;
    }

    private String convertMarkdownToHtml(String fileName, String mdString, HashMap<String, String> valuesMap) {
        if (convertMdToHtml) {
            mdString = convertMdLinks(mdString);
        }
        String htmlString = mdProcessor.processMarkdown(mdString);

        String mdHeader = valuesMap.get("SNP_MARKDOWN_HEADER");
        String mdFooter = valuesMap.get("SNP_MARKDOWN_FOOTER");
        htmlString = (mdHeader == null ? "<html><head>\n<title>" + fileName + "</title>\n</head><body>\n" : mdHeader)
                + htmlString + (mdFooter == null ? "\n</body></html>" : mdFooter);

        return htmlString;
    }

    private String convertMdLinks(String mdString) {
        // Convert links to .md files to point to the correct .html file
        // This regex locate strings like "(path/file.md)"...
        String regex = "\\(([-0-9a-zA-Z/_.]+).md\\)";
        // ...and replace it with "(path/file.html)"
        String replacement = "($1.html)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mdString);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Checks if the destination folder exists and creates it if it does not.
     * 
     * @param destFullPath The full path to the destination folder.
     */
    private void assureDestinationExists(String destFullPath) {
        File destFullPathFile = new File(destFullPath);
        if (destFullPathFile != null && !destFullPathFile.exists()) {
            boolean created = destFullPathFile.mkdirs();
            if (created) {
                ActionLogger.info("Created dir: "+destFullPath);
            } else {
                ActionLogger.severe("FAILED to create dir: "+destFullPath);
            }
        }
    }

    /**
     * Get the names of the subdirectories of the specified dir.
     * 
     * @param dir The directory to list the subdirectories from.
     * @return A list of subdirs or an empty list if the directory has no subdirs or is non existent.
     */
    private List<String> listSubDirs(File dir) {
        File[] allchildren = dir.listFiles();
        List<String> subdirs = new ArrayList<>();

        if (allchildren != null) {
            for (File child : allchildren) {
                if (child.isDirectory()) {
                    subdirs.add(child.getName());
                }
            }
        }
        return subdirs;
    }

    /**
     * Get the filenames from the content of the specified dir.
     * 
     * @param dir The directory to list the files from.
     * @return A list of filenames or an empty list if the directory is empty or non existent.
     */
    private List<String> listFilesInDir(File dir) {
        File[] allchildren = dir.listFiles();
        List<String> files = new ArrayList<String>();

        if (allchildren != null) {
            for (File child : allchildren) {
                if (child.isFile()) {
                    files.add(child.getName());
                }
            }
        }
        return files;
    }

    private String readTextFile(String textFilePath) throws IOException {
        Stream<String> lines = Files.lines(Paths.get(textFilePath));
        String readmeMd = lines.collect(Collectors.joining("\n"));
        lines.close();
        return readmeMd;
    }

    /**
	 * Write a string to a file.
	 *
	 * @param text The string to write to the file.
	 * @param path The path of the file to be writen.
	 */
	private void writeFile(String text, String path) {
		FileWriter out;
        try {
            out = new FileWriter(path);
            out.write(text);
            out.close();
        } catch (IOException e) {
            ActionLogger.severe(e.getMessage(), e);
        }
	}

    public void setSnippetsPath(String snptsPath) {
        if (snptsPath != null && !"".equals(snptsPath)) {
            snippetsPath = snptsPath;
        }
    }

    public void configPublishReadme(String publishReadmeInput) {
        publishReadmeInput = publishReadmeInput.toLowerCase();
        if ("true".equals(publishReadmeInput) || "inline".equals(publishReadmeInput)) {
            publishReadme = true;
            if ("inline".equals(publishReadmeInput)) {
                readmeInline = true;
            }
        }
    }

    public void setContentToCopy(String contentToCopyStr) {
        if (contentToCopyStr != null && !"".equals(contentToCopyStr)) {
            contentToCopy = contentToCopyStr.split("\\s+");
        }
    }

    public void setSyntaxHighlightTheme(String theme) {
        if (theme != null && !"".equals(theme)) {
            syntaxHighlightTheme = theme.toLowerCase();
        }
    }

    public void setConvertMdToHtml(String convertMd2Html) {
        if (convertMd2Html != null && "true".equals(convertMd2Html.toLowerCase())) {
            convertMdToHtml = true;
        }
    }
}
