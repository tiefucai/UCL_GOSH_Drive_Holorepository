package HoloBackend;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;


import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import java.util.*;


@Path("/patient")
public class RetrievePatient {

    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;" + "AccountName=team19;" + "AccountKey=R41Pkh1GNKJldc+RApCn6q46trUWzlfDhIKs0axBwxgwgszOWdHoM+HiiNzZeSsMBJf/s0+fA1gUANPAAJGpSA==;EndpointSuffix=core.windows.net;";

    CloudStorageAccount storageAccount;
    CloudBlobClient blobClient = null;
    CloudBlobContainer container = null;

    @Path("/{id_of_patient}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPatientCase(@PathParam("id_of_patient") String id_of_patient) {

        id_of_patient = id_of_patient + ".mhif.xml";
        try {
            this.storageAccount = CloudStorageAccount.parse(storageConnectionString);
            this.blobClient = storageAccount.createCloudBlobClient();
            this.container = blobClient.getContainerReference("patientdata");
            this.container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());
            CloudBlockBlob blob = this.container.getBlockBlobReference(id_of_patient);
            XMLReader reader = new XMLReader();
            if (blob.exists()) {
                File temp_file = File.createTempFile("temp", "xml");
                File Downloaded = new File(temp_file.getParent(), "DownloadedXml.xml");
                blob.downloadToFile(Downloaded.getAbsolutePath());
                Map<String, String> map = reader.ReadPatientDetail(Downloaded);
                JSONObject object = new JSONObject();
                object.put("patientId", map.get("patientId"));
                object.put("revisionNumber", map.get("revisionNumber"));
                object.put("lastEditedBy", map.get("lastEditedBy"));
                object.put("timeOfLastEdit", map.get("timeOfLastEdit"));
                object.put("gender", map.get("gender"));
                object.put("firstName", map.get("firstName"));
                object.put("lastName", map.get("lastName"));
                object.put("DateOfBirth", map.get("DateOfBirth"));
                object.put("age", map.get("age"));
                return object.toString();
            } else {
                return "such patient not found from the database";
            }
        } catch (StorageException ex) {
            System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s", ex.getHttpStatusCode(), ex.getErrorCode()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        return null;
    }

