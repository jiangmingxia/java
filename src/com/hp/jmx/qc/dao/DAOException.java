package com.hp.jmx.qc.dao;

public class DAOException extends RuntimeException {

    private static final long serialVersionUID = 164322369444381903L;

    public DAOException(String msg) {
        super(msg);
    }
    
    public DAOException(String msg, Throwable t) {
        super(msg, t);
    }
}
