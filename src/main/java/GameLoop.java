import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

/**
 * Class that runs a game of Hangman
 */
public class GameLoop {

    private RandomWiki rw;

    private String wordToGuess;

    private Map<Character, List<Integer>> positions;

    private Scanner input;

    private Set<Character> guesses;

    private ExtractorOptions eo;

    private OutputOptions oo;

    private int maxGuesses;

    private int hintOnTurn;

    /**
     * Creates and runs a game of potentially infinite rounds
     *
     * @param eo         ExtractorOptions enum
     * @param oo         OutputOptions enum
     * @param maxGuesses the maximum number of guesses per game
     * @param hintOnTurn the turn on which to display the hint (set greater than maxGuesses to disable hints)
     */
    public GameLoop(ExtractorOptions eo, OutputOptions oo, int maxGuesses, int hintOnTurn) {
        rw = new RandomWiki();
        this.eo = eo;
        wordToGuess = extract();
        input = new Scanner(System.in);
        guesses = new TreeSet<>();
        this.oo = oo;
        this.maxGuesses = maxGuesses;
        this.hintOnTurn = hintOnTurn;
        loadPositions();
        playGame();
    }

    private String extract() {
        for(int i = 0; i < 5; i++) {
            String word = RandomWordExtractor.extract(rw.getPage(), rw.getURI(), eo);
            if (word != null)
                return word;
            try {
                rw.refresh();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Extraction failed: couldn't refresh");
                System.exit(-1);
            }
        }
        System.err.println("Extraction failed: too many retries");
        System.exit(-1);
        return null;
    }

    private void loadPositions() {
        positions = new TreeMap<>();
        for(int i = 0; i < wordToGuess.length(); i++){
            Character c = wordToGuess.charAt(i);
            if(positions.containsKey(c)){
                List<Integer> l = positions.get(c);
                l.add(i);
                positions.put(c, l);
            } else {
                List<Integer> l = new ArrayList<>();
                l.add(i);
                positions.put(c, l);
            }
        }
    }

    private void playGame() {
        while (true) {
            if (playRound()) {
                System.out.println("Congratulations, you won!");
            } else {
                System.out.println("\nYou lost on the word " + wordToGuess + ". Better luck next time!");
            }
            System.out.println("Play again? [y for yes n for no]");
            char nextChar = getNext().charAt(0);
            if (nextChar == 'y') {
                reset();
            } else if (nextChar == 'n') {
                break;
            } else {
                System.out.println("I'll just assume that was a yes. \nWhy would you ever want to stop playing this game?");
            }
        }
        endGame();
    }

    private boolean playRound() {
        boolean hintShown = false;
        char [] slots = new char[wordToGuess.length()];
        int positionsLeft = slots.length;
        for (int i = 0; i < slots.length; i++) {
            slots[i] = '_';
        }
        oo.show_round_info(slots, maxGuesses + 1);
        int guessesLeft = maxGuesses;
        while (guessesLeft > 0){
            System.out.println("Guesses left: " + guessesLeft);
            if (!hintShown && (maxGuesses - guessesLeft == hintOnTurn)) {
                showHint();
                hintShown = true;
            }
            for (char nextChar : getNext().toCharArray()) {
                if (makeGuess(nextChar)) {
                    List<Integer> list = positions.get(nextChar);
                    if(list != null) {
                        positionsLeft -= list.size();
                        for (Integer j : list) {
                            slots[j] = nextChar;
                        }
                        positions.remove(nextChar);
                    }
                } else {
                    guessesLeft--;
                }
            }
            oo.show_round_info(slots, guessesLeft);
            if(positionsLeft <= 0) {
                return true;
            }
        }
        return false;
    }

    private void showHint() {
        System.out.println("Showing hint...");
        try {
            if(Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(rw.getURI()));
            }
        } catch (URISyntaxException | IOException e) {
            System.out.println("There was a problem showing the hint.");
        }
    }

    private boolean makeGuess(char nextChar) {
        if(guesses.add(nextChar)) {
            if (!positions.containsKey(nextChar)) {
                System.out.println("Wrong guess!");
                return false;
            }
        } else {
            assert positions.get(nextChar) == null;
            System.out.println("You've already guessed this character!");
        }
        return true;
    }

    private String getNext() {
        String next = input.nextLine().toLowerCase();
        if(next.length() > 0) {
            return next;
        } else {
            return "\n";
        }
    }

    private void reset() {
        try {
            rw.refresh();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        wordToGuess = RandomWordExtractor.extract(rw.getPage(), rw.getURI(), eo);
        guesses.clear();
        loadPositions();
    }

    private void endGame() {
        try {
            rw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        input.close();
        System.exit(0);
    }

    public static void main(String[] args) {
        new GameLoop(ExtractorOptions.INFO_TABLE, OutputOptions.TERM_TEXT, 7, 0);
    }

}
