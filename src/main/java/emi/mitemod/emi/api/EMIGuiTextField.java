package emi.mitemod.emi.api;

public interface EMIGuiTextField {
    default boolean getIsEnabled() {
        return false;
    }
}
