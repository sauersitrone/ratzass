/*
 *   Copyright (c) 2024 Sauer Sitrone
 *   All rights reserved.
 *   I am Terry, and Terry has done this.
 */
package de.simone;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class FileUtils {

    private static final String RESOURCES_PATH = "sFiles/";
    private static final String RESOURCES_IMAGES = RESOURCES_PATH + "images/";
    private static final String STATIC_RESOURCES_PATH = "/META-INF/resources/"; // NOSONAR this value is not
                                                                                // Customizable
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static String DB_DDL;

    public String uploadFile(String folder, String base64Data) {
        String fileN = saveMimeDataToFile(folder, base64Data);
        return "File uploaded " + fileN;
    }

    public Response downloadFile(String filePath) {
        File fileDownload = new File(filePath);
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
        Response.ResponseBuilder response = Response.ok(fileDownload);
        response.header("Content-Disposition", "attachment;filename=" + fileName);
        return response.build();
    }

    public static String saveMimeDataToFile(String base64Data) {
        return saveMimeDataToFile(RESOURCES_IMAGES, base64Data);
    }

    /**
     * save the incomming information stored in {@link Base64} format as file. This
     * method parse the
     * base64 data and decide the tipe of file extension based on the data
     * information schema stored
     * in head of the incomming data
     *
     * @param folder     - target folder. (muss end with /)
     * @param base64Data - information to store
     * @return the path/file.name assigned by this method
     * @throws Exception
     */
    public static String saveMimeDataToFile(String folder, String base64Data) {
        // parse incommin data format (data:image/png;base64)
        int comaI = base64Data.indexOf(',', 0);
        String dFormat = base64Data.substring(0, comaI);
        String base64 = base64Data.substring(comaI + 1);
        String mime = dFormat.split(":")[1].split(";")[0];
        String ext = mime.split("/")[1];
        String fileN = folder + Long.toHexString(System.currentTimeMillis()) + "." + ext;
        byte[] data = Base64.getDecoder().decode(base64);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        saveToFile(bais, fileN);
        return fileN;
    }

    public static String getDbDdl() {
        if (DB_DDL == null) {
            DB_DDL = readResourceFile("ddl.sql");
        }
        return DB_DDL;
    }

    /**
     * read the content of a text file located in static resources folder. {@code
     * /META-INF/resources/}
     *
     * @param fileName - the file to read
     * @return the content
     */
    public static String readResourceFile(String fileName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = loader.getResource(STATIC_RESOURCES_PATH + fileName).openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String script = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            return script;
        } catch (Exception e) {
            Log.error("", e);
        }

        // return empty string is needed to avoid null pointer exception in qute engine
        return "";
    }

    /**
     * check if the file exist in static resources folder.
     * {@code /META-INF/resources/}
     * 
     * @param fileName - the file to check
     * @return true if the file exists, false otherwise
     */
    public static boolean existsResourceFile(String fileName) {
        boolean exist = false;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = loader.getResource(STATIC_RESOURCES_PATH + fileName).openStream()) {
            exist = true;
        } catch (Exception e) {
            // do nothing
        }

        return exist;
    }

    /**
     * check if the filename exist. if exist, this method will return a new
     * fileName. e.g if fileName
     * terry.txt exis, this method return Terrx_1.txt
     *
     * @param fileName - the origen file name to consider
     */
    private static String getFileName(String fileName) {

        File targetFile = new File(fileName);

        if (targetFile.exists()) {
            String dir = FilenameUtils.getPath(fileName);
            String fbn = FilenameUtils.getBaseName(fileName);
            String fext = FilenameUtils.getExtension(fileName);
            int cnt = 0;
            while (targetFile.exists()) {
                cnt++;
                targetFile = new File(dir + fbn + "_" + cnt + "." + fext);
            }
        }
        return targetFile.getPath();
    }

    /**
     * save the data channeled by the inputStream argument to the
     * {@link FileUtils#RESOURCES_IMAGES}
     * folder in hard drive and return the saved filename path
     *
     * <p>
     * NOTE: this method check if the filaName argument start with {@link
     * FileUtils#RESOURCES_PATH}. if not, automatic add the resource path
     *
     * @param inputStream - data source
     * @param fileName    - original filename.
     */
    public static String saveImageToFile(InputStream inputStream, String fileName) {
        String tmp = fileName.startsWith(FileUtils.RESOURCES_IMAGES)
                ? fileName
                : FileUtils.RESOURCES_IMAGES + fileName;
        String fileName2 = getFileName(tmp);
        saveToFile(inputStream, fileName2);
        return fileName2;
    }

    /**
     * Read the File asociated with fileName parameter and convert the binary file
     * into {@link Base64}
     * format. this method also include de header mime schema at start of the
     * converted binary file.
     *
     * @param fileName - the file name to read
     * @return base64 string with header
     */
    public static String readMimeDataFromFile(String fileName) {
        String filename2 = fileName;
        boolean userFileInput = false;
        // is file static?
        if (fileName.startsWith(RESOURCES_PATH)) {
            userFileInput = true;
            filename2 = System.getProperty("user.dir") + File.separator + fileName;
        } else {
            filename2 = STATIC_RESOURCES_PATH + fileName;
        }

        File file = new File(filename2);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = userFileInput ? new FileInputStream(file)
                : loader.getResource(filename2).openStream()) {
            byte[] data = inputStream.readAllBytes();
            String cntT = Files.probeContentType(file.toPath());
            String base64 = Base64.getEncoder().encodeToString(data);
            String finalData = "data:" + cntT + ";base64," + base64;
            return finalData;
        } catch (Exception e) {
            Log.error(e); // single line log
        }
        return null;
    }

    public static void deleteFile(String fileName) {
        String filename2 = System.getProperty("user.dir") + "/" + fileName;
        File file = new File(filename2);
        boolean b = file.delete(); // NOSONAR: if delete fail, log error
        if (!b)
            Log.error("Error trying to delete the file '" + filename2 + "'");
    }

    /**
     * extract the mimetype from the base64 image passes as argument.
     *
     * @param image64 - the image
     * @return the mimetype
     */
    public static String getMimeType(String image64) {
        String s1 = image64.substring(0, 100);
        int i0 = s1.indexOf(':');
        int i1 = s1.indexOf(';');
        String mime = s1.substring(i0 + 1, i1);
        return mime;
    }

    /**
     * save the content of the inputStream to a file.
     *
     * @param inputStream - the inputStream
     * @param fileName    - the file name
     * @return the relative path to the saved file z.B public/files (no pre or pos
     *         /)
     */
    public static String saveToFile(InputStream inputStream, String fileName) {

        // fail safe: remove posible directory reference in fileName
        File file = new File(fileName);

        String fileType = "image";
        if (UIUtils.isFileMemberOf(fileName, UIUtils.VALID_DOCUMENTS))
            fileType = "file";

        String fullFn = "media/" + fileType + "/" + file.getName();

        String fileName2 = getFileName(fullFn);

        try {
            // if fileName2 contain directory, create if not exist
            java.nio.file.Path path = Paths.get(fileName2);
            if (path.getNameCount() > 0) {
                File dir = path.getParent().toFile();
                if (!dir.exists())
                    dir.mkdirs();
            }
            byte[] bytes = IOUtils.toByteArray(inputStream);
            Files.write(Paths.get(fileName2), bytes);
        } catch (Exception e) {
            Log.error(e);
        }
        return fileName2;
    }

    public static String getNormalizeString(String stringToNormalize) {
        // replace all but chars, nums and dot
        String nString = stringToNormalize.replaceAll("[^\\w\\.\\/]", "_");
        // remove all but the last dot
        nString = nString.replaceAll("\\.(?=.*\\.)", "_");
        return nString;
    }

    /**
     * return a URL of the Thumbnail of the youtube url main video
     *
     * @param url - the url
     * @return the Thumbnail.s url
     */
    public static String getRemoteYoutubeThumbnail(String url) {
        if (!url.contains("youtube"))
            return null;

        String pattern = "watch/?.*v=([^&]*)";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            String id = matcher.group(1);
            String url3 = "https://img.youtube.com/vi/" + id + "/maxresdefault.jpg";
            return url3;
        }
        return null;
    }
}
