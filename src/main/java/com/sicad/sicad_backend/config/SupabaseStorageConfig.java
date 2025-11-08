package com.sicad.sicad_backend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SupabaseStorageConfig {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    public String getStorageUrl() {
        return supabaseUrl + "/storage/v1";
    }

    public String getPublicUrl(String folder, String fileName) {
        return String.format("%s/storage/v1/object/public/%s/%s/%s",
                supabaseUrl, bucketName, folder, fileName);
    }
}