package com.sigo.asistencia.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // Mantiene compatibilidad con Asistencia
    public Map<String, Object> subirImagen(MultipartFile archivo) throws IOException {
        return subirImagen(archivo, "sigo/asistencia");
    }

    // Permite elegir la carpeta para otros módulos
    @SuppressWarnings("unchecked")
    public Map<String, Object> subirImagen(
            MultipartFile archivo,
            String folder
    ) throws IOException {

        System.out.println("C1. Entrando a CloudinaryService");

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una imagen"
            );
        }

        System.out.println("C2. Archivo válido");

        if (archivo.getContentType() == null ||
                !archivo.getContentType().startsWith("image/")) {

            throw new IllegalArgumentException(
                    "El archivo debe ser una imagen"
            );
        }

        System.out.println(
                "C3. Tipo válido: " + archivo.getContentType()
        );

        String carpeta = (folder == null || folder.isBlank())
                ? "sigo"
                : folder.trim();

        System.out.println(
                "C4. Subiendo a Cloudinary. Carpeta: " + carpeta
        );

        try {

            Map<?, ?> resultado = cloudinary.uploader().upload(
                    archivo.getBytes(),
                    ObjectUtils.asMap(
                            "folder", carpeta,
                            "resource_type", "image"
                    )
            );

            System.out.println("C5. CLOUDINARY FUNCIONÓ");
            System.out.println(
                    "C6. public_id: " + resultado.get("public_id")
            );

            return (Map<String, Object>) resultado;

        } catch (Exception e) {

            System.err.println(
                    "========== ERROR CLOUDINARY =========="
            );
            System.err.println(
                    "Tipo: " + e.getClass().getName()
            );
            System.err.println(
                    "Mensaje: " + e.getMessage()
            );

            e.printStackTrace();

            System.err.println(
                    "======================================"
            );

            throw e;
        }
    }
}