package javase03.t03;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PicLinksFinder {

    public static void main(String[] args) {
//        readFileToString("javase03/src/main/resources/t03/PicLinksFinder/JF03 - 3.1 - Information handling_task_attachment.html");
    }
    private Pattern p;
    private Matcher m;
    // Maps sentences with picture links to the List of these link
    private final Map<Integer, Tuple2<String, List<Integer>>> picLinks = new HashMap<>();

    //Ссылка на рисунок: \s*[^>][Рр]ис[^\d\s]*\s\d+
    //Разбиение на слова: [^а-яёА-ЯЁ_0-9]+

//     Типы ссылок на рисунки:
//     рисунке 1
//     рисунка 17
//     (рис. 8)
//     (Рис. 1)
//     (Рис. 1, 2)
//     (Рис. 8-б)
//     (Рис. 8 г,д)
//     На рисунке (Рис. 14-а)
//     (Рис. 15,16)
//     (Рис. 25 и 26)


    /**
     * Returns mapping of sentence to list of picture links in this sentence.
     * @param sentences List of Strings to find picture links in.
     * @return Map with Integer keys (indexes) and Tuple2 of String sentence and
     * ArrayList of corresponding picture links from that sentence
     */
    @SuppressWarnings("WeakerAccess")
    public Map<Integer, Tuple2<String, List<Integer>>> getPicLinks(@NonNull List<String> sentences) {
        List<String> output = new ArrayList<>();
        List<Integer> picLinkNumbers = new ArrayList<>();
// If sentence contains link for picture, add this sentence to List output.
        p = Pattern.compile("(?<=[кс][.еа]\\s)(\\d{1,3})([, и]+(\\d{1,3}))?");
        for (String sentence : sentences) {
            // Temporary list to accumulate picture links from current sentence
            List<Integer> links = new ArrayList<>();
            m = p.matcher(sentence);
            while (m.find()) {
                String group1 = m.group(1);
                if (group1 != null)
                    links.add(Integer.parseInt(group1));
                String group3 = m.group(3);
                if (group3 != null)
                    links.add(Integer.parseInt(group3));
            }
            // Check whether we put fot the first time
            if (picLinks.keySet().isEmpty()) {
                picLinks.put(0, Tuple.of(sentence, links));
            // Incrementing Integer key with each put operation
            } else picLinks.put(picLinks.keySet().size(), Tuple.of(sentence, links));
        }
        return picLinks;
    }

    /**
     * Splitting original text from file into sentences and puts them into returned List
     * @param fileName fully qualified file name to read from.
     * @return List of Strings with sentences of the original text.
     */
    public List<String> getSentences(@NonNull String fileName) {
        String text = readFileToString(fileName);
        String valuableText = cleanHtmlTags(text);
        // Splitting by sentences
        p = Pattern.compile("[А-ЯЁ][А-ЯЁа-яёa-z\\w\\s\\d\\p{Punct}«»–]*?[!.?]\\s+(?=[А-ЯЁ]|$)");
        m = p.matcher(valuableText);
        List<String> sentences = new ArrayList<>();
        String s;
        while (m.find()) {
            s = valuableText.substring(m.start(), m.end() - 1).trim();
            sentences.add(s);
        }
        return sentences;
    }

    private String cleanHtmlTags(String text) {
        // truncating not valuable bytes from the beginning of the file
        String valuableText = text.substring(text.indexOf("Мнения ученых"));
        // Excluding all pictures captions
        p = Pattern.compile(">Рис.\\s*?\\d+");
        m = p.matcher(valuableText);
        valuableText = m.replaceAll(">");
        // Excluding html tags
        p = Pattern.compile("&nbsp;");
        m = p.matcher(valuableText);
        valuableText = m.replaceAll(" ");
        p = Pattern.compile("<.*?>");
        m = p.matcher(valuableText);
        valuableText = m.replaceAll(" ");
        return valuableText;
    }

    @SneakyThrows
    private static String readFileToString(@NonNull String fileName) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), Charset.forName("Windows-1251")))) {
            while (br.ready()) {
                sb.append((char)br.read());
            }
        }
        return sb.toString();
    }
}