package com.mysawit.mysawit_auth.exception;

import java.io.Serial;

public class MandorSertifMissingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public MandorSertifMissingException() {
        super("Nomor sertifikasi mandor is required for MANDOR role");
    }
}
