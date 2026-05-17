package com.example.SosLocaliza.util;

/**
 * Normalização e validação (dígitos verificadores) de CPF brasileiro.
 */
public final class CpfUtil {

    private CpfUtil() {
    }

    public static String somenteDigitos(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replaceAll("\\D", "");
    }

    public static boolean cpfValido(String onzeDigitos) {
        if (onzeDigitos == null || onzeDigitos.length() != 11) {
            return false;
        }
        if (onzeDigitos.chars().distinct().count() == 1) {
            return false;
        }
        int d1 = digitoVerificador(onzeDigitos.substring(0, 9), 10);
        int d2 = digitoVerificador(onzeDigitos.substring(0, 10), 11);
        return d1 == Character.getNumericValue(onzeDigitos.charAt(9))
                && d2 == Character.getNumericValue(onzeDigitos.charAt(10));
    }

    private static int digitoVerificador(String base, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
