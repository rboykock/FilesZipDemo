package pro.cherkassy.boyko;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Created by rboyko on 22.11.16.
 */
public class FilesZipDemo {

    public static String[] AUDIO_EXT_ARRAY=new String[]{"mp3","wav","wma"};
    public static String[] VIDEO_EXT_ARRAY=new String[]{"avi","mp4","flv"};
    public static String[] IMAGE_EXT_ARRAY=new String[]{"jpeg","jpg","gif","png"};
    public static String DST_ZIP_AUDIOS="audios.zip";
    public static String DST_ZIP_VIDEOS="videos.zip";
    public static String DST_ZIP_IMAGES="images.zip";

    public static void main(String[] args){
        File srcFolder=null;
        ArrayList<File> filesList=new ArrayList<>();
        do{
            System.out.print("Input path to folder: ");
            Scanner input=new Scanner(System.in);
            srcFolder=new File(input.nextLine());

        }while (!srcFolder.exists() || !srcFolder.isDirectory() );
        System.out.println("Start zip:");
        findAllFilesInDir(srcFolder,filesList);

        ArrayList<File> videoFiles=filter(filesList,new FilenameFilterExt(VIDEO_EXT_ARRAY));
        ArrayList<File> audioFiles=filter(filesList,new FilenameFilterExt(AUDIO_EXT_ARRAY));
        ArrayList<File> imageFiles=filter(filesList,new FilenameFilterExt(IMAGE_EXT_ARRAY));

        try {
            System.out.print("\t"+DST_ZIP_VIDEOS+" ... ");
            zip(new File(srcFolder, DST_ZIP_VIDEOS), videoFiles);
            System.out.print("Done \n\t"+DST_ZIP_AUDIOS+" ... ");
            zip(new File(srcFolder,DST_ZIP_AUDIOS),audioFiles);
            System.out.print("Done \n\t"+DST_ZIP_IMAGES+" ... ");
            zip(new File(srcFolder,DST_ZIP_IMAGES),imageFiles);
            System.out.print("Done\nFinish zip");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path getRelativizPath(String basePath,String absolutePath){
        return Paths.get(basePath).relativize(Paths.get(absolutePath));
    }

    private static void write(InputStream inputStream,ZipOutputStream outputStream) throws  IOException{
        byte[] buff=new byte[2048];
        int len;
        while ((len=inputStream.read(buff))>=0){
            outputStream.write(buff,0,len);
        }
        inputStream.close();
    }

    public static void zip(File dst,ArrayList<File> src) throws IOException{
        ZipOutputStream zipOutputStream=new ZipOutputStream(new FileOutputStream(dst));
        for(File file:src) {
            zipOutputStream.putNextEntry(new ZipEntry(getRelativizPath(dst.getParent(),file.getPath()).toString()));
            write(new FileInputStream(file),zipOutputStream);
        }
        zipOutputStream.close();
    }

    public static void findAllFilesInDir(File dir,ArrayList<File> filesList){
        File[] list=dir.listFiles();
        for (File file:list){
            if(file.isDirectory()){
                findAllFilesInDir(file.getAbsoluteFile(),filesList);
            }else {
                filesList.add(file);
            }
        }
        return;
    }

    public static ArrayList<File> filter(ArrayList<File> fileArrayList,FilenameFilter filenameFilter){
        ArrayList<File> filesList=new ArrayList<>();
        for (File file:fileArrayList){
            if(filenameFilter.accept(file,file.getName())){
                filesList.add(file);
            }
        }
        return filesList;
    }

    public static class FilenameFilterExt implements FilenameFilter {

        protected String[] extArray=null;

        public FilenameFilterExt(String[] extArray){
            this.extArray=extArray;
        }
        @Override
        public boolean accept(File dir, String name) {
            String[] nameParts=name.split("\\.");
            if(nameParts.length<2)
                return false;
            String fileExt=nameParts[nameParts.length-1];
            return Arrays.asList(extArray).contains(fileExt);
        }

    }

}
