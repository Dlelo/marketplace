package com.example.marketplace.enums;

public enum CountyOptions {
    NAIROBI("Nairobi", 0),
    KIAMBU("Kiambu", 500),
    MACHAKOS("Machakos", 500),
    KAJIADO("Kajiado", 500),
    MURANG_A("Murang'a", 800),
    NYERI("Nyeri", 1000),
    KIRINYAGA("Kirinyaga", 1000),
    NYANDARUA("Nyandarua", 1000),
    EMBU("Embu", 1200),
    THARAKA_NITHI("Tharaka Nithi", 1200),
    MERU("Meru", 1500),
    ISIOLO("Isiolo", 2000),
    MARSABIT("Marsabit", 3000),
    GARISSA("Garissa", 2500),
    WAJIR("Wajir", 3000),
    MANDERA("Mandera", 3500),
    MOMBASA("Mombasa", 2000),
    KWALE("Kwale", 2000),
    KILIFI("Kilifi", 2000),
    TANA_RIVER("Tana River", 2500),
    LAMU("Lamu", 3000),
    TAITA_TAVETA("Taita Taveta", 2000),
    NAKURU("Nakuru", 1000),
    NAROK("Narok", 1500),
    KERICHO("Kericho", 1500),
    BOMET("Bomet", 1500),
    KAKAMEGA("Kakamega", 1800),
    VIHIGA("Vihiga", 1800),
    BUNGOMA("Bungoma", 2000),
    BUSIA("Busia", 2000),
    SIAYA("Siaya", 1800),
    KISUMU("Kisumu", 1800),
    HOMA_BAY("Homa Bay", 2000),
    MIGORI("Migori", 2000),
    KISII("Kisii", 1800),
    NYAMIRA("Nyamira", 1800),
    NAIVASHA("Naivasha", 800),
    BARINGO("Baringo", 1500),
    LAIKIPIA("Laikipia", 1200),
    SAMBURU("Samburu", 2000),
    TRANS_NZOIA("Trans Nzoia", 2000),
    UASIN_GISHU("Uasin Gishu", 1800),
    ELGEYO_MARAKWET("Elgeyo Marakwet", 1800),
    NANDI("Nandi", 1800),
    WEST_POKOT("West Pokot", 2500),
    TURKANA("Turkana", 3500),
    MAKUENI("Makueni", 800);

    private final String displayName;
    private final int surcharge;

    CountyOptions(String displayName, int surcharge) {
        this.displayName = displayName;
        this.surcharge = surcharge;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSurcharge() {
        return surcharge;
    }

    /**
     * Get county by display name
     */
    public static CountyOptions fromDisplayName(String displayName) {
        for (CountyOptions county : CountyOptions.values()) {
            if (county.getDisplayName().equalsIgnoreCase(displayName)) {
                return county;
            }
        }
        throw new IllegalArgumentException("Unknown county: " + displayName);
    }

    /**
     * Check if county is Nairobi (no surcharge)
     */
    public boolean isNairobi() {
        return this == NAIROBI;
    }

    /**
     * Get surcharge in KES
     */
    public int getSurchargeAmount() {
        return surcharge;
    }
}
