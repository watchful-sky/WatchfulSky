package ca.mun.engi5895.stargazer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent orekit = new Intent(this, saveFileIntent.class);
        // add infos for the service which file to download and where to store
        orekit.putExtra(saveFileIntent.FILENAME, "orekit-data.zip");
        //orekit.putExtra(saveFileIntent.URL,
        //        "https://www.orekit.org/forge/attachments/download/677/orekit-data.zip");
        orekit.putExtra(saveFileIntent.URL,
                "https://www.orekit.org/forge/attachments/download/677/orekit-data.zip");
        startService(orekit);

        //String target = getFilesDir().getName() + "orekit-data";
        unzip("orekit-data.zip", getFilesDir().getName());
        File orekitData = new File(getFilesDir().getName() + "/orekit-data");
        DataProvidersManager manager = DataProvidersManager.getInstance();
        try {
            manager.addProvider(new DirectoryCrawler(orekitData));
        } catch (OrekitException e) {
            e.printStackTrace();
        }
    }


    private static void dirChecker(String pathname) {

        File theDir = new File(pathname);

        boolean result = false;
        // if the directory does not exist, create it
        if (!theDir.exists())

        {
            System.out.println("creating directory: " + theDir.getName());
            //result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                System.out.print(se);
            }
            if (result) {
                System.out.println("DIR created");
            }
        }
    }

    public void unzip(String _zipFile, String _targetLocation) {

        //create target location folder if not exist
        byte[] buffer = new byte[1024];
        dirChecker(_targetLocation);
        System.out.println("Got to 1");

        try {
            System.out.println(new File(_targetLocation).getAbsolutePath());
            File zip = new File(getFilesDir(), _zipFile);
            FileInputStream fin = new FileInputStream(zip);
            System.out.println("Got to 2");
            ZipArchiveInputStream zin = new ZipArchiveInputStream(fin);
            System.out.println("Got to 3");
            ArchiveEntry ze = null;
            System.out.println("Got to 4");
            while ((ze = zin.getNextEntry()) != null) {

               /* String fileName = ze.getName();
                System.out.print(fileName);
                File newFile = new File( _targetLocation + File.separator + fileName);
                System.out.println("file unzip : "+ newFile.getAbsoluteFile());
                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                //new File(newFile.getParent()).mkdirs();
                dirChecker(newFile.getName());
                System.out.println("made Dir");
                FileOutputStream fos = new FileOutputStream(newFile);
                System.out.println("made file output stream");
                int len;
                while ((len = zin.read(buffer)) > 0) {
                    System.out.println("in while loop");
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //ze = zin.getNextEntry();
                */
                //create dir if required while unzipping
                if (!ze.isDirectory()) {
                    dirChecker(ze.getName());
                    System.out.println("went into if statement");
               }else if(ze.isDirectory()) {
                    System.out.println("went into else statement");
                    FileOutputStream fout = new FileOutputStream(zip);
                    System.out.println("made fout");
                    int forlooptest = zin.read();
                    System.out.print(forlooptest);
                    for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                        System.out.println("weeeeeee for loop");
                        System.out.print(forlooptest);
                    }
                    System.out.println("out of the while loop");
                    //zin.closeEntry();
                    fout.close();
                    System.out.println("closed stuff");

                }
            }
            zin.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    /*private void unpackZip(String path, String zipname) {
        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
           // File zip = new File(getFilesDir(), zipname);
            is = new FileInputStream(path + "/" + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;
            System.out.print("MADEIT");

            while ((ze = zis.getNextEntry()) != null) {
                // zapis do souboru
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
           // return false;
        }

        //return true;
    }
     */

    public void geoGo(View view) {
        Intent intent = new Intent(this, GeocentricActivity.class);
        startActivity(intent);
    }

    public void helioGo(View view) {
        Intent intent = new Intent(this, HeliocentricActivity.class);
        startActivity(intent);
    }

    public void settingsGo(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
