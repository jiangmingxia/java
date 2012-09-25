package com.hp.jmx.qc.rest;

public class EntityNotFoundException extends QCRestWSException {

    private static final long serialVersionUID = -6702349741704620302L;

    public EntityNotFoundException(final String exceptionTitle) {
        super(QCRestWSException.QC_REST_EXCEPTION_ENTITY_NOT_FOUND, exceptionTitle);
    }

}
