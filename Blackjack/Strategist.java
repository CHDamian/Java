package Blackjack;

public class Strategist extends Player {

    private final Integer REASONABLE_LIMIT = 17;

    private final int NORMAL_BIG_LIMIT = 17;
    private final int NORMAL_SMALL_LIMIT = 12;
    private final int NORMAL_CROUPIER_LIMIT = 7;

    private final int ACE_BIG_LIMIT = 19;
    private final int ACE_SMALL_LIMIT = 18;
    private final int ACE_CROUPIER_LIMIT = 9;

    public Strategist(String name)
    {
        super(name);
    }

    public Integer move()
    {
        if(getPoints() >= REASONABLE_LIMIT)
            return Game.STAND_COMMAND;
        return Game.CROUPIER_CARD;
    }

    public Integer move(int cardLeft)
    {
        return Game.STAND_COMMAND;
    }

    public Integer move(Card croupierCard)
    {
        if(myHand.aceBonus())
            return aceStrategy(croupierCard);
        return normalStrategy(croupierCard);
    }

    private Integer normalStrategy(Card croupierCard)
    {
        if(croupierCard.getName() == "ACE" || croupierCard.getValue() >= NORMAL_CROUPIER_LIMIT)
        {
            if(getPoints() < NORMAL_BIG_LIMIT)
                return Game.HIT_COMMAND;
        }
        else
        {
            if(getPoints() < NORMAL_SMALL_LIMIT)
                return Game.HIT_COMMAND;
        }

        return Game.STAND_COMMAND;
    }

    private Integer aceStrategy(Card croupierCard)
    {
        if(croupierCard.getName() == "ACE" || croupierCard.getValue() >= ACE_CROUPIER_LIMIT)
        {
            if(getPoints() < ACE_BIG_LIMIT)
                return Game.HIT_COMMAND;
        }
        else
        {
            if(getPoints() < ACE_SMALL_LIMIT)
                return Game.HIT_COMMAND;
        }

        return Game.STAND_COMMAND;
    }
}
