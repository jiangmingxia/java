package com.hp.jmx.qc.rest;

public class QCRestWSException extends RuntimeException {

    /** TODO Comment for <code>serialVersionUID</code>. */

    private static final long serialVersionUID = 4974368501457665221L;

    public static final String QC_REST_EXCEPTION_ENTITY_NOT_FOUND = "qccore.entity-not-found";

    public static final String QC_REST_EXCEPTION_LOCK_FAILURE = "qccore.lock-failure";

    private String exceptionId;

    private String exceptionTitle;

    public String getExceptionId() {
        return this.exceptionId;
    }

    public void setExceptionId(final String exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getExceptionTitle() {
        return this.exceptionTitle;
    }

    public void setExceptionTitle(final String exceptionTitle) {
        this.exceptionTitle = exceptionTitle;
    }

    public QCRestWSException(final String exceptionId, final String exceptionTitle) {
        super(exceptionTitle);
        this.exceptionId = exceptionId;
        this.exceptionTitle = exceptionTitle;
    }

    public QCRestWSException(final String msg, final Throwable cause) {
        super(msg, cause);
    }

    public QCRestWSException(final String msg) {
        super(msg);
    }
}
