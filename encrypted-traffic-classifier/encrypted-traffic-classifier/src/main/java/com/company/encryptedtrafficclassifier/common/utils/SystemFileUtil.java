package com.company.encryptedtrafficclassifier.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SystemFileUtil {

    public static void copyFilteredFiles(File sourceDirectory, File targetDirectory) throws IOException {
        if (sourceDirectory.isDirectory()) {
            File[] files = sourceDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    copyFilteredFiles(file, targetDirectory);
                }
            }
        } else if (sourceDirectory.isFile() && sourceDirectory.getName().toLowerCase().endsWith(".png")) {
            File targetFile = new File(targetDirectory, sourceDirectory.getName());
            Files.copy(sourceDirectory.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

}