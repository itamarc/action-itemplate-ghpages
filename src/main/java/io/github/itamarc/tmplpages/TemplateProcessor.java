package io.github.itamarc.tmplpages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.gjt.itemplate.ITemplate;

public class TemplateProcessor {
    static String templatesPath = null;
    static String destinationPath = null;
    static String destinationBranch = null;
    static String snippetsPath = null;

    /**
     * Class responsible to make the processing of the templates.
     * 
     * @param tmplPath Path to the folder containing the templates
     * @param tmplBranch Branch where the templates are stored
     * @param destPath Path to the folder where the generated pages will reside
     * @param destBranch Branch where the pages will be saved
     */
    public TemplateProcessor(String tmplPath, String destPath, String destBranch, String snptsPath) {
        templatesPath = tmplPath;
        destinationPath = destPath;
        destinationBranch = destBranch;
        snippetsPath = snptsPath;
    }

    public int run(HashMap<String, String> valuesMap) {
        String githubWkSpc = valuesMap.get("GITHUB_WORKSPACE");
        int result = 0;
        ITemplate tmpl = null;
        String tmplFullPath = githubWkSpc + File.separator + templatesPath;
        List<String> tmplFiles = listFilesInDir(new File(tmplFullPath));
        for (String tmplFile : tmplFiles) {
            try {
                tmpl = new ITemplate(tmplFullPath + File.separator + tmplFile, "path");
                String filledTmpl = tmpl.fill(valuesMap);
                // TODO: Remove code only for testing
                System.out.println(">>> File: "+tmplFile+"\nFilled:\n"+filledTmpl);
                String destfile = tmplFile.replaceFirst("\\.tmpl", "");
                writeFile(filledTmpl, githubWkSpc + File.separator + destinationPath + File.separator + destfile);
            } catch (Exception e) {
                Logger log = Logger.getLogger(this.getClass().getName());
                log.warning(
                        e.getClass().getCanonicalName() + ": " + e.getMessage() + " - " + e.getStackTrace().toString());
                result = 1;
            }
        }
        return result;
    }
    
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
            Logger log = Logger.getLogger(this.getClass().getName());
            log.warning(e.getClass().getCanonicalName()+": "+e.getMessage() + " - " + e.getStackTrace().toString());
        }
	}
}
