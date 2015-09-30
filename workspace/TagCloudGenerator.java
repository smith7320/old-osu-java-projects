import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

/**
 * Generate a html tag cloud file from a given input txt file.
 * 
 * @author Douglas Smith Feysal Ibrahim
 * 
 */
public final class TagCloudGenerator {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudGenerator() {
    }

    /**
     * Orders Map.Entry<String, Integer> in alphabetical order.
     */
    public static class PairByAlphabeticalOrder implements
            Comparator<Map.Entry<String, Integer>> {
        @Override
        public final int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            return o1.getKey().compareToIgnoreCase(o2.getKey());
        }

        /**
         * Orders Map.Entry<String, Integer> in numerical order from greatest to
         * least.
         */
        public static class PairByGreaterInteger implements
                Comparator<Map.Entry<String, Integer>> {
            @Override
            public final int compare(Map.Entry<String, Integer> o1,
                    Map.Entry<String, Integer> o2) {
                int compare = 0;
                if (o2.getValue().compareTo(o1.getValue()) == 0) {
                    compare = o1.getKey().compareToIgnoreCase(o2.getKey());
                } else {
                    compare = o2.getValue().compareTo(o1.getValue());
                }
                return compare;
            }
        }

        /**
         * Creates a set of all separator characters.
         * 
         * @param separators
         *            Set of separator characters.
         * 
         * @updates separators
         * @requires <pre>
         * {@code separators is empty}
         * </pre>
         * @ensures <pre>
         * {@code separators.content = #separators.content 
         *        * [all the separator characters]}
         * </pre>
         */
        private static void createSet(Set<Character> separators) {

            // Add separator characters to set
            separators.add('\n');
            separators.add('\t');
            separators.add('\r');
            separators.add(',');
            separators.add('-');
            separators.add('.');
            separators.add('!');
            separators.add('?');
            separators.add('[');
            separators.add(']');
            separators.add(' ');
            separators.add(':');
            separators.add(';');
            separators.add('_');
            separators.add('*');
            separators.add('"');
        }

        /**
         * Get lines from the text file and places them into a Queue.
         * 
         * @param in
         *            File to read text from.
         * 
         * @return Queue<String> of each line of text
         * @requires <pre>
         * {@code in.is_open}
         * </pre>
         * @ensures <pre>
         * {@code queue.content = #queue.content * [each separate line of text]}
         * </pre>
         */
        private static Queue<String> getLinesFromText(BufferedReader in) {

            //Initialize variables
            Queue<String> lines = new PriorityQueue<String>();
            String line = "";

            try {
                line = in.readLine();
                while (line != null) {
                    lines.add(line);
                    line = in.readLine();
                }
            } catch (IOException e) {
                System.err.println("Error reading lines.");
            }

            return lines;
        }

        /**
         * Gets lines and separates each line into words using getWords.
         * 
         * @param lines
         *            Lines of text to separate into words.
         * @param separators
         *            Separator characters by which to discern words.
         * 
         * @clears lines
         * @return Map<String, Integer> of all words separated in each line with
         *         their counts
         * @requires <pre>
         * {@code lines is not empty}
         * </pre>
         * @ensures <pre>
         * {@code each line is separated into words} and {@code map.content 
         *                = #map.content * [separated words and their counts]}
         * </pre>
         */
        private static Map<String, Integer> getWordsFromLines(
                Queue<String> lines, Set<Character> separators) {

            //Initialize variables
            Map<String, Integer> words = new HashMap<>();
            String line = "";

            //Takes each line and uses the getWords function to update map of separated 
            //words and their counts
            while (lines.size() > 0) {
                line = lines.remove();
                getWords(line, words, separators);
            }

            return words;

        }

        /**
         * Separates each word from the line (separating by use of separator
         * characters) and places them into a map full of words with their
         * counts.
         * 
         * @param line
         *            Line of text to separate into words.
         * @param words
         *            Map of separated words to update with new words and
         *            counts.
         * @param separators
         *            Separator characters by which to discern words.
         * 
         * @update Map<String, Integer> words
         * @requires <pre>
         * {@code line is not null}
         * </pre>
         * @ensures <pre>
         * {@code map.content = #map.content * [separated words and their counts]}
         * </pre>
         */
        private static void getWords(String line, Map<String, Integer> words,
                Set<Character> separators) {

            //Initialize variables
            String word = "";
            Boolean wordFinished = false;
            int i = 0;

            //While i is less than the length of the line increment until a 
            //non-separator character is found. Then concate each character to a 
            //string until i is at its length else if a separator character is found.
            //Then the word is finished and added and i is incremented until
            //another non-separator character is found.
            while (i < line.length()) {
                if (separators.contains(line.charAt(i))) {
                    i++;
                } else {
                    while (!wordFinished) {
                        word += line.substring(i, i + 1);
                        i++;
                        if (i == line.length()) {
                            wordFinished = true;
                            if (words.containsKey(word)) {
                                int num = words.remove(word);
                                num++;
                                words.put(word, num);
                            } else {
                                words.put(word, 1);
                            }
                            word = "";
                        } else if (separators.contains(line.charAt(i))) {
                            wordFinished = true;
                            if (words.containsKey(word)) {
                                int num = words.remove(word);
                                num++;
                                words.put(word, num);
                            } else {
                                words.put(word, 1);
                            }
                            word = "";
                        }
                    }
                    wordFinished = false;
                }
            }

        }

