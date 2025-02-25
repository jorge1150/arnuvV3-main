package com.core.arnuv.constants;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

public class Constants {

    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    public static final String KEY_RADIO = "RADIO";
    public static final String KM_BUSQUEDA = "KMBUSQUEDA";
    public static final String VISTA_MAPA = "VISTAMAPA";
    public static final String KEY_PLANTILLA_MAIL = "PLANTILLAMAIL";
    public static final String KEY_MAIL_INFO_PASEADOR = "MAILINFOPASEADOR";
    public static final String KEY_MAIL_RECUPERACION_PASSWORD = "MAILRECUPERACIONPASSWORD";
    public static final String KEY_MAIL_CLIENTE = "MAILCLIENTE";
    public static final String KEY_MAIL_SOLICITUD_PASEO = "MAILSOLICITUDPASEO";
    public static final String KEY_MAIL_ESTADO = "MAILESTADO";
    public static final String KEY_MAIL_APROBACION_PASEO = "MAILAPROBACIONPASEO";
    public static final String KEY_MAIL_RECHAZO_PASEO = "MAILRECHAZOPASEO";
    public static final String KEY_MAIL_PASEO_FINALIZADO = "MAILPASEOFINALIZADO";
    public static final String KEY_MAIL_CALIFICACION_PASEO = "MAILCALIFICACIONPASEO";
    public static final String KEY_MAIL_CANCELACION_PASEO = "MAILCANCELACIONPASEO";
    public static final String KEY_MAIL_ADMINISTRADOR = "MAILADMINISTRADOR";
    public static final String KEY_LINK_MAPA_GOOGLE = "LINKMAPAGOOGLE";
    public static final int EARTH_RADIUS = 6371;
    public static final String PASEADOR = "Paseador";
    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_APROBADO = "APROBADO";
    public static final String ESTADO_CANCELADO = "CANCELADO";
    public static final String ESTADO_PASEO_FINALIZADO = "PASEO_FINALIZADO";
    public static final String ESTADO_FINALIZADO = "FINALIZADO";
    public static final String ESTADO_RECHAZADO = "RECHAZADO";
    public static final Integer SECONDS = 60;
    public static final String KEY_MINUTES_EXPIRATION = "MINUTESEXPIRATION";
    public static final Integer DEFAULT_MINUTES_EXPIRATION = 5;
    public static final String URL_API_GOOGLE_MAP = "https://maps.googleapis.com/maps/api/js?key=";
    public static final String URL_DOMAIN_MAIL = "URLDOMAINMAIL";
    public static final SimpleDateFormat FECHA_FORMATO_ENTRADA = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat FECHA_FORMATO_SALIDA = new SimpleDateFormat("EEEE dd 'de' MMMM 'del' yyyy HH:mm:ss", new Locale("es", "ES"));

    public static Integer convertMinutesToSeconds(int minutos) {
        return minutos * SECONDS;
    }

    public static String[] getPermittedRoutes() {
        return new String[]{
                "/content/**",
                "/landing/**",
                "/auth/**",
                "/index",
                "/",
                "/paseos/**"
        };
    }
}
