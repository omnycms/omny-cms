package ca.omny.configuration;

public class StringUtils {
    public static String join(String[] input, String joiner) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<input.length; i++) {
            if(i>0) {
                sb.append(joiner);
            }
            sb.append(input[i]);
        }
        return sb.toString();
    }
}
