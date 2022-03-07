package jroid.kakaotalk.bot;

import androidx.annotation.NonNull;

public class SystemConfig {
    
    private static final String REGEX = "[^A-Za-z-_0-9ㄱ-ㅎㅏ-ㅣ가-힣\\s]|[\n]|[\r]";
    
    @NonNull
    public static String getRegex(@NonNull String string, @NonNull String replacement) {
        return string.replaceAll(REGEX, replacement)
            .replaceAll("[ ]{2,}", replacement)
            .trim();
    }
    
}
