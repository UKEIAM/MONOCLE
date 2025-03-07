package de.uke.iam.mtb.control.models.enums.coredata;

public enum MolecularTherapyProcedurePosition {
    O("ohne Bezug zur operativen Therapie"),
    A("adjuvant"),
    N("neoadjuvant"),
    I("intraoperativ"),
    S("sonstiges");
    private final String value;

    private MolecularTherapyProcedurePosition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }
    }