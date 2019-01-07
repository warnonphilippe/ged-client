package be.phw.gedclient.web.rest;

import feign.form.FormData;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.phw.gedclient.client.document.CivadisDocument;
import be.phw.gedclient.client.document.DocumentMainClient;
import be.phw.gedclient.client.document.ticket.TicketInfo;
import be.phw.gedclient.client.document.ticket.TicketTools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

@RestController
@RequestMapping("/test/document")
public class TestDocumentResource {

    private DocumentMainClient client;

    public TestDocumentResource(DocumentMainClient client) {
        this.client = client;
    }

    @GetMapping("/get")
    public ResponseEntity<List<CivadisDocument>> list() {
        return client.testDocuments("parent");
    }

    // sert uniquement à déclencher le test via browser, donc get suffit
    @GetMapping("/post")
    public ResponseEntity<CivadisDocument> post() throws IOException {
        CivadisDocument doc = new CivadisDocument();
        doc.setName("docTest");
        return client.testPostDocument(doc);
    }

    @GetMapping("/upload")
    public ResponseEntity<CivadisDocument> upload() throws IOException {
        return client.testUploadDocument(getFormData(new File("/Users/philippe/test.txt")), "parenPath"); /// home/vagrant/testdoc
    }

    @GetMapping("/download")
    public ResponseEntity<Boolean> download() throws IOException {
        byte[] bytes = client.testDownloadDocument("id");
        try (FileOutputStream out = new FileOutputStream(new File("/Users/philippe/testdownloaded"))) { /// home/vagrant/testdownloaded
            out.write(bytes);
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping()
    public ResponseEntity<Boolean> test() throws IOException {

        testUpload("/Users/philippe/tmp/test1.docx");
        testBase();
        testDownload("92b4002e-4d13-48a7-b9ba-9c16467db27e;1.0", "/Users/philippe/tmp/test_donwloaded.docx");
        
        testMerge();

        testAllConvert("test1", "docx", "", null);

        return ResponseEntity.ok(true);
    }

    private void testBase(){

        //get documents
        ResponseEntity<List<CivadisDocument>> resp = client.getDocuments("/Sites/jhipster/documentLibrary/testapp");
        if (resp.hasBody()){
            resp.getBody().stream().forEach(doc -> { 
                System.out.println(doc.toString()); 
            });
        }

        ResponseEntity<List<CivadisDocument>> resp0 = client.getDocumentsByContent("/Sites/jhipster/documentLibrary/testapp", "jhipster toto", "OR");
        System.out.println("**************get content 'jhipster'");
        if (resp0.hasBody()){
            resp0.getBody().stream().forEach(doc -> { System.out.println(doc.toString()); });
        }

        //get documents detail
        
        ResponseEntity<CivadisDocument> resp1 = client.getDocument("92b4002e-4d13-48a7-b9ba-9c16467db27e;1.0", null,true);
        if (resp1.hasBody()){
            System.out.println("**************get by id");
            System.out.println(resp1.getBody().toString());
        }
        

    }

    private void testDownload(String id, String target) throws IOException {

        //download
        byte[] bytes = client.downloadDocument(id, true);
        //copy data to file
        java.nio.file.Files.write(
            new File(target).toPath(),
            bytes,
            StandardOpenOption.CREATE_NEW);

    }

    private void testUpload(String path){
            File file = new File(path);
            try{
                ResponseEntity<CivadisDocument> respUpload = client.uploadDocument(getFormData(file), "/Sites/jhipster/documentLibrary/testapp", "Description");
                if (respUpload.hasBody()){
                    System.out.println(respUpload.getBody());
                }
            } catch (Exception ex){
                System.err.println(ex.getMessage());
            }
    }

    private void testMerge() {

        try {
            //prendre le contenu json de permis.json
            InputStream inJson = this.getClass().getResourceAsStream("/be/civadis/demo/permis.json");

            //convertir en Map de données
            Map<String, Object> data = new ObjectMapper().readValue(inJson, new TypeReference<HashMap<String, Object>>() {});

            //appel du merge
            //963e406d-dbdd-40f0-a045-cad31ae9300b;1.0
            //d14396b0-9bc1-4adf-818a-059f21c47d5f;1.0
            //mergeType : pdf-form, jasper-json,...
            System.out.println("call merge");
            //TODO : def id du template
            ResponseEntity<TicketInfo> resp = client.merge("98e314eb-6ba3-442a-9f6d-049416649efa;1.0", "jasper-json", "/Sites/jhipster/documentLibrary/testapp/result_merge.pdf", data);

            if (resp.hasBody()){
                System.out.println("Ticket merge " + resp.getBody().getTicketId());
                TicketTools.whenTicketMergeHandled(client, resp.getBody().getTicketId(),
                    ticketMerge -> {
                        System.out.println("Merged document : " + ticketMerge.getDestId());
                        //testDownload(ticketMerge.getDestId(), "converted.pdf");
                    },
                    ticketMerge -> {
                        System.out.println("Error : " + ticketMerge.getErrorMsg());
                    });
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * upload [filename].[ext]
     * convertit [filename].[ext] -> [filename]_converted.pdf
     * upload et convertit [filename].[ext] en [filename]_uploaded.pdf
     *
     * résultat sur alfresco :
     * - eau.rtf
     * - eau_converted.pdf
     * - eau_uploaded.pdf
     * local :
     * - eau_local_convert.pdf
     *
     */

    private void testAllConvert(String fileName, String ext, String idx, String engineType) {

        try {

            File file = new File("/Users/philippe/tmp/" + fileName + idx + "." + ext);

            //upload

            System.out.println(idx + " upload sans conversion");
            try{
                ResponseEntity<CivadisDocument> respUpload = client.uploadDocument(getFormData(file), "/Sites/jhipster/documentLibrary/testapp", "Description");
                if (respUpload.hasBody()){
                    //conversion
                    
                    System.out.println(idx + " convert " + respUpload.getBody().getId());
                    ResponseEntity<TicketInfo> resp = client.convert(respUpload.getBody().getId(), fileName + idx + "_converted.pdf", engineType);

                    if (resp.hasBody()){
                        System.out.println(idx + " Ticket " + respUpload.getBody().toString());
                        TicketTools.whenTicketConversionHandled(client, resp.getBody().getTicketId(),
                            ticketConversion -> {
                                System.out.println("Converted document (1) : " + ticketConversion.getDestId());
                            },
                            ticketConversion -> {
                                System.out.println("Error (1): " + ticketConversion.getErrorMsg());
                            });
                    }

                }
            } catch (Exception ex){
                System.err.println(idx + ex.getMessage());
            }


            try{

                System.out.println(idx + " upload AVEC conversion");
                FormData fd = getFormData(file);
                ResponseEntity<TicketInfo> resp = client.uploadDocument(fd, "/Sites/jhipster/documentLibrary/testapp", null, fileName + idx + "_uploaded.pdf", engineType);

                if (resp.hasBody()){
                    TicketTools.whenTicketConversionHandled(client, resp.getBody().getTicketId(),
                        ticketConversion -> {
                            System.out.println("Converted document (2a) : " + ticketConversion.getDestId());
                            //testDownload(ticketConversion.getDestId(), "d:\\eau_downloaded.pdf");
                        },
                        ticketConversion -> {
                            System.out.println("Error (2a) : " + ticketConversion.getErrorMsg());
                        });
                }


                System.out.println("this comment shoud be BEFORE Converted (2a) message result");

                //idem, mais on veut attendre la fin de l'exécution avant de continuer

                if (resp.hasBody()){
                    TicketTools.wait(
                        TicketTools.whenTicketConversionHandled(client, resp.getBody().getTicketId(),
                            ticketConversion -> {
                                System.out.println("Converted document (2b) : " + ticketConversion.getDestId());
//                                        testDownload(ticketConversion.getDestId(), "d:\\eau_downloaded.pdf");
                            },
                            ticketConversion -> {
                                System.out.println("Error Converting document (2b) : " + ticketConversion.getErrorMsg());
                            }),
                        300
                    );
                }

                System.out.println("this comment shoud be AFTER Converted (2b) message result");


            } catch (Exception ex){
                System.err.println(idx + ex.getMessage());
            }


            try{
                //test conversion d'un fichier pas stocké dans alfresco
                byte[] bytes = client.convert(file, ext, "pdf", engineType);
                File dest = new File("d:\\" + fileName + idx + "_local_convert.pdf");
                Files.write(dest.toPath(), bytes);
            } catch (Exception ex){
                System.err.println(idx + ex.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    /**
     * Crée un FormData qui encapsule une fichier afin de permettre son transfert
     * @param file fichier
     * @return
     * @throws IOException
     */
    public FormData getFormData(File file) throws IOException {
        //bytes array
        byte[] bytesArray = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray); //read file into bytes[]
        fis.close();

        //content type
        String fileType = Files.probeContentType(file.toPath());

        //create formData
        return new FormData(fileType, bytesArray);
    }

}
