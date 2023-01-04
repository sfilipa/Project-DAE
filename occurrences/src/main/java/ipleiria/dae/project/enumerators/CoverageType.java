package ipleiria.dae.project.enumerators;

import org.json.JSONArray;

import java.util.LinkedList;
import java.util.List;

public enum CoverageType {
    ACCIDENTAL_DAMAGE,
    THEFT,
    WATER_DAMAGE,
    MECHANICAL_FAILURE,
    WORLDWIDE_COVERAGE,
    STRUCTURAL_DAMAGE,
    FLOOD_DAMAGE,
    EARTHQUAKE_DAMAGE;

    public static List<CoverageType> getCoverageTypeList(JSONArray insuranceCoversAPI) {
        List<CoverageType> covers = new LinkedList<>();
        for (int i = 0; i < insuranceCoversAPI.length(); i++) {
            covers.add(CoverageType.valueOf(insuranceCoversAPI.getString(i)));
        }
        return covers;
    }
}
