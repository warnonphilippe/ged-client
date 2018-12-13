package be.phw.gedclient.web.rest;

import feign.form.FormData;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.phw.gedclient.client.document.CivadisDocument;
import be.phw.gedclient.client.document.DocumentMainClient;

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

    @GetMapping("/upload")
    public ResponseEntity<CivadisDocument> upload() throws IOException {
        return client.testUploadDocument(getFormData(new File("/home/vagrant/testdoc")), "parenPath"); ///Users/philippe/kafka.pptx
    }

    @GetMapping("/download")
    public ResponseEntity<Boolean> download() throws IOException {
        byte[] bytes = client.testDownloadDocument("id");
        try (FileOutputStream out = new FileOutputStream(new File("/home/vagrant/testdownloaded"))){
            out.write(bytes);
        }
        return ResponseEntity.ok(true);
    }

    /**
     * Crée un FormData qui encapsule une fichier afin de permettre son transfert
     * 
     * @param file fichier
     * @return
     * @throws IOException
     */
    private FormData getFormData(File file) throws IOException {
        // bytes array
        byte[] bytesArray = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray); // read file into bytes[]
        fis.close();

        // content type
        String fileType = Files.probeContentType(file.toPath());

        // create formData
        return new FormData(fileType, bytesArray);
    }

}
