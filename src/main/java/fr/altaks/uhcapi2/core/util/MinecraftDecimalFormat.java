package fr.altaks.uhcapi2.core.util;

import java.text.NumberFormat;
import java.util.Locale;

public class MinecraftDecimalFormat {

    static NumberFormat format = NumberFormat.getNumberInstance(Locale.FRANCE);

    public static String format(double number){
        return format.format(number).replace((char) 160, ' ');
    }
}
