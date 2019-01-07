package be.phw.gedclient.client.document;

import java.io.File;
import java.io.IOException;
import java.util.*;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.form.FormData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;

import be.phw.gedclient.client.document.config.DocumentMainFeignConfiguration;
import be.phw.gedclient.client.document.ticket.TicketConversion;
import be.phw.gedclient.client.document.ticket.TicketInfo;
import be.phw.gedclient.client.document.ticket.TicketMerge;

@FeignClient(name = "GEDSERVER", configuration = DocumentMainFeignConfiguration.class)
public interface DocumentMainClient {

    /**
     * Création d'un répertoire
     * @param parentId id répertoire parent sous lequel créer le nouveau répertoire
     * @param name nom du nouveau répertoire
     * @return
     */
    @RequestLine("GET /createFolder?parent={parent}&name={name}")
    public ResponseEntity<CivadisDocument> createFolder(
        @Param("parent") String parentId,
        @Param("name") String name);

    /**
     * Obtenir les informations à propos d'un document
     * @param id id du document
     * @return
     */
    @RequestLine("GET /document-detail?id={id}&path={path}&latest={latest}")
    public ResponseEntity<CivadisDocument> getDocument(
        @Param("id") String id,
        @Param("path") String path,
        @Param("latest") Boolean latest);

    /**
     * Obtenir les informations à propos de toutes les versions d'un document
     * @param id id du document
     * @return
     */
    @RequestLine("GET /document-all-versions?id={id}")
    public ResponseEntity<List<CivadisDocument>> getDocumentAllVersions(
        @Param("id") String id);

    /**
     * Obtenir les infos des documents sous un répertoire parent
     * @param parent id du répertoire parent
     * @return
     */
    @RequestLine("GET /documents?parent={parent}")
    public ResponseEntity<List<CivadisDocument>> getDocuments(
        @Param("parent") String parent);

    /**
     * Rechercher les documents par une recherche dans leur contenu, 3 types de recherche :
     *  - OR : les documents doivent contenir au moins 1 des mots (par défaut)
     *  - AND : les documents doivent contenir tous lers mots
     *  - PHRASE : les documents doivent contenir la phrase exacte
     * @param parent id réperoire parent sous lequel rechercher (facultatif)
     * @param valueContent mot(s) à rechercher, séparés par des espaces
     * @param searchTypeContent type de recherche (OR, AND, PHRASE)
     * @return
     */
    @RequestLine("GET /documents-by-content?parent={parent}&valueContent={valueContent}&searchTypeContent={searchTypeContent}")
    public ResponseEntity<List<CivadisDocument>> getDocumentsByContent(
        @Param("parent") String parent,
        @Param("valueContent") String valueContent,
        @Param("searchTypeContent") String searchTypeContent);

    /**
     * Rechercher les documents par une recherche dans leurs tags, 2 types de recherche :
     *  - OR : les documents doivent être associés à au moins 1 des tags
     *  - AND : les documents doivent être associés à tous les tags
     * @param parent id réperoire parent sous lequel rechercher (facultatif)
     * @param valueTags mot(s) à rechercher, séparés par des espaces
     * @param searchTypeTags type de recherche (OR, AND)
     * @return
     */
    @RequestLine("GET /documents-by-tags?parent={parent}&valueTags={valueTags}&searchTypeTags={searchTypeTags}")
    public ResponseEntity<List<CivadisDocument>> getDocumentsByTags(
        @Param("parent") String parent,
        @Param("valueTags") String valueTags,
        @Param("searchTypeTags") String searchTypeTags);

    /**
     * Recherche des documents par une recherche sur leur contenu et selon leurs tags
     * 3 types de recherche du contenu :
     *      - OR : les documents doivent contenir au moins 1 des mots (par défaut)
     *      - AND : les documents doivent contenir tous lers mots
     *      - PHRASE : les documents doivent contenir la phrase exacte
     * 2 types de recherche selon les tags:
     *      - OR : les documents doivent être associés à au moins 1 des tags
     *      - AND : les documents doivent être associés à tous les tags
     * Si des conditions de recherche sont précisées pour le contenu et pour les tags, les documements retenus devront satisfaire au 2 recherches
     * @param parent id réperoire parent sous lequel rechercher (facultatif)
     * @param valueContent mot(s) à rechercher, séparés par des espaces
     * @param searchTypeContent type de recherche (OR, AND, PHRASE)
     * @param valueTags tag(s) à rechercher, séparés par des espaces
     * @param searchTypeTags type de recherche (OR, AND)
     * @return
     */
    @RequestLine("GET /documents-by-content-tags?parent={parent}&valueContent={valueContent}&searchTypeContent={searchTypeContent}&valueTags={valueTags}&searchTypeTags={searchTypeTags}")
    public ResponseEntity<List<CivadisDocument>> searchDocuments(
        @Param("parent") String parent,
        @Param("valueContent") String valueContent,
        @Param("searchTypeContent") String searchTypeContent,
        @Param("valueTags") String valueTags,
        @Param("searchTypeTags") String searchTypeTags);

