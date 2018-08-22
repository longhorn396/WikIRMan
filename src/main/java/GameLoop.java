import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

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

    public GameLoop(ExtractorOptions eo, OutputOptions oo, int maxGuesses, int hintOnTurn) {
        rw = new RandomWiki();
        wordToGuess = RandomWordExtractor.extract(rw.getPage(), rw.getURI(), (this.eo = eo));
        input = new Scanner(System.in);
        guesses = new TreeSet<>();
        this.oo = oo;
        this.maxGuesses = maxGuesses;
        this.hintOnTurn = hintOnTurn;
        loadPositions();
        playGame();
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
            //Decide if game over or not
            System.out.println("Play again? [y for yes n for no]");
            String next = input.nextLine();
            char c;
            if (next.length() == 0) {
                c = '\n';
            } else {
                c = next.charAt(0);
            }
            if (c == 'y') {
                reset();
            } else if (c == 'n') {
                break;
            } else {
                System.out.println("I'll just assume that was a yes. \nWhy would you ever want to stop playing this game?");
            }
        }
        endGame();
    }

    private boolean playRound() {
        char [] slots = new char[wordToGuess.length()];
        boolean shown = false;
        int positionsLeft = slots.length;
        char in;
        for(int k = 0; k < slots.length; k++) {
            slots[k] = '_';
        }
        oo.show_round_info(slots, maxGuesses + 1);
        for(int guess = maxGuesses; guess > 0; guess--){
            System.out.println("Guesses left: " + guess);
            if(!shown && (maxGuesses - guess == hintOnTurn)) {
                System.out.println("Showing hint...");
                try {
                    showHint();
                } catch (URISyntaxException | IOException e) {
                    System.out.println("There was a problem showing the hint.");
                }
                shown = true;
            }
            //Process key inputs
            String next = input.nextLine().toLowerCase();
            if(next.length() > 0) {
                in = next.charAt(0);
            } else {
                in = '\n';
            }
            if(guesses.contains(in)) {
                guess++;
                System.out.println("You've already guessed this character!");
            } else if(positions.containsKey(in)) {
                guess++;
                guesses.add(in);
                List<Integer> arr = positions.get(in);
                positionsLeft -= arr.size();
                for(Integer j : arr) {
                    slots[j] = in;
                }
            } else {
                guesses.add(in);
                System.out.println("Wrong guess!");
            }
            if(positionsLeft <= 0) {
                return true;
            }
            oo.show_round_info(slots, guess);
        }
        return false;
    }

    private void showHint() throws URISyntaxException, IOException {
        if(Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(rw.getURI()));
        }
    }

    private void reset() {
        try {
            rw.refresh();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
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

}
