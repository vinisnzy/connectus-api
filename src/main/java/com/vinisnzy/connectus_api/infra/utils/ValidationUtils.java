package com.vinisnzy.connectus_api.infra.utils;

public class ValidationUtils {
    private ValidationUtils() {
        throw new IllegalArgumentException("Utility class");
    }

    public static boolean validateCnpj(String cnpj) {
        int[] b = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        // Remove tudo que não for dígito
        String c = cnpj.replaceAll("\\D", "");

        if (c.length() != 14)
            return false;

        if (c.matches("0{14}"))
            return false;

        // Primeiro dígito verificador
        int n = 0;
        for (int i = 0; i < 12; ) {
            n += Character.getNumericValue(c.charAt(i)) * b[++i];
        }

        int dv1 = (n % 11) < 2 ? 0 : 11 - (n % 11);
        if (Character.getNumericValue(c.charAt(12)) != dv1)
            return false;

        // Segundo dígito verificador
        n = 0;
        for (int i = 0; i <= 12; ) {
            n += Character.getNumericValue(c.charAt(i)) * b[i++];
        }

        int dv2 = (n % 11) < 2 ? 0 : 11 - (n % 11);
        if (Character.getNumericValue(c.charAt(13)) != dv2)
            return false;

        return true;
    }
}
