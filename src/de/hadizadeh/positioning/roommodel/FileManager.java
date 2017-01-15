package de.hadizadeh.positioning.roommodel;


import org.json.JSONArray;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Handles file operations of the room models
 */
public class FileManager {
    /**
     * File extension of the room model file
     */
    public static final String MODEL_EDITOR_FILE_EXTENSION = "mef";
    /**
     * Starting header of the room model file
     */
    private static final String MODEL_EDITOR_FILE_HEADER = "MODEL-EDITOR-FILE ";

    /**
     * Progress listener for downloading and uploading data
     */
    public static interface ProgressListener {
        void progress(int progress);
    }

    /**
     * Creates a temp file for saving temporary data
     *
     * @param subfolder subfolder in the temp directory
     * @return path to the available temp file
     */
    public static File getTmpName(String subfolder) {
        File tmpDir = new File(System.getProperty("java.io.tmpdir") + "ModelEditor" + File.separator + subfolder);
        removeDirectory(tmpDir);
        tmpDir.mkdirs();
        return tmpDir;
    }

    /**
     * Reads all text data from a file to a string
     *
     * @param file text file
     * @return text
     */
    public static String readTextFile(String file) {
        File fileData = new File(file);
        if (fileData.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileData), "UTF8"));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (text.length() > 0) {
                return text.toString().substring(0, text.length() - 1);
            }
        }
        return "";
    }

    /**
     * Removes a directory recursive
     *
     * @param dir directory to remove
     */
    public static void removeDirectory(File dir) {
        if (dir.exists()) {
            if (dir.isDirectory()) {
                String[] entries = dir.list();
                for (String s : entries) {
                    File currentFile = new File(dir.getPath(), s);
                    if (currentFile.isDirectory()) {
                        removeDirectory(currentFile);
                    } else {
                        currentFile.delete();
                    }
                }
            }
            dir.delete();
        }
    }

    /**
     * Calculates the hash check sum of a file (SHA1)
     *
     * @param file file to check
     * @return hash check sum
     */
    public static String calculateHash(File file) {
        if (file.exists()) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                FileInputStream fis = new FileInputStream(file);
                byte[] dataBytes = new byte[1024];
                int read = 0;
                while ((read = fis.read(dataBytes)) != -1) {
                    md.update(dataBytes, 0, read);
                }
                byte[] mdbytes = md.digest();
                StringBuffer sb = new StringBuffer("");
                for (int i = 0; i < mdbytes.length; i++) {
                    sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
                }
                fis.close();
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Compresses a directory to a zip file
     *
     * @param zipFile destination zip file
     * @param dir     directory to compress
     * @return created zip file
     */
    public static File compress(File zipFile, File dir) {
        File createdFile = null;
        try {
            File backupFile = new File(zipFile.getAbsolutePath() + ".bak");
            if (zipFile.exists()) {
                if (backupFile.exists()) {
                    backupFile.delete();
                }
                zipFile.renameTo(backupFile);
                if (zipFile.exists()) {
                    zipFile.delete();
                }
            }
            FileOutputStream fout = new FileOutputStream(zipFile);
            fout.write(MODEL_EDITOR_FILE_HEADER.getBytes());
            createdFile = createZip(fout, dir);
            if (createdFile != null) {
                if (backupFile.exists()) {
                    backupFile.delete();
                }
            } else {
                if (zipFile.exists() && backupFile.exists()) {
                    zipFile.delete();
                }
                if (backupFile.exists()) {
                    backupFile.renameTo(zipFile);
                }
            }
            return createdFile;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return createdFile;
    }

    /**
     * Decompress a zip file to a destination directory
     *
     * @param zipFile       zip file
     * @param destDirectory destination directory
     * @return unzipped destination directory
     */
    public static File decompress(File zipFile, File destDirectory) {
        File decompressedFolder = null;
        try {
            byte[] header = new byte[MODEL_EDITOR_FILE_HEADER.getBytes().length];
            FileInputStream is = new FileInputStream(zipFile);
            is.read(header);
            if (new String(header).equals(MODEL_EDITOR_FILE_HEADER)) {
                decompressedFolder = unzip(is, destDirectory);
            } else {
                // Try it, if it is a raw zip file
                is.close();
                is = new FileInputStream(zipFile);
                decompressedFolder = unzip(is, destDirectory);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return decompressedFolder;
    }

    private static File createZip(FileOutputStream fout, File dir) {
        try {
            ZipOutputStream zout = new ZipOutputStream(fout);
            zout.setMethod(ZipOutputStream.DEFLATED);
            zout.setLevel(Deflater.BEST_COMPRESSION);
            zipSubDirectory("", dir, zout);
            zout.close();
            fout.close();
            return dir;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static void zipSubDirectory(String basePath, File dir, ZipOutputStream zout) throws IOException {
        byte[] buffer = new byte[4096];
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                String path = basePath + file.getName() + File.separator;
                zipSubDirectory(path, file, zout);
            } else {
                FileInputStream fin = new FileInputStream(file);
                zout.putNextEntry(new ZipEntry(basePath + file.getName()));
                int length;
                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                }
                zout.closeEntry();
                fin.close();
            }
        }
    }

    private static File unzip(FileInputStream is, File destDirectory) {
        try {
            if (destDirectory.exists()) {
                removeDirectory(destDirectory);
            }
            if (!destDirectory.exists()) {
                destDirectory.mkdirs();
            }
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                fileName = fileName.replace("/", File.separator).replace("\\", File.separator);
                File newFile = new File(destDirectory.getAbsolutePath() + File.separator + fileName);
                (new File(newFile.getParent())).mkdirs();
                if (!ze.isDirectory()) {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            is.close();
            return destDirectory;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private static void writeToOutputStream(InputStream inputStream, OutputStream outputStream, long fileLength, ProgressListener progressListener) throws IOException {
        int perCent;
        int lastPercent = -1;
        byte[] buffer = new byte[4096];
        int bytesRead;
        long bytes = 0;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            if (progressListener != null) {
                bytes += bytesRead;
                perCent = (int) ((100 * bytes) / fileLength);
                if (perCent != lastPercent) {
                    progressListener.progress(perCent);
                    lastPercent = perCent;
                }
            }
        }
    }

    /**
     * Uploads a file to a server
     *
     * @param url       url of the server
     * @param authToken authentication token or null, if it is not needed
     * @param file      file to upload
     * @return http response status code
     */
    public static int uploadFile(String url, String authToken, File file) {
        return uploadFile(url, authToken, file, null);
    }

    /**
     * Uploads a file to a server
     *
     * @param url              url of the server
     * @param authToken        authentication token or null, if it is not needed
     * @param file             file to upload
     * @param progressListener progress listener of the upload
     * @return http response status code
     */
    public static int uploadFile(String url, String authToken, File file, ProgressListener progressListener) {
        try {
            String boundary = String.valueOf(System.currentTimeMillis());
            String lineEnd = "\r\n";
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setChunkedStreamingMode(4096);
            con.setRequestProperty("Authorization", "Bearer " + authToken);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes("--" + boundary + lineEnd);
            wr.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + file.getName() + "\"" + lineEnd);
            wr.writeBytes(lineEnd);
            FileInputStream inputStream = new FileInputStream(file);
            writeToOutputStream(inputStream, wr, file.length(), progressListener);
            inputStream.close();
            wr.writeBytes(lineEnd);
            wr.writeBytes("--" + boundary + "--" + lineEnd);
            wr.flush();
            wr.close();
            int status = con.getResponseCode();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            return HttpURLConnection.HTTP_INTERNAL_ERROR;
        }
    }

    /**
     * Downloads a file from a server
     *
     * @param url             url of the server
     * @param destinationFile destination file to download
     * @return http response status code
     */
    public static int downloadFile(String url, File destinationFile) {
        return downloadFile(url, null, destinationFile, null);
    }

    /**
     * Checks if a file is already downloaded (compare hash codes)
     *
     * @param url       url of the server
     * @param authToken authentication token or null, if it is not needed
     * @return http response status code
     */
    public static int checkFileState(String url, String authToken) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            if (authToken != null) {
                con.setRequestProperty("Authorization", "Bearer " + authToken);
            }
            con.setRequestMethod("GET");
            return con.getResponseCode();
        } catch (FileNotFoundException ex) {
            return HttpURLConnection.HTTP_NOT_FOUND;
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpURLConnection.HTTP_INTERNAL_ERROR;
        }
    }

    /**
     * Downloads a file from a server
     *
     * @param url              url of the server
     * @param destinationFile  destination file to download
     * @param progressListener progress listener of the download
     * @return http response status code
     */
    public static int downloadFile(String url, String authToken, File destinationFile, ProgressListener progressListener) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            if (authToken != null) {
                con.setRequestProperty("Authorization", "Bearer " + authToken);
            }
            con.setRequestMethod("GET");
            int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = con.getInputStream();
                OutputStream outputStream = new FileOutputStream(destinationFile);
                writeToOutputStream(inputStream, outputStream, con.getContentLength(), progressListener);
                outputStream.flush();
                inputStream.close();
                outputStream.close();
            }
            return status;
        } catch (FileNotFoundException ex) {
            return HttpURLConnection.HTTP_NOT_FOUND;
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpURLConnection.HTTP_INTERNAL_ERROR;
        }
    }

    /**
     * Requests all available projects on a server
     *
     * @param url url of the server
     * @return list of project names
     */
    public static List<String> getAllProjects(String url) {
        return getAllProjects(url, null);
    }

    /**
     * Requests all available projects on a server
     *
     * @param url       url of the server
     * @param authToken authentication token or null, if it is not needed
     * @return list of project names
     */
    public static List<String> getAllProjects(String url, String authToken) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            if (authToken != null) {
                con.setRequestProperty("Authorization", "Bearer " + authToken);
            }
            con.setRequestMethod("GET");
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONArray data = new JSONArray(response.toString());
                List<String> projects = new ArrayList<String>();
                if (data != null) {
                    for (int i = 0; i < data.length(); i++) {
                        projects.add(data.get(i).toString());
                    }
                }
                return projects;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a new project on the server
     *
     * @param url       url of the server
     * @param authToken authentication token or null, if it is not needed
     * @return http response status code
     */
    public static int createRemoteProject(String url, String authToken) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            if (authToken != null) {
                con.setRequestProperty("Authorization", "Bearer " + authToken);
            }
            con.setRequestMethod("POST");
            return con.getResponseCode();
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpURLConnection.HTTP_INTERNAL_ERROR;
        }
    }

    /**
     * Removes an existing project of the server
     *
     * @param url       url of the server
     * @param authToken authentication token or null, if it is not needed
     * @return http response status code
     */
    public static int deleteRemoteProject(String url, String authToken) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            if (authToken != null) {
                con.setRequestProperty("Authorization", "Bearer " + authToken);
            }
            con.setRequestMethod("DELETE");
            return con.getResponseCode();
        } catch (Exception ex) {
            ex.printStackTrace();
            return HttpURLConnection.HTTP_INTERNAL_ERROR;
        }
    }
}
