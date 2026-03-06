package com.mysawit.mysawit_auth.exception;

public class MandorSertifMissingException extends RuntimeException {
    public MandorSertifMissingException() {
        super("Nomor sertifikasi mandor is required for MANDOR role");
    }
}
