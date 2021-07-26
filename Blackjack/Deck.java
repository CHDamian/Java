package Blackjack;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

    private final int COLORS = 4;

    private ArrayList<Card> cards;

    public class NotPositiveNumberOfSets extends Exception
    {
        public NotPositiveNumberOfSets(){}
    }

    public Deck(int sets) throws NotPositiveNumberOfSets
    {
        if (sets < 1) throw new NotPositiveNumberOfSets();
        cards = new ArrayList<>();
        for (int i = 0; i < sets; i++)
            addSet();
        shuffle();
    }

    private void addSet()
    {
        for (int i=0; i<COLORS; i++)
        {
            cards.add(new Card(Card.ACE));
            cards.add(new Card(Card.TWO));
            cards.add(new Card(Card.THREE));
            cards.add(new Card(Card.FOUR));
            cards.add(new Card(Card.FIVE));
            cards.add(new Card(Card.SIX));
            cards.add(new Card(Card.SEVEN));
            cards.add(new Card(Card.EIGHT));
            cards.add(new Card(Card.NINE));
            cards.add(new Card(Card.TEN));
            cards.add(new Card(Card.JACK));
            cards.add(new Card(Card.QUEEN));
            cards.add(new Card(Card.KING));
        }
    }

    private void shuffle()
    {
        Collections.shuffle(cards);
    }

    public Card draw()
    {
        if(cards.isEmpty())
        {
            addSet();
            shuffle();
        }

        return cards.remove(0);
    }

    public Integer cardsLeft()
    {
        return cards.size();
    }

}