    /**
     * Download un document selon son id
     * @param id id du document
     * @param latest indique si on dowload la dernière version du document, sinon celle associée au no de version présent dans l'id
     * @return
     */
    @RequestLine("GET /download-document?id={id}&latest={latest}")
    public byte[] downloadDocument(
        @Param("id") String id,
        @Param("latest") Boolean latest);

    /**
    * Upload d'un document 
    * @param file fichier à uploader
    * @param parenPath path sous lequel placé le fichier
    * @param description description du fichier
    * @return
    */
    @RequestLine("POST /upload-document")
    @Headers("Content-Type: multipart/form-data")
    public ResponseEntity<CivadisDocument> uploadDocument(@Param("file") FormData file, @Param("parent") String parenPath, @Param("description") String description);
    
    /**
     * Upload d'un document  et converti le document
     * @param file fichier à uploader
     * @param parenPath path sous lequel placé le fichier
     * @param description description du fichier
     * @param targetName fichier cible (le fichier uploadé sera converti dans le type associé à l'extension du targetName)
     * @param engineType type de moteur de conversion (office ou libreoffice)
     * @return
     */
    @RequestLine("POST /upload-convert-document")
    @Headers("Content-Type: multipart/form-data")
    public ResponseEntity<TicketInfo> uploadDocument(
        @Param("file") FormData file,
        @Param("parent") String parenPath,
        @Param("description") String description,
        @Param("targetName") String targetName,
        @Param("engineType") String engineType);

    /**
     * Suppression d'un document
     * @param id id du document à supprimé
     */
    @RequestLine("DELETE /delete-document?id={id}")
    public ResponseEntity deleteDocument(
        @Param("id") String id);


    /***********************************************************/
    /** Edit-service *******************************************/
    /***********************************************************/

    /**
     * Retourne l'url à utiliser pour une édition en ligne du document (protocol wopi)
     * @param id id du document
     * @param mergeFieldsId champs de fusion
     * @param readOnly indique si édition en readonly
     * @return
     */
    @RequestLine("GET /editUrl?id={id}&mergeFieldsId={mergeFieldsId}&readOnly={readOnly}")
    @Headers("Accept: text/plain")
    public String getEditUrl(
        @Param("id") String id,
        @Param("mergeFieldsId") String mergeFieldsId,
        @Param("readOnly") Boolean readOnly);

    /**
     * Edition du document dans un libre office (protocol webdav)
     * @param id id du documemt
     * @param mergeFieldsId champs de fusion
     * @param readOnly indique si edition en readonly
     * @return
     */
    @RequestLine("GET /webdavOdt?id={id}&mergeFieldsId={mergeFieldsId}&readOnly={readOnly}")
    public byte[] getWebdavOdt(
        @Param("id") String id,
        @Param("mergeFieldsId") String mergeFieldsId,
        @Param("readOnly") Boolean readOnly);

    /**
     * Télécharge l'extension à installer dans libre office
     * @return
     */
    @RequestLine("GET /webdavOdtExt")
    public byte[] getWebdavOdt();

    /**
     * Edition du document dans MS Word (protocol webdav)
     * @param id id du documemt
     * @param mergeFieldsId champs de fusion
     * @param readOnly indique si edition en readonly
     * @return
     */
    @RequestLine("GET /webdavDocm?id={id}&mergeFieldsId={mergeFieldsId}&readOnly={readOnly}")
    public byte[] getWebdavDocm(
        @Param("id") String id,
        @Param("mergeFieldsId") String mergeFieldsId,
        @Param("readOnly") Boolean readOnly);


