package com.study.board.exception;

public class FileNotPresentException extends RuntimeException {
    public FileNotPresentException(String message) {
        super(message);
    }
}