        /**
         * Creates the header for the tag cloud HTML page.
         * 
         * @param title
         *            Name of file used.
         * @param num
         *            The number of words to be generated.
         * @param out
         *            Output stream.
         * @updates {@code out.content}
         * @requires <pre>
         * {@code out.is_open}
         * </pre>
         * @ensures <pre>
         * {@code out.content = #out.content * [the HTML opening tags]}
         * </pre>
         */
        private static void createHeader(String title, int num, PrintWriter out) {

            // Creating header tags
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Top " + num + " word(s) in " + title
                    + "</title>");
            out.println("<link href=\"data/tagcloud.css\""
                    + "rel=\"stylesheet\" type=\"text/css\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>Top " + num + " word(s) in " + title + "</h2>");
            out.println("<hr>");

        }

        /**
         * Outputs an html file tag cloud based on the words to generate of the
         * top n words that appear in the txt file. If n > number of words,
         * outputs the nearest number of words to n as possible.
         * 
         * @param out
         *            Output stream.
         * @param wordsCounts
         *            Map of words with their counts.
         * @param n
         *            Number of words to generate.
         * @param filename
         *            Name of txt file.
         * 
         * @updates {@code out.content}
         * @requires <pre>
         * {@code 1 <= n < MAX_INTEGER and |result| > 0} and 
         * {@code filename is not null} and {@code out.is_open}
         * </pre>
         * @ensures <pre>
         * {@code out.content = #out.content * [the top n words in tag cloud format]}
         * </pre>
         */
        public static void outputFile(Map<String, Integer> wordsCounts,
                PrintWriter out, int n, String filename) {

            createHeader(filename, n, out);
            out.println("<div class=\"cdiv\">");
            out.println("<p class=\"cbox\">");

            //Initialize variables
            int min = Integer.MAX_VALUE;
            int max = 0;
            int font = 0;

            //Create sorted list of map entries
            List<Map.Entry<String, Integer>> list;
            list = new LinkedList<Map.Entry<String, Integer>>(
                    wordsCounts.entrySet());
            Collections.sort(list, new PairByGreaterInteger());
            List<Map.Entry<String, Integer>> list1;
            if (n > list.size() - 1) {
                list1 = list;
            } else {
                list1 = list.subList(0, n);
            }
            if (!list1.isEmpty()) {
                max = list.get(0).getValue();
                min = list.get(list.size() - 1).getValue();
            }

            //Resort in alphabetical order
            Collections.sort(list1, new PairByAlphabeticalOrder());
            Iterator<Map.Entry<String, Integer>> i = list1.iterator();

            //Create tag cloud of words
            while (i.hasNext()) {
                Map.Entry<String, Integer> p = i.next();
                String word = p.getKey();
                int count = p.getValue();
                final int maxfont = 37;
                final int offset = 11;
                if (max - min == 0) {
                    font = ((maxfont * (count - min)) / 1) + offset;
                } else {
                    font = ((maxfont * (count - min)) / (max - min)) + offset;
                }
                out.print("<span style=\"cursor:default\" class=\"f" + font
                        + "\" title=\"count:" + count + "\">");
                out.print(word);
                out.println("</span>");
            }

            out.println("</p>");
            createFooter(out);
        }

        /**
         * Creates the footer for the tag cloud HTML page.
         * 
         * @param out
         *            Output stream.
         * @requires <pre>
         * {@code out.is_open}
         * </pre>
         * @ensures <pre>
         * {@code out.content = #out.content * [the HTML closing tags]}
         * </pre>
         */
        private static void createFooter(PrintWriter out) {

            // Creating footer tags
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        }

        /**
         * Main method.
         * 
         * @param args
         *            the command line arguments
         */
        public static void main(String[] args) {
            Scanner in = new Scanner(System.in);

            System.out.println("Enter the name of the input file:");
            String name = in.nextLine();
            System.out.println("Enter the name of the output file:");
            String fileName = in.nextLine();
            System.out
                    .println("Enter a positive integer (less than MAX INTEGER) "
                            + "for the number of words to generate:");
            int n = in.nextInt();

            PrintWriter outputFile;
            BufferedReader file;
            try {
                file = new BufferedReader(new FileReader(name));
                outputFile = new PrintWriter(new BufferedWriter(new FileWriter(
                        fileName)));
                Set<Character> separators = new HashSet<>();
                createSet(separators);
                Queue<String> lines = getLinesFromText(file);
                Map<String, Integer> wordsCounts = getWordsFromLines(lines,
                        separators);
                outputFile(wordsCounts, outputFile, n, name);
                System.out.println("Output file created successfully.");
            } catch (IOException e) {
                System.err.println("Error with reading or writing file.");
                in.close();
                return;
            }

            try {
                file.close();
                outputFile.close();
                in.close();
            } catch (IOException e) {
                System.err.println("Error closing streams.");
            }
        }
    }
}
