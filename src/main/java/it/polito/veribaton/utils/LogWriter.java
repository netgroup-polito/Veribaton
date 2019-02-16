package it.polito.veribaton.utils;

import com.google.gson.Gson;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * LogWriter is a utility class for logging XML and JSON objects to file
 */
public class LogWriter {

    /**
     * Writes JSON object to file. If the file is not empty, it will be overwritten
     *
     * @param o    the object to be written
     * @param path the filesystem path for the log file
     */
    public static void logJson(Object o, String path){
        try {
            Gson gson = new Gson();
            String nfvJson = gson.toJson(o);
            File outJson = new File(path);
            if (outJson.getParentFile() != null) {
                outJson.getParentFile().mkdirs();
            }
            outJson.createNewFile();
            FileOutputStream os = new FileOutputStream(outJson);
            os.write(nfvJson.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes XML object to file. If the file is not empty, it will be overwritten
     *
     * @param o    the object to be written
     * @param path the filesystem path for the logfile
     */
    public static void logXml(Object o, String path){
        try {
            File f = new File(path);
            if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
            final JAXBContext jaxbContext = JAXBContext.newInstance(o.getClass());
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(o, f);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }
}
