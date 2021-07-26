package Blackjack;

public class Hand {

    private final Integer ACE_BONUS = 10;
    private final Integer MAX_POINTS = 21;

    private Integer points;
    private boolean hasAce;

    public Hand()
    {
        reset();
    }

    public final Integer getPoints() {
        if (aceBonus())
            return points + 10;
        return points;
    }

    public final void takeCard(Card c)
    {
        points += c.getValue();
        if (c.getName() == "ACE")
            hasAce = true;
    }

    public boolean aceBonus()
    {
        if(hasAce && points + ACE_BONUS <= MAX_POINTS)
            return true;
        return false;
    }

    public final void reset()
    {
        points = 0;
        hasAce = false;
    }
}
