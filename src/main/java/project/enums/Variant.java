package project.enums;

import java.util.HashMap;
import java.util.List;

import static project.enums.EnumMethods.returnFormattedEnum;

public enum Variant {
    VARIANT_0,
    VARIANT_1,
    VARIANT_2,
    VARIANT_3;

    public static List<String> getVariantList() {
        Variant[] variantValues = Variant.values();
        return EnumMethods.getEnumList(variantValues);
    }

    public static Variant setVariant(String variant) {
        HashMap<String, Variant> variantHashMap = new HashMap<>();
        Variant[] variantValues = Variant.values();

        for(Variant variantValue:variantValues) {
            variantHashMap.put(returnFormattedEnum(variantValue), variantValue);
        }
        return variantHashMap.get(variant);
    }
}
