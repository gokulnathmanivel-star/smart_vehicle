package com.svsbas.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class ReferenceGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String generateBookingRef() {
        String date = LocalDate.now().format(DATE_FORMAT);
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "SB-" + date + "-" + rand;
    }

    public static String generateSosRef() {
        String date = LocalDate.now().format(DATE_FORMAT);
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "SOS-" + date + "-" + rand;
    }

    public static String generateInvoiceRef() {
        String date = LocalDate.now().format(DATE_FORMAT);
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "INV-" + date + "-" + rand;
    }
}