    /***********************************************************/
    /** Conversion-service *************************************/
    /***********************************************************/
    /**
     * Conversion d'un document
     * @param id id du document à convertir
     * @param targetName nom et extension cible
     * @return
     * @throws Exception
     */
    @RequestLine("GET /convert?id={id}&targetName={targetName}&engineType={engineType}")
    public ResponseEntity<TicketInfo> convert(
        @Param("id") String id,
        @Param("targetName") String targetName,
        @Param("engineType") String engineType) throws Exception;

    @RequestLine("POST /convertStream")
    @Headers("Content-Type: multipart/form-data")
    public byte[] convert(
        @Param("file") File file,
        @Param("sourceMimeType") String sourceMimeType,
        @Param("destMimeType") String destMimeType,
        @Param("engineType") String engineType) throws IOException;

    //@GetMapping("/conversion/supportedFormats")
    //public ResponseEntity<Set<SupportedFormat>> getSupportedFormats(@Param("input") final String input);


    /***********************************************************/
    /** Merge-service ******************************************/
    /***********************************************************/
    /**
     * Fusion d'un modèle de document avec des données
     * @return
     * @throws Exception
     */
    @RequestLine("POST /merge?templateId={templateId}&mergeType={mergeType}&destPath={destPath}")
    @Headers("Content-Type: application/json")
    public ResponseEntity<TicketInfo> merge(
        @Param("templateId") String templateId,
        @Param("mergeType") String mergeType,
        @Param("destPath") String destPath,
        Map<String, Object> data
    ) throws Exception;


    /***********************************************************/
    /** Ticket-service ******************************************/
    /***********************************************************/
    @RequestLine("GET /ticket-conversions/{id}")
    public ResponseEntity<TicketConversion> getTicketConversion(@Param("id") Long id);

    @RequestLine("GET /ticket-merges/{id}")
    public ResponseEntity<TicketMerge> getTicketMerge(@Param("id") Long id);



    /***********************************************************/
    /** CheckIn - Checkout *************************************/
    /***********************************************************/

    /**
     * Check out la dernière version du document
     * @param id
     * @param path
     * @return
     * */
    @RequestLine("GET /checkout-document?id={id}&path={path}")
    public ResponseEntity<CivadisDocument> checkOutLatestDocument(
        @Param("id") String id,
        @Param("path") String path);

    /**
     * Indique si la dernière version d'un document est lockée
     * @param id id du document
     * @return
     */
    @RequestLine("GET /is-document-checked-out?id={id}&path={path}")
    public ResponseEntity<Boolean> isDocumentCheckedOut(
        @Param("id") String id,
        @Param("path") String path);

    /**
     * Annule le lock de la dernière version d'un document
     * @param id id du document
     * @return
     */
    @RequestLine("GET /cancel-document-checkout?id={id}")
    public ResponseEntity<Boolean> cancelDocumentCheckOut(
        @Param("id") String id);
    
    /**
     * Upload d'un document et effectue le checkin
     * @param file fichier à uploader
     * @param parenPath path sous lequel placé le fichier
     * @param description description du fichier
     */
    @RequestLine("POST /upload-checkin-document")
    @Headers("Content-Type: multipart/form-data")
    public ResponseEntity<CivadisDocument> uploadCheckInDocument(
        @Param("file") FormData file,
        @Param("id") String id,
        @Param("description") String description);


    /***********************************************************/
    /** Tests... ***********************************************/
    /***********************************************************/

    @RequestLine("GET /token")
    @Headers("Accept: text/plain")
    public String hello();

    @RequestLine("GET /test/test-documents?parent={parent}")
    public ResponseEntity<List<CivadisDocument>> testDocuments(@Param(value = "parent") String parent);

    @RequestLine("POST /test/test-post-document")
    @Headers("Content-Type: application/json")
    public ResponseEntity<CivadisDocument> testPostDocument(CivadisDocument document);

    @RequestLine("POST /test/test-upload-document")
    @Headers("Content-Type: multipart/form-data")
    public ResponseEntity<CivadisDocument> testUploadDocument(@Param("file") FormData file,
            @Param("parent") String parenPath);

    @RequestLine("GET /test/test-download-document?id={id}")
    // @Headers("Accept: application/octect-stream")
    public byte[] testDownloadDocument(@Param("id") String id);

}

