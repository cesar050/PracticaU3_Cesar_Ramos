package com.tercero.controller.excepcion;

public class OverFlowException extends Exception {
    public OverFlowException(){}
    public OverFlowException(String message) {
        super(message);
    }
}
