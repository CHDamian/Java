package Blackjack;

public class Card {

    public final static int ACE = 1;
    public final static int TWO = 2;
    public final static int THREE = 3;
    public final static int FOUR = 4;
    public final static int FIVE = 5;
    public final static int SIX = 6;
    public final static int SEVEN = 7;
    public final static int EIGHT = 8;
    public final static int NINE = 9;
    public final static int TEN = 10;
    public final static int JACK = 11;
    public final static int QUEEN = 12;
    public final static int KING = 13;

    private String name;
    private Integer value;

    public Card(int card_name)
    {
        switch (card_name)
        {
            case ACE:
                name = "ACE";
                value = 1;
                break;
            case TWO:
                name = "TWO";
                value = 2;
                break;
            case THREE:
                name = "THREE";
                value = 3;
                break;
            case FOUR:
                name = "FOUR";
                value = 4;
                break;
            case FIVE:
                name = "FIVE";
                value = 5;
                break;
            case SIX:
                name = "SIX";
                value = 6;
                break;
            case SEVEN:
                name = "SEVEN";
                value = 7;
                break;
            case EIGHT:
                name = "EIGHT";
                value = 8;
                break;
            case NINE:
                name = "NINE";
                value = 9;
                break;
            case TEN:
                name = "TEN";
                value = 10;
                break;
            case JACK:
                name = "JACK";
                value = 10;
                break;
            case QUEEN:
                name = "QUEEN";
                value = 10;
                break;
            case KING:
                name = "KING";
                value = 10;
                break;
        }
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }
}
