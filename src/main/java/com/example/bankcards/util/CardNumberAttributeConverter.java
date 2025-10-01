package com.example.bankcards.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * JPA-конвертер для шифрования номера карты при сохранении в БД
 * и расшифровки при чтении. Для простоты используется детерминированный
 * AES/GCM с фиксированным IV (для демо; для продакшена лучше хранить IV на запись).
 */
@Converter(autoApply = false)
public class CardNumberAttributeConverter implements AttributeConverter<String, String> {

    private static final String AES_ALGO = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final byte[] FIXED_IV = new byte[]{
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0A, 0x0B
    }; // 12 bytes IV

    private static SecretKeySpec getKey() {
        // Ключ берём из переменной окружения/системного свойства, иначе dev-ключ
        String keyStr = System.getenv("CARD_ENC_SECRET");
        if (keyStr == null || keyStr.isEmpty()) {
            keyStr = System.getProperty("CARD_ENC_SECRET", "dev-secret-key-for-cards-please-change");
        }
        // Приводим к 256-bit через SHA-256
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("SHA-256").digest(keyStr.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot init encryption key", e);
        }
        return new SecretKeySpec(hash, AES_ALGO);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(128, FIXED_IV);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), spec);
            byte[] enc = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(enc);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt card number", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(128, FIXED_IV);
            cipher.init(Cipher.DECRYPT_MODE, getKey(), spec);
            byte[] dec = cipher.doFinal(Base64.getDecoder().decode(dbData));
            return new String(dec, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt card number", e);
        }
    }
}


