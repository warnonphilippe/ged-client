package be.phw.gedclient.client.document.ticket;

import java.time.ZonedDateTime;

public class TicketMerge {

    protected Long id;
    protected ZonedDateTime startDate;
    protected ZonedDateTime endDate;
    protected Boolean ok;
    protected String errorMsg;

    private String mergeType;
    private String templateRepo;
    private String templatePath;
    private String templateId;
    private String destRepo;
    private String destPath;
    private String destId;
    private String destMimeType;
    private String destDescription;

    public TicketMerge() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getOk() {
        return ok;
    }

    public void setOk(Boolean ok) {
        this.ok = ok;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getMergeType() {
        return mergeType;
    }

    public void setMergeType(String mergeType) {
        this.mergeType = mergeType;
    }

    public String getTemplateRepo() {
        return templateRepo;
    }

    public void setTemplateRepo(String templateRepo) {
        this.templateRepo = templateRepo;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getDestRepo() {
        return destRepo;
    }

    public void setDestRepo(String destRepo) {
        this.destRepo = destRepo;
    }

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public String getDestId() {
        return destId;
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }

    public String getDestMimeType() {
        return destMimeType;
    }

    public void setDestMimeType(String destMimeType) {
        this.destMimeType = destMimeType;
    }

    public String getDestDescription() {
        return destDescription;
    }

    public void setDestDescription(String destDescription) {
        this.destDescription = destDescription;
    }

    @Override
    public String toString() {
        return "TicketMerge{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", ok=" + ok +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
