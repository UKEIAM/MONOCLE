package de.uke.iam.mtb.control.models.enums.coredata;

public enum MolecularTherapyIntention {
    K("kurativ"),
    P("palliativ"),
    S("sonstiges"),
    X("keine Angabe");
    private final String value;

    private MolecularTherapyIntention(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
