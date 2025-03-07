package de.uke.iam.mtb.control.helper;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {

  private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);

  public static void unzip(Path zipFilePath, Path destDir) throws IOException {
    File destinationDirectory = destDir.toFile();
    if (!destinationDirectory.exists()) {
      destinationDirectory.mkdirs();
    }
    try (ZipFile zipFile = new ZipFile(zipFilePath.toFile())) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        String destPath = destDir.resolve(entry.getName()).toString();
        if (entry.isDirectory()) {
          new File(destPath).mkdirs();
        } else {
          try (InputStream inputStream = zipFile.getInputStream(entry);
              FileOutputStream outputStream = new FileOutputStream(destPath)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
              outputStream.write(buffer, 0, length);
            }
          }
        }
      }
    }
  }


  public static boolean isValidZipFile(Path zipFilePath) {
    ZipFile zipfile = null;
    try {
      /* if the file has no errors */
      zipfile = new ZipFile(zipFilePath.toFile());
      return true;
      /* if an exception is thrown then return false */
    } catch (IOException e) {
      logger.debug("zip file {} is not valid", zipFilePath);
      e.printStackTrace();
      return false;
      /* finally close the file */
    } finally {
      try {
        if (zipfile != null) {
          zipfile.close();
          zipfile = null;
        }
        /* if the file could not be closed */
      } catch (IOException e) {
        /*        e.printStackTrace();*/
        logger.debug("Could not close {}", zipfile);
      }
    }
  }

  /* move all files from the given folder and delete the folder after that */
  public static boolean moveFiles(File srcDir, String destDir) throws IOException {
    File[] listOfFiles = srcDir.listFiles();
    /* create a list of moved files to delete them if any error occurs */
    ArrayList<Path> listOfMovedFiles = new ArrayList<>();
    try {
      if (srcDir.isDirectory()) {
        if (listOfFiles != null) {
          for (File subFile : listOfFiles) {
            Path srcFilePath = subFile.toPath();
            Path destFilePath = Paths.get(destDir, subFile.getName());
            /* move each file in the folder */
            Files.move(srcFilePath, destFilePath, REPLACE_EXISTING);
            listOfMovedFiles.add(destFilePath);
          }
        }
        /* delete the folder after moving all files */
        Files.deleteIfExists(srcDir.toPath());
      }
      /* if all files are successfully moved -> return true*/
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      /* if all files are not successfully moved -> rollback + return false */
      for (Path movedFile : listOfMovedFiles) {
        Files.deleteIfExists(movedFile);
      }
      return false;
    }
  }

}
