package com.mysawit.mysawit_auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MandorSertifMissingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public MandorSertifMissingException() {
        super("Nomor sertifikasi mandor is required for MANDOR role");
    }
}
