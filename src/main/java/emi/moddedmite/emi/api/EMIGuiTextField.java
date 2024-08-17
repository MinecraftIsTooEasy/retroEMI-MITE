package emi.moddedmite.emi.api;

public interface EMIGuiTextField {
    default boolean getIsEnabled() {
        return false;
    }
}
