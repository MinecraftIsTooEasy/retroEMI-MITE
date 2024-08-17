package emi.moddedmite.emi.api;

import java.util.List;

public interface EMICraftingManager {
    default List getRecipes() {
        return null;
    }
}
