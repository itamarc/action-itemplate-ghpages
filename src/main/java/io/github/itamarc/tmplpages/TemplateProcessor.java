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
    static boolean allowSubfolders = false;
    static String githubWkSpc = null;

    /**
     * Class responsible to make the processing of the templates.
     * 
     * @param ghWkSpc GitHub Workspace
     * @param tmplPath Path to the folder containing the templates
     * @param destPath Path to the folder where the generated pages will reside
     * @param allowSubdirs Allow the templates to be stored in subdirs of tmplPath
     */
    public TemplateProcessor(String ghWkSpc, String tmplPath, String destPath, boolean allowSubdirs) {
        githubWkSpc = ghWkSpc;
        templatesPath = tmplPath;
        destinationPath = destPath;
        allowSubfolders = allowSubdirs;
    }

    public int run(HashMap<String, String> valuesMap) {
        String tmplFullPath = githubWkSpc + File.separator + templatesPath;
        return processTmplFolder(new File(tmplFullPath), valuesMap);
    }
    
    private int processTmplFolder(File tmplDir, HashMap<String, String> valuesMap) {
        int result = 0;
        ITemplate tmpl = null;
        String templatesRootPath = githubWkSpc + File.separator + templatesPath;
        List<String> tmplFiles = listFilesInDir(tmplDir);
        for (String tmplFile : tmplFiles) {
            try {
                tmpl = new ITemplate(tmplDir.getAbsolutePath() + File.separator + tmplFile, "path");
                String filledTmpl = tmpl.fill(valuesMap);
                // TODO: Remove code only for testing
                System.out.println(">>> File: "+tmplFile+"\nFilled:\n"+filledTmpl);
                String destfile = tmplFile.replaceFirst("\\.tmpl", "");
                String destFullPath = tmplDir.getAbsolutePath().replaceFirst(templatesRootPath, githubWkSpc + File.separator + destinationPath);
                // TODO: Remove code only for testing
                System.out.println("Destination full path: "+destFullPath);
                File destFullPathFile = new File(destFullPath);
                if (destFullPathFile != null && !destFullPathFile.exists()) {
                    boolean created = destFullPathFile.mkdirs();
                    // TODO: Remove code only for testing
                    if (created) {
                        System.out.println("Created dir: "+destFullPath);
                    } else {
                        System.out.println("FAILED to create dir: "+destFullPath);
                    }
                }
                writeFile(filledTmpl, destFullPath + File.separator + destfile);
            } catch (Exception e) {
                Logger log = Logger.getLogger(this.getClass().getName());
                log.warning(
                        e.getClass().getCanonicalName() + ": " + e.getMessage() + " - " + e.getStackTrace().toString());
                result = 1;
            }
        }
        if (allowSubfolders) {
            List<File> subdirs = listSubDirs(tmplDir);
            for (File dir : subdirs) {
                processTmplFolder(dir, valuesMap);
            }
        }
        return result;
    }

    private List<File> listSubDirs(File dir) {
        File[] allchildren = dir.listFiles();
        List<File> subdirs = new ArrayList<>();

        if (allchildren != null) {
            for (File child : allchildren) {
                if (child.isDirectory()) {
                    subdirs.add(child);
                }
            }
        }
        return subdirs;
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
