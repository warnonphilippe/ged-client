package be.phw.gedclient.client.document;

import java.util.*;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import be.phw.gedclient.client.document.config.DocumentMainFeignConfiguration;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.form.FormData;

@FeignClient(name = "GEDSERVER", configuration = DocumentMainFeignConfiguration.class)
public interface DocumentMainClient {

    @RequestLine("GET /test/test-documents?parent={parent}")
    //@GetMapping(path = "/test/test-documents")
    public ResponseEntity<List<CivadisDocument>> testDocuments(@Param(value="parent") String parent);
/*
    @RequestLine("POST /test/upload-document")
    @Headers("Content-Type: multipart/form-data")
    public ResponseEntity<CivadisDocument> testUploadDocument(@Param("file") FormData file,
            @Param("parent") String parenPath);

    @RequestLine("GET /test/test-download-document")
    @Headers("Accept: application/octect-stream")
    public byte[] testDownloadDocument(@Param("id") String id);
*/
}