    @Path("/search_mesh/{id_of_patient}/{searching_detail}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String GetRawData(@PathParam("id_of_patient") String id,
                             @PathParam("searching_detail") String searching_detail) {
        id = id + ".mhif.xml";
        try {
            this.storageAccount = CloudStorageAccount.parse(storageConnectionString);
            this.blobClient = storageAccount.createCloudBlobClient();
            this.container = blobClient.getContainerReference("patientdata");
            this.container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());
            CloudBlockBlob target_blob = this.container.getBlockBlobReference(id);
            if (target_blob == null) {
                return "such patient not found from the database";
            }
            XMLReader reader = new XMLReader();
            File temp_xml = File.createTempFile("temp", "xml");
            File Downloaded = new File(temp_xml.getParent(), "DownloadedXml.xml");
            target_blob.downloadToFile(Downloaded.getAbsolutePath());
            JSONArray array = new JSONArray();

            for (Map<String, String> map : reader.ReadMeshes(Downloaded)) {
                if (map.containsValue(searching_detail)) {
                    array.put(map);
                }
            }
            if (array.length() != 0) {
                return array.toString();
            } else {
                return "no mesh include this " + searching_detail + " element";
            }


        } catch (StorageException ex) {
            System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s", ex.getHttpStatusCode(), ex.getErrorCode()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Path("/search_annotation/{id_of_patient}/{search_detail}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String search_annotation(@PathParam("id_of_patient") String id,
                                    @PathParam("search_detail") String search_detail) {

        id = id + ".mhif.xml";
        try {
            this.storageAccount = CloudStorageAccount.parse(storageConnectionString);
            this.blobClient = storageAccount.createCloudBlobClient();
            this.container = blobClient.getContainerReference("patientdata");
            this.container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());
            CloudBlockBlob target_blob = this.container.getBlockBlobReference(id);
            if (target_blob == null) {
                return "such patient not found from the database";
            }
            XMLReader reader = new XMLReader();
            File temp_xml = File.createTempFile("temp", "xml");
            File Downloaded = new File(temp_xml.getParent(), "DownloadedXml.xml");
            target_blob.downloadToFile(Downloaded.getAbsolutePath());
            JSONArray array = new JSONArray();

            for (Map<String, String> map : reader.ReadAnnotations(Downloaded)) {
                if (map.containsValue(search_detail)) {
                    array.put(map);
                }
            }
            if (array.length() != 0) {
                return array.toString();
            } else {
                return "no annotation include this" + search_detail + "element";
            }


        } catch (StorageException ex) {
            System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s", ex.getHttpStatusCode(), ex.getErrorCode()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Path("/search/{search_detail}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String searchSingl_detail(@PathParam("search_detail") String search_detail) {
        try {
            this.storageAccount = CloudStorageAccount.parse(storageConnectionString);
            this.blobClient = storageAccount.createCloudBlobClient();
            this.container = blobClient.getContainerReference("patientdata");
            this.container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());
            JSONArray array = new JSONArray();
            XMLReader reader = new XMLReader();

            for(ListBlobItem blobItem : this.container.listBlobs())
            {
                if(blobItem instanceof CloudBlockBlob)
                {
                    CloudBlockBlob blockBlob = (CloudBlockBlob) blobItem;
                    File temp_xml = File.createTempFile("temp", "xml");
                    File Downloaded = new File(temp_xml.getParent(), "DownloadedXml.xml");
                    blockBlob.downloadToFile(Downloaded.getAbsolutePath());
                    Integer integer = new Integer(reader.ReadPatientDetail(Downloaded).get("age"));
                    int age = integer.intValue();
                    if (number_checker(search_detail) != null) {
                        if (number_checker(search_detail).get(0) <= age && number_checker(search_detail).get(1) >= age)
                        {
                            array.put(reader.ReadPatientDetail(Downloaded));
                        }

                    }
                    if(detail_Checker(search_detail,Downloaded))
                    {
                        array.put(reader.ReadPatientDetail(Downloaded));
                    }
                }
            }
            if (array.length() != 0)
            {
                return array.toString();
            }
            return "no patient fit your requirement";
        }catch (StorageException ex) {
            System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s", ex.getHttpStatusCode(), ex.getErrorCode()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Path("/search_2/{search_detail}/{search_detail2}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String searchSingl_detail(@PathParam("search_detail") String search_detail,
                                     @PathParam("search_detail2") String search_detail2) {
        try {
            this.storageAccount = CloudStorageAccount.parse(storageConnectionString);
            this.blobClient = storageAccount.createCloudBlobClient();
            this.container = blobClient.getContainerReference("patientdata");
            this.container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());
            JSONArray array = new JSONArray();
            XMLReader reader = new XMLReader();
            for(ListBlobItem blobItem : this.container.listBlobs()) {
                if (blobItem instanceof CloudBlockBlob) {
                    CloudBlockBlob blockBlob = (CloudBlockBlob) blobItem;
                    File temp_xml = File.createTempFile("temp", "xml");
                    File Downloaded = new File(temp_xml.getParent(), "DownloadedXml.xml");
                    blockBlob.downloadToFile(Downloaded.getAbsolutePath());
                    Integer integer = new Integer(reader.ReadPatientDetail(Downloaded).get("age"));
                    int age = integer.intValue();
                    if(number_checker(search_detail) != null)
                    {
                        if (number_checker(search_detail).get(0) <= age && number_checker(search_detail).get(1) >= age && detail_Checker(search_detail2,Downloaded))
                        {
                            array.put(reader.ReadPatientDetail(Downloaded));
                        }
                    }else if(detail_Checker(search_detail,Downloaded) && detail_Checker(search_detail2,Downloaded))
                    {
                        array.put(reader.ReadPatientDetail(Downloaded));
                    }else if(number_checker(search_detail2) != null)
                    {
                        if (number_checker(search_detail2).get(0) <= age && number_checker(search_detail2).get(1) >= age && detail_Checker(search_detail,Downloaded))
                        {
                            array.put(reader.ReadPatientDetail(Downloaded));
                        }
                    }


                }
            }
            if(array.length() != 0)
            {
                return array.toString();
            }

            return "no patient fit your requirement";
        }catch (StorageException ex) {
            System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s", ex.getHttpStatusCode(), ex.getErrorCode()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    @Path("search_3/{detail1}/{detail2}/{detail3}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String search_3(@PathParam("detail1") String detail1,
                           @PathParam("detail2") String detail2,
                           @PathParam("detail3") String detail3)
    {
        try {
            this.storageAccount = CloudStorageAccount.parse(storageConnectionString);
            this.blobClient = storageAccount.createCloudBlobClient();
            this.container = blobClient.getContainerReference("patientdata");
            this.container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());
            JSONArray array = new JSONArray();
            XMLReader reader = new XMLReader();
            for(ListBlobItem blobItem : this.container.listBlobs())
            {
                if(blobItem instanceof CloudBlockBlob)
                {
                    CloudBlockBlob blockBlob = (CloudBlockBlob) blobItem;
                    File temp_xml = File.createTempFile("temp", "xml");
                    File Downloaded = new File(temp_xml.getParent(), "DownloadedXml.xml");
                    blockBlob.downloadToFile(Downloaded.getAbsolutePath());
                    Integer integer = new Integer(reader.ReadPatientDetail(Downloaded).get("age"));
                    int age = integer.intValue();
                    if(number_checker(detail1) != null)
                    {
                        if (number_checker(detail1).get(0) <= age && number_checker(detail1).get(1) >= age && detail_Checker(detail2,Downloaded) &&detail_Checker(detail3,Downloaded))
                        {
                            array.put(reader.ReadPatientDetail(Downloaded));
                        }
                    }else if(detail_Checker(detail1,Downloaded) && detail_Checker(detail2,Downloaded) && detail_Checker(detail3,Downloaded))
                    {
                        array.put(reader.ReadPatientDetail(Downloaded));
                    }else if(number_checker(detail2) != null)
                    {
                        if (number_checker(detail2).get(0) <= age && number_checker(detail2).get(1) >= age && detail_Checker(detail1,Downloaded) &&detail_Checker(detail3,Downloaded))
                        {
                            array.put(reader.ReadPatientDetail(Downloaded));
                        }
                    }else if (number_checker(detail3) != null)
                    {
                        if (number_checker(detail3).get(0) <= age && number_checker(detail3).get(1) >= age && detail_Checker(detail2,Downloaded) &&detail_Checker(detail1,Downloaded))
                        {
                            array.put(reader.ReadPatientDetail(Downloaded));
                        }
                    }
                }
            }
            if(array.length() != 0)
            {
                return array.toString();
            }
        return "no patient fit your requirement";
        }catch (StorageException ex) {
            System.out.println(String.format("Error returned from the service. Http code: %d and error code: %s", ex.getHttpStatusCode(), ex.getErrorCode()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }



    private boolean detail_Checker(String string , File file)
    {
        XMLReader reader = new XMLReader();
        if(reader.ReadPatientDetail(file).containsValue(string))
        {
            return true;
        }

        for(Map<String, String> map : reader.ReadMeshes(file))
        {
            if (map.containsValue(string))
            {
                return true;
            }
        }
        for(Map<String,String> map : reader.ReadAnnotations(file))
        {
            if(map.containsValue(string))
            {
                return true;
            }
        }
        return false;
    }

    private List<Integer> number_checker(String string )
    {
        int index;
        int upper_number = 0;
        int lower_number = 0;
        if(string.contains("-"))
        {
            index = string.indexOf("-");
            lower_number = Integer.parseInt(string.substring(0,index));
            upper_number = Integer.parseInt(string.substring(index+1,string.length()));
        }else
        {
            return null;
        }
       ArrayList<Integer> list = new ArrayList<>();
        list.add(lower_number);
        list.add(upper_number);
        return list;
    }
